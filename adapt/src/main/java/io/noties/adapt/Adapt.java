package io.noties.adapt;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.noties.adapt.ListUtils.safeList;

public class Adapt extends RecyclerView.Adapter<Item.Holder> {

    /**
     * @see NotifyDataSetChanged
     * @see DiffUtilDataSetChanged
     * @see AsyncDiffUtilDataSetChanged
     * @since 2.0.0
     */
    public interface DataSetChangeHandler {

        /**
         * Calculate diff between 2 datasets and deliver result. Before actual
         * update dispatch must call {@link Adapt#swapItemsBeforeUpdate(List)} or
         * {@link Adapt#swapItemsBeforeUpdate(List, ItemViewTypeFactory)}
         *
         * @param adapt               {@link Adapt}
         * @param itemViewTypeFactory {@link ItemViewTypeFactory}
         * @param oldList             old items
         * @param newList             new items
         * @see #createItemViewFactory(List)
         */
        void handleDataSetChange(
                @NonNull Adapt adapt,
                @Nullable ItemViewTypeFactory itemViewTypeFactory,
                @NonNull List<Item> oldList,
                @NonNull List<Item> newList);

        /**
         * Will be called when {@link #onDetachedFromRecyclerView(RecyclerView)} will be triggered.
         */
        void cancel();
    }

    /**
     * @see #createItemViewFactory(List)
     * @since 2.0.0
     */
    public interface ItemViewTypeFactory {

        @NonNull
        Item itemWithViewType(int viewType);

        @NonNull
        Set<Integer> viewTypes();
    }

    /**
     * Creates {@link Adapt} instance with {@link NotifyDataSetChanged} as {@link DataSetChangeHandler}
     *
     * @see NotifyDataSetChanged
     * @see #create(DataSetChangeHandler)
     * @since 2.0.0
     */
    @NonNull
    public static Adapt create() {
        return create(NotifyDataSetChanged.create());
    }

    /**
     * Creates {@link Adapt} instance specified {@link DataSetChangeHandler}
     *
     * @param dataSetChangeHandler {@link DataSetChangeHandler}
     * @see DataSetChangeHandler
     * @see NotifyDataSetChanged
     * @see DiffUtilDataSetChanged
     * @see AsyncDiffUtilDataSetChanged
     * @since 2.0.0
     */
    @NonNull
    public static Adapt create(@NonNull DataSetChangeHandler dataSetChangeHandler) {
        return new Adapt(dataSetChangeHandler);
    }

    private static final String STATE_KEY_RECYCLER = "adapt.state.RecyclerView";
    private static final String STATE_KEY_VIEW_STATE = "adapt.state.ViewState";

    private final DataSetChangeHandler dataSetChangeHandler;

    private final SparseArray<RecyclerView.ItemDecoration> itemDecorations =
            new SparseArray<>(0);

    private ItemViewTypeFactory itemViewTypeFactory;
    private LayoutInflater inflater;
    private List<Item> items;
    private Set<Integer> pendingViewTypesRemoved;

    private RecyclerView recyclerView;


