package io.noties.adapt.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Adapter<? extends RecyclerView.ViewHolder> adapter;

    // special storage to keep track of items and view-types
    private final Map<Integer, Item<?>> store = new HashMap<>(3);

    private final DataSetChangeResultCallback changeResultCallback = new DataSetChangeResultCallback() {
        @NonNull
        @Override
        public RecyclerView.Adapter<?> applyItemsChange(@NonNull List<Item<?>> items) {

            // release old items from referencing
            store.clear();

            for (Item<?> item : items) {
                store.put(item.viewType(), item);
            }

            AdaptRecyclerView.this.items = items;

            return adapter();
        }
    };

    private List<Item<? extends Item.Holder>> items;

    AdaptRecyclerView(@Nullable final RecyclerView recyclerView, @NonNull ConfigurationImpl configuration) {
        this.recyclerView = recyclerView;
        this.configuration = configuration;
        this.adapter = new AdapterImpl();

        adapter.setHasStableIds(configuration.hasStableIds);
    }

    @Nullable
    public RecyclerView recyclerView() {
        return recyclerView;
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

    public static abstract class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        @NonNull
        public abstract Item<?> getItem(int position);
    }

    private class AdapterImpl extends Adapter<ItemViewHolder> {

        private LayoutInflater inflater;

        @NonNull
        AdaptRecyclerView adaptRecyclerView() {
            return AdaptRecyclerView.this;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Item<?> firstItem = store.get(viewType);
            if (firstItem == null) {
                throw AdaptException.create("Unexpected viewType: " + viewType);
            }

            LayoutInflater inflater = this.inflater;
            if (inflater == null) {
                this.inflater = inflater = LayoutInflater.from(parent.getContext());
            }

            final Item.Holder holder = firstItem.createHolder(inflater, parent);
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
            return items.get(position).viewType();
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
