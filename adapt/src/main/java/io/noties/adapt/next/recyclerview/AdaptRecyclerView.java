package io.noties.adapt.next.recyclerview;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.noties.adapt.next.Adapt;
import io.noties.adapt.next.AdaptException;
import io.noties.adapt.next.Item;
import io.noties.adapt.next.utils.ListUtils;

public class AdaptRecyclerView implements Adapt {

    public interface DataSetChangeResultCallback {
        @Nullable
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
        @NonNull
        Configuration hasStableIds(boolean hasStableIds);

        @NonNull
        Configuration dataSetChangeHandler(@NonNull DataSetChangeHandler dataSetChangeHandler);
    }

    public interface Configurator {
        void configure(@NonNull Configuration configuration);
    }

    public static final long NO_ID = RecyclerView.NO_ID;

//    @NonNull
//    public static AdaptRecyclerView create(@NonNull RecyclerView recyclerView) {
//        return new AdaptRecyclerView(recyclerView, new ConfigurationImpl());
//    }
//
//    @NonNull
//    public static AdaptRecyclerView create(@NonNull RecyclerView recyclerView, @NonNull Configurator configurator) {
//        final ConfigurationImpl configuration = new ConfigurationImpl();
//        configurator.configure(configuration);
//        return new AdaptRecyclerView(recyclerView, configuration);
//    }

    @NonNull
    public static AdaptRecyclerView create(@NonNull RecyclerView recyclerView) {
        final AdaptRecyclerView adaptRecyclerView = new AdaptRecyclerView(recyclerView, new ConfigurationImpl());
        recyclerView.setAdapter(adaptRecyclerView.adapter());
        return adaptRecyclerView;
    }

    @NonNull
    public static AdaptRecyclerView create(@NonNull RecyclerView recyclerView, @NonNull Configurator configurator) {
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
    private final RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;

    // special storage to keep track of items and view-types
    private final SparseArray<Item<?>> store = new SparseArray<>();

    private final DataSetChangeResultCallback changeResultCallback = new DataSetChangeResultCallback() {
        @Nullable
        @Override
        public RecyclerView.Adapter<?> applyItemsChange(@NonNull List<Item<?>> items) {
            // check if we are attached (in case if data set change handler is asynchronous)
            if (!recyclerView.isAttachedToWindow()) {
                return null;
            }

            // release old items from referencing (maybe change to hash map for better performance)
            store.clear();

            for (Item<?> item : items) {
                final int viewType = assignedViewType(item.getClass());
                if (store.indexOfKey(viewType) < 0) {
                    store.put(viewType, item);
                }
            }

            AdaptRecyclerView.this.items = items;
            return adapter();
        }
    };

    private List<Item<? extends Item.Holder>> items;

    AdaptRecyclerView(@NonNull RecyclerView recyclerView, @NonNull ConfigurationImpl configuration) {
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
    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter() {
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

    private class AdapterImpl extends RecyclerView.Adapter<ItemViewHolder> {

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
//            // todo: it is not clear if we must still implement this method
//            throw new RuntimeException("onBindViewHolder without payloads called");

            //noinspection rawtypes
            final Item item = items.get(position);
            //noinspection unchecked
            item.render(holder.holder());
        }

        // diff util must provide a way to supply payloads, but we ignore partial update
//        @Override
//        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull List<Object> payloads) {
//            //noinspection rawtypes
//            final Item item = items.get(position);
//            if (item instanceof RecyclerViewItem) {
//                // 2 rawtypes?
//                //noinspection unchecked,rawtypes,rawtypes
//                ((RecyclerViewItem) item).recyclerViewRender(holder, holder.holder(), payloads);
//            } else {
//                //noinspection unchecked
//                item.render(holder.holder());
//            }
//        }

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
    }

    private static class ConfigurationImpl implements Configuration {

        boolean hasStableIds = false;
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
