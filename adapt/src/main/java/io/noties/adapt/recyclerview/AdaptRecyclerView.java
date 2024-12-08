package io.noties.adapt.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import io.noties.adapt.Adapt;
import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.util.ListUtils;

public class AdaptRecyclerView implements Adapt {

    public interface DataSetChangeResultCallback {
        @NonNull
        RecyclerView.Adapter<?> applyItemsChange(@NonNull List<Item<?>> items);
    }

    public interface DataSetChangeHandler {
        void handleDataSetChange(
                @NonNull List<Item<?>> oldList,
                @NonNull List<Item<?>> newList,
                @NonNull DataSetChangeResultCallback callback
        );
    }

    public interface Configuration {
        /**
         * by default {@code true}
         *
         * @param hasStableIds indicates if adapter has stable ids
         */
        @NonNull
        Configuration hasStableIds(boolean hasStableIds);

        /**
         * by default {@link NotifyDataSetChangedHandler}
         *
         * @see NotifyDataSetChangedHandler
         * @see DiffUtilDataSetChangedHandler
         */
        @NonNull
        Configuration dataSetChangeHandler(@NonNull DataSetChangeHandler dataSetChangeHandler);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    @NonNull
    public static AdaptRecyclerView init(@NonNull RecyclerView recyclerView) {
        final AdaptRecyclerView adaptRecyclerView = new AdaptRecyclerView(recyclerView, new ConfigurationImpl());
        recyclerView.setAdapter(adaptRecyclerView.adapter());
        return adaptRecyclerView;
    }

    @NonNull
    public static AdaptRecyclerView init(@NonNull RecyclerView recyclerView, @NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        final AdaptRecyclerView adaptRecyclerView = new AdaptRecyclerView(recyclerView, configuration);
        recyclerView.setAdapter(adaptRecyclerView.adapter());
        return adaptRecyclerView;
    }

    @NonNull
    public static AdaptRecyclerView create() {
        return new AdaptRecyclerView(null, new ConfigurationImpl());
    }

    @NonNull
    public static AdaptRecyclerView create(@NonNull Configurator configurator) {
        final ConfigurationImpl configuration = new ConfigurationImpl();
        configurator.configure(configuration);
        return new AdaptRecyclerView(null, configuration);
    }

    /**
     * @since $UNRELEASED;
     */
    @Nullable
    public static AdaptRecyclerView find(@NonNull RecyclerView recyclerView) {
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter instanceof AdapterImpl) {
            return ((AdapterImpl) adapter).adaptRecyclerView();
        }
        return null;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final Item.Holder holder;

        public ItemViewHolder(@NonNull Item.Holder holder) {
            super(holder.itemView());
            this.holder = holder;
        }

        @NonNull
        public Item.Holder holder() {
            return holder;
        }
    }

    private final RecyclerView recyclerView;
    private final ConfigurationImpl configuration;
    private final AdapterImpl adapter;

    private final DataSetChangeResultCallback changeResultCallback = new DataSetChangeResultCallback() {
        @NonNull
        @Override
        public RecyclerView.Adapter<?> applyItemsChange(@NonNull List<Item<?>> items) {

            AdaptRecyclerView.this.items = items;

            // this is where we clear currently built factory and start filling it again
            adapter.clearFactory();

            return adapter();
        }
    };

    private final CopyOnWriteArrayList<OnItemsChangedListener> listeners = new CopyOnWriteArrayList<>();

    private List<Item<? extends Item.Holder>> items;

    AdaptRecyclerView(@Nullable final RecyclerView recyclerView, @NonNull ConfigurationImpl configuration) {
        this.recyclerView = recyclerView;
        this.configuration = configuration;
        this.adapter = new AdapterImpl();

        adapter.setHasStableIds(configuration.hasStableIds);
    }

    @Nullable
    public RecyclerView recyclerView() {
        final RecyclerView recyclerView = this.recyclerView;
        if (recyclerView != null) {
            return recyclerView;
        }
        return adapter.attachedRecyclerView();
    }

