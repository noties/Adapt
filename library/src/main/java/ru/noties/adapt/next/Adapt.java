package ru.noties.adapt.next;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Adapt extends RecyclerView.Adapter<Item.Holder> {

    public interface DataSetChangeHandler {

        void handleDataSetChange(
                @NonNull Adapt adapt,
                @Nullable ItemViewTypeFactory itemViewTypeFactory,
                @NonNull List<Item> oldList,
                @NonNull List<Item> newList);
    }

    // we must have enumeration of present viewTypes here
    interface ItemViewTypeFactory {

        @NonNull
        Item itemWithViewType(int viewType);

        @NonNull
        Set<Integer> viewTypes();
    }


    @NonNull
    public static Adapt create() {
        return create(NotifyDataSetChanged.create());
    }

    @NonNull
    public static Adapt create(@NonNull DataSetChangeHandler dataSetChangeHandler) {
        return new Adapt(dataSetChangeHandler);
    }


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
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        // we also could remove all decorations here
        this.recyclerView = null;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        // no op, we allow only stable ids and set this value in constructor
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
        onBindViewHolder(holder, position, Collections.emptyList());
    }

    @Override
    public void onBindViewHolder(
            @NonNull Item.Holder holder,
            int position,
            @NonNull List<Object> payloads) {

        // postpone removal of removed viewTypes item decorations until first `bind` call
        ensureRemovedViewTypesUnregisteredDecorators();

        //noinspection unchecked
        items.get(position).recyclerRender(holder, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).recyclerViewType();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id();
    }

    @Override
    public int getItemCount() {
        return safeList(items).size();
    }

    public void setItems(@Nullable List<Item> items) {
        setItems(items, null);
    }

    /**
     * Update items with precomputed {@link ItemViewTypeFactory}
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
    public List<Item> getItems() {
        return Collections.unmodifiableList(safeList(items));
    }

    /**
     * Can be used to dispatch adapter updates manually. Beware that after this method
     * one of the `notify*` methods must be called.
     */
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
     * Beware that after this method one of the `notify*` methods must be called.
     */
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

        itemDecoration = item.recyclerDecoration();

        if (itemDecoration == null) {
            // there is no decoration for this item, return
            return;
        }

        // it's not added in our tracked collection -> add it
        itemDecorations.append(viewType, itemDecoration);


        final RecyclerView recyclerView = ensureRecyclerView();

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

    @NonNull
    private static <T> List<T> safeList(@Nullable List<T> list) {
        return list != null
                ? list
                : Collections.<T>emptyList();
    }

    @NonNull
    public static ItemViewTypeFactory createItemViewFactory(@Nullable List<Item> items) {

        final SparseArray<Item> array = new SparseArray<>(3);
        final Set<Integer> viewTypes = new HashSet<>(3);

        int viewType;
        int index;

        for (Item item : safeList(items)) {
            viewType = item.recyclerViewType();
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