    public Adapt(@NonNull DataSetChangeHandler dataSetChangeHandler) {
        this.dataSetChangeHandler = dataSetChangeHandler;
        super.setHasStableIds(true);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.inflater = ensureLayoutInflater(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {

        // trigger cancellation event for data-set-change handler (in case if asynchronous operations
        // involved)
        dataSetChangeHandler.cancel();

        // unregister all item decorations in case if recyclerView will be used
        // further with different adapter
        removeAllItemDecorations(recyclerView);

        this.recyclerView = null;
        this.inflater = null;
    }

    /**
     * Save RecyclerView state of attached/currently-visible children. To save state of all
     * children (including already detached) additionally use {@link ViewState#process(long, View)} inside
     * your {@link Item#render(Item.Holder)} method.
     *
     * @param bundle to save state to
     * @param key    key to use for state representation inside passed Bundle
     * @return flag indicating if state saving was successful
     * @see #onRestoreInstanceState(Bundle, String)
     * @since 2.3.0-SNAPSHOT
     */
    public boolean onSaveInstanceState(@NonNull Bundle bundle, @NonNull String key) {
        // we have here: recycler-view state (that we use)
        // we have state of children

        // if we are not attached -> nothing we can do
        final RecyclerView recyclerView = this.recyclerView;
        if (recyclerView == null) {
            return false;
        }

        // as ViewState saves state when view is detached -> we must iterate over current views and
        // save them explicitly

        View view;
        RecyclerView.ViewHolder holder;

        for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
            view = recyclerView.getChildAt(i);
            holder = recyclerView.findContainingViewHolder(view);
            if (holder != null) {
                ViewState.save(getItemId(holder.getAdapterPosition()), view);
            }
        }

        final SparseArray<Parcelable> recyclerState = new SparseArray<>(1);
        recyclerView.saveHierarchyState(recyclerState);

        final Bundle state = new Bundle();
        state.putSparseParcelableArray(STATE_KEY_RECYCLER, recyclerState);
        state.putBundle(STATE_KEY_VIEW_STATE, ViewState.onSaveInstanceState(recyclerView));

        bundle.putBundle(key, state);

        return true;
    }

    /**
     * @param bundle to restore state from or null
     * @param key    that was used to save state by {@link #onSaveInstanceState(Bundle, String)} call
     * @return flag indicating if state restoration did happen (bundle is not null and internal
     * state is present inside bundle)
     * @see #onSaveInstanceState(Bundle, String)
     * @since 2.3.0-SNAPSHOT
     */
    public boolean onRestoreInstanceState(@Nullable Bundle bundle, @NonNull String key) {

        final RecyclerView recyclerView = this.recyclerView;
        final Bundle state = bundle != null
                ? bundle.getBundle(key)
                : null;

        if (recyclerView == null
                || state == null) {
            return false;
        }

        final SparseArray<Parcelable> recyclerState = state.getSparseParcelableArray(STATE_KEY_RECYCLER);
        if (recyclerState != null) {
            recyclerView.restoreHierarchyState(recyclerState);
        }

        ViewState.onRestoreInstanceState(recyclerView, state.getBundle(STATE_KEY_VIEW_STATE));

        return true;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        // no op, we allow only stable ids and set this value in constructor
    }

    @NonNull
    public RecyclerView recyclerView() {
        return ensureRecyclerView();
    }

    @NonNull
    @Override
    public Item.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemViewType) {

        // obtain item by specified viewType
        final Item item = ensureItem(itemViewType);

        // obtain layoutInflater
        final LayoutInflater inflater = ensureLayoutInflater(viewGroup);

        // ensure that item decoration is registered for this item
        ensureItemDecoration(itemViewType, item);

        // create holder
        return item.createHolder(inflater, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull Item.Holder holder, int position) {

        // postpone removal of removed viewTypes item decorations until first `bind` call
        ensureRemovedViewTypesUnregisteredDecorators();

        //noinspection unchecked
        items.get(position).render(holder);
    }

    @Override
    public void onBindViewHolder(
            @NonNull Item.Holder holder,
            int position,
            @NonNull List<Object> payloads) {
        // by default Adapter does exactly that, but let's make it explicit
        onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id();
    }

    @Override
    public int getItemCount() {
        return safeList(items).size();
    }

    /**
     * @param position of item
     * @since 2.2.0
     */
    @NonNull
    public Item getItem(int position) {
        return items.get(position);
    }

    public void setItems(@Nullable List<Item> items) {
        setItems(items, null);
    }

    /**
     * Update items with precomputed {@link ItemViewTypeFactory}
     *
     * @see #createItemViewFactory(List)
     * @see ItemViewTypeFactory
     * @since 2.0.0
     */
    public void setItems(
            @Nullable List<Item> items,
            @Nullable ItemViewTypeFactory itemViewTypeFactory) {
        dataSetChangeHandler.handleDataSetChange(
                this,
                itemViewTypeFactory,
                safeList(this.items),
                safeList(items));
    }

    @NonNull
    public List<Item> getCurrentItems() {
        return safeList(items);
    }

    /**
     * Can be used to dispatch adapter updates manually. Beware that after this method
     * one of the \'notify*\' methods must be called.
     *
     * @since 2.0.0
     */
    @SuppressWarnings("unused")
    @NonNull
    @CheckResult(suggest = "#notifyDataSetChanged()," +
            "#notifyItemChanged(int)," +
            "#notifyItemChanged(int, Object)," +
            "#notifyItemInserted(int)," +
            "#notifyItemMoved(int, int)," +
            "#notifyItemRangeChanged(int, int)," +
            "#notifyItemRangeInserted(int, int)," +
            "#notifyItemRangeRemoved(int, int)," +
            "#notifyItemRemoved(int)")
    public Adapt swapItemsBeforeUpdate(@Nullable List<Item> items) {
        return swapItemsBeforeUpdate(items, null);
    }

    /**
     * Can be used to dispatch adapter updates manually (or in {@link DataSetChangeHandler}.
     * Beware that after this method one of the \'notify*\' methods must be called.
     *
     * @since 2.0.0
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    @CheckResult(suggest = "#notifyDataSetChanged()," +
            "#notifyItemChanged(int)," +
            "#notifyItemChanged(int, Object)," +
            "#notifyItemInserted(int)," +
            "#notifyItemMoved(int, int)," +
            "#notifyItemRangeChanged(int, int)," +
            "#notifyItemRangeInserted(int, int)," +
            "#notifyItemRangeRemoved(int, int)," +
            "#notifyItemRemoved(int)")
    public Adapt swapItemsBeforeUpdate(
            @Nullable List<Item> items,
            @Nullable ItemViewTypeFactory itemViewTypeFactory) {

        this.items = items;

        final ItemViewTypeFactory factory = itemViewTypeFactory != null
                ? itemViewTypeFactory
                : createItemViewFactory(items);

        // if we have previous factory -> extract removed items (to remove itemDecorations)
        this.pendingViewTypesRemoved = extractRemovedViewTypes(
                this.pendingViewTypesRemoved,
                this.itemViewTypeFactory,
                factory);

        // apply factory
        this.itemViewTypeFactory = factory;

        return this;
    }

    @NonNull
    private Item ensureItem(int viewType) {

        final ItemViewTypeFactory itemViewTypeFactory = this.itemViewTypeFactory;
        if (itemViewTypeFactory == null) {
            throw AdaptException.create("ItemViewTypeFactory is not initialized properly");
        }

        final Item item = itemViewTypeFactory.itemWithViewType(viewType);

        //noinspection ConstantConditions
        if (item == null) {
            throw AdaptException.create("ItemViewTypeFactory returned null " +
                    "item for viewType: %d", viewType);
        }

        return item;
    }

    @NonNull
    private LayoutInflater ensureLayoutInflater(@NonNull ViewGroup group) {
        LayoutInflater inflater = this.inflater;
        if (inflater == null) {
            inflater = this.inflater = LayoutInflater.from(group.getContext());
        }
        return inflater;
    }

    @NonNull
    private RecyclerView ensureRecyclerView() {
        final RecyclerView recyclerView = this.recyclerView;
        if (recyclerView == null) {
            throw AdaptException.create("Adapt instance is not attached to RecyclerView");
        }
        return recyclerView;
    }

    private void ensureItemDecoration(int viewType, @NonNull Item item) {

        RecyclerView.ItemDecoration itemDecoration = itemDecorations.get(viewType);

        // if it's already added -> return
        if (itemDecoration != null) {
            return;
        }

        final RecyclerView recyclerView = ensureRecyclerView();

        itemDecoration = item.recyclerDecoration(recyclerView);

        if (itemDecoration == null) {
            // there is no decoration for this item, return
            return;
        }

        // it's not added in our tracked collection -> add it
        itemDecorations.append(viewType, itemDecoration);

        if (!recyclerView.isComputingLayout()) {
            recyclerView.addItemDecoration(itemDecoration);
        } else {
            final RecyclerView.ItemDecoration decoration = itemDecoration;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (!recyclerView.isComputingLayout()) {
                        recyclerView.addItemDecoration(decoration);
                    } else {
                        recyclerView.post(this);
                    }
                }
            });
        }
    }

    private void ensureRemovedViewTypesUnregisteredDecorators() {

        final Set<Integer> pendingViewTypesRemoved = this.pendingViewTypesRemoved;

        if (pendingViewTypesRemoved == null
                || pendingViewTypesRemoved.isEmpty()) {
            return;
        }

        final RecyclerView recyclerView = ensureRecyclerView();

        final Runnable action = new Runnable() {
            @Override
            public void run() {

                if (recyclerView.isComputingLayout()) {
                    recyclerView.post(this);
                    return;
                }

                RecyclerView.ItemDecoration decoration;
                int intValue;

                for (Integer viewType : pendingViewTypesRemoved) {
                    intValue = viewType;
                    decoration = itemDecorations.get(intValue);
                    if (decoration != null) {
                        itemDecorations.remove(intValue);
                        recyclerView.removeItemDecoration(decoration);
                    }
                }

                pendingViewTypesRemoved.clear();
            }
        };
        action.run();
    }

    private void removeAllItemDecorations(@NonNull final RecyclerView recyclerView) {

        // clean-up this collection (we no longer need it)
        if (pendingViewTypesRemoved != null) {
            pendingViewTypesRemoved.clear();
        }

        // no need to do anything if we have no decorations registered
        if (itemDecorations.size() == 0) {
            return;
        }

        // it is still possible that RecyclerView is going through `layout` phases, post until it is not
        final Runnable action = new Runnable() {
            @Override
            public void run() {
                if (recyclerView.isComputingLayout()) {
                    recyclerView.post(this);
                } else {
                    for (int i = 0, size = itemDecorations.size(); i < size; i++) {
                        recyclerView.removeItemDecoration(itemDecorations.valueAt(i));
                    }
                    itemDecorations.clear();
                }
            }
        };
        action.run();
    }

    @Nullable
    private static Set<Integer> extractRemovedViewTypes(
            @Nullable Set<Integer> pendingViewTypesRemoved,
            @Nullable ItemViewTypeFactory previous,
            @NonNull ItemViewTypeFactory current) {

        if (previous == null) {
            // nothing to do, if previous is null (first call to setItems)
            return pendingViewTypesRemoved;
        }

        final Set<Integer> set = new HashSet<>(previous.viewTypes());

        // hm, it's interesting, what if `pendingViewTypesRemoved` is NOT empty at this point?
        // this would mean that previous clean-up operation hasn't completed
        if (pendingViewTypesRemoved != null) {
            // so, if we have pending items, we will add them to _previous_ set,
            // this way we will preserve items that were previously marked for deletion, but present in
            // new set
            set.addAll(pendingViewTypesRemoved);
        }

        set.removeAll(current.viewTypes());

        return set.isEmpty()
                ? null
                : set;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static ItemViewTypeFactory createItemViewFactory(@Nullable List<Item> items) {

        final SparseArray<Item> array = new SparseArray<>(3);
        final Set<Integer> viewTypes = new HashSet<>(3);

        int viewType;
        int index;

        for (Item item : safeList(items)) {
            viewType = item.viewType();
            index = array.indexOfKey(viewType);
            if (index < 0) {
                array.append(viewType, item);
                viewTypes.add(viewType);
            }
        }

        return new ItemViewTypeFactoryImpl(array, Collections.unmodifiableSet(viewTypes));
    }

    private static class ItemViewTypeFactoryImpl implements ItemViewTypeFactory {

        private final SparseArray<Item> items;
        private final Set<Integer> viewTypes;

        ItemViewTypeFactoryImpl(@NonNull SparseArray<Item> items, @NonNull Set<Integer> viewTypes) {
            this.items = items;
            this.viewTypes = viewTypes;
        }

        @NonNull
        @Override
        public Item itemWithViewType(int viewType) {
            return items.get(viewType);
        }

        @NonNull
        @Override
        public Set<Integer> viewTypes() {
            return viewTypes;
        }
    }
}
