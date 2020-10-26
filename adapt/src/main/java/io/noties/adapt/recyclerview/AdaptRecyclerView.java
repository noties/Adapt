package io.noties.adapt.recyclerview;

import android.content.Context;
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

        @NonNull
        Configuration dataSetChangeHandler(@NonNull DataSetChangeHandler dataSetChangeHandler);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    public static final long NO_ID = RecyclerView.NO_ID;

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

    @SuppressWarnings("rawtypes")
    public static int assignedViewType(@NonNull Class<? extends Item> type) {
        return type.getName().hashCode();
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
                store.put(assignedViewType(item.getClass()), item);
            }

            AdaptRecyclerView.this.items = items;

            return adapter();
        }
    };

    private List<Item<? extends Item.Holder>> items;

    AdaptRecyclerView(@NonNull final RecyclerView recyclerView, @NonNull ConfigurationImpl configuration) {
        this.recyclerView = recyclerView;
        this.configuration = configuration;
        this.adapter = new AdapterImpl(recyclerView.getContext());

        adapter.setHasStableIds(configuration.hasStableIds);
    }

    @NonNull
    public RecyclerView recyclerView() {
        return recyclerView;
    }

    @NonNull
    public Adapter<? extends RecyclerView.ViewHolder> adapter() {
        return adapter;
    }

    @NonNull
    @Override
    public List<Item<? extends Item.Holder>> items() {
        return ListUtils.freeze(items);
    }

    @Override
    public void setItems(@Nullable List<Item<? extends Item.Holder>> items) {
        configuration.dataSetChangeHandler.handleDataSetChange(
                ListUtils.freeze(this.items),
                ListUtils.freeze(items),
                changeResultCallback
        );
    }

    public static abstract class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        @NonNull
        public abstract Item<? extends Item.Holder> getItem(int position);
    }

    private class AdapterImpl extends Adapter<ItemViewHolder> {

        private final LayoutInflater inflater;

        AdapterImpl(@NonNull Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Item<?> firstItem = store.get(viewType);
            if (firstItem == null) {
                throw AdaptException.create("Unexpected viewType: %d", viewType);
            }
            return new ItemViewHolder(firstItem.createHolder(inflater, parent));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            //noinspection rawtypes
            final Item item = items.get(position);
            //noinspection unchecked
            item.render(holder.holder());
        }


        @Override
        public int getItemCount() {
            return ListUtils.size(items);
        }

        @Override
        public int getItemViewType(int position) {
            return assignedViewType(items.get(position).getClass());
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).id();
        }

        @NonNull
        @Override
        public Item<? extends Item.Holder> getItem(int position) {
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