    @NonNull
    public Adapter<? extends RecyclerView.ViewHolder> adapter() {
        return adapter;
    }

    @NonNull
    @Override
    public List<Item<?>> items() {
        return ListUtils.freeze(items);
    }

    @Override
    public void setItems(@Nullable List<Item<?>> items) {
        configuration.dataSetChangeHandler.handleDataSetChange(
                ListUtils.freeze(this.items),
                ListUtils.freeze(items),
                changeResultCallback
        );

        triggerOnItemsChanged(items);
    }

    @Override
    public void notifyAllItemsChanged() {
        // let change handler process notification
        setItems(items);
    }

    @Override
    public void notifyItemChanged(@NonNull Item<?> item) {
        final int index = ListUtils.freeze(items).indexOf(item);
        if (index >= 0) {
            adapter.notifyItemChanged(index);
        }
    }

    @Override
    public void registerOnItemsChangedListener(@NonNull OnItemsChangedListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterOnItemsChangedListener(@NonNull OnItemsChangedListener listener) {
        listeners.remove(listener);
    }

    private void triggerOnItemsChanged(@Nullable List<Item<?>> items) {
        for (OnItemsChangedListener listener : listeners) {
            listener.onItemsChanged(items);
        }
    }

    public static abstract class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        @NonNull
        public abstract Item<?> getItem(int position);
    }

    private class AdapterImpl extends Adapter<ItemViewHolder> {

        private final Map<Integer, Item<?>> factory = new HashMap<>();

        private LayoutInflater inflater;

        @Nullable
        private WeakReference<RecyclerView> attachedRecyclerView = null;

        @NonNull
        AdaptRecyclerView adaptRecyclerView() {
            return AdaptRecyclerView.this;
        }

        @Nullable
        RecyclerView attachedRecyclerView() {
            final WeakReference<RecyclerView> current = attachedRecyclerView;
            return current != null ? current.get() : null;
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            attachedRecyclerView = new WeakReference<>(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            final WeakReference<RecyclerView> current = attachedRecyclerView;
            final RecyclerView currentRecyclerView = current != null ? current.get() : null;
            if (currentRecyclerView == recyclerView) {
                attachedRecyclerView = null;
            }
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Item<?> item = factory.get(viewType);
            if (item == null) {
                throw AdaptException.create(
                        "Item with a viewType:" + viewType + " not found, factory:" + factory
                );
            }

            LayoutInflater inflater = this.inflater;
            if (inflater == null) {
                this.inflater = inflater = LayoutInflater.from(parent.getContext());
            }

            final Item.Holder holder = item.createHolder(inflater, parent);
            holder.setAdapt(AdaptRecyclerView.this);

            return new ItemViewHolder(holder);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            //noinspection rawtypes
            final Item item = items.get(position);
            //noinspection unchecked
            item.bind(holder.holder());
        }


        @Override
        public int getItemCount() {
            return ListUtils.size(items);
        }

        @Override
        public int getItemViewType(int position) {
            // lazily fill the factory
            final Item<?> item = items.get(position);
            final int viewType = item.viewType();
            if (factory.get(viewType) == null) {
                factory.put(viewType, item);
            }
            return viewType;
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).id();
        }

        @NonNull
        @Override
        public Item<?> getItem(int position) {
            return items.get(position);
        }

        void clearFactory() {
            factory.clear();
        }
    }

    private static class ConfigurationImpl implements Configuration {

        boolean hasStableIds = true;
        DataSetChangeHandler dataSetChangeHandler = NotifyDataSetChangedHandler.create();

        @NonNull
        @Override
        public Configuration hasStableIds(boolean hasStableIds) {
            this.hasStableIds = hasStableIds;
            return this;
        }

        @NonNull
        @Override
        public Configuration dataSetChangeHandler(@NonNull DataSetChangeHandler dataSetChangeHandler) {
            this.dataSetChangeHandler = dataSetChangeHandler;
            return this;
        }
    }
}
