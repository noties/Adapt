package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

class AdaptImpl<T> extends Adapt<T> implements AdaptUpdate.Source<T> {

    private final RecyclerViewAdapter adapter = new RecyclerViewAdapter();

    private final AdaptSource<T> adaptSource;

    private final AdaptUpdate<T> adaptUpdate;

    @Nullable
    private LayoutInflater layoutInflater;

    private List<? extends T> items;

    @SuppressWarnings("unused")
    AdaptImpl(
            @NonNull Class<T> baseItemType,
            @Nullable LayoutInflater layoutInflater,
            boolean hasStableIds,
            @NonNull AdaptSource<T> adaptSource,
            @NonNull AdaptUpdate<T> adaptUpdate
    ) {
        this.layoutInflater = layoutInflater;
        this.adapter.setHasStableIds(hasStableIds);
        this.adaptSource = adaptSource;
        this.adaptUpdate = adaptUpdate;
    }

    @NonNull
    @Override
    public RecyclerView.Adapter<? extends Holder> toRecyclerViewAdapter() {
        return adapter;
    }

    @Override
    public void setItems(@Nullable List<? extends T> items) {
        adaptUpdate.updateItems(this, this.items, items);
    }

    @Override
    public void updateItems(@Nullable List<? extends T> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> recyclerViewAdapter() {
        return adapter;
    }

    @NonNull
    @Override
    public List<? extends T> getItems() {
        return items != null
                ? Collections.unmodifiableList(items)
                : Collections.<T>emptyList();
    }

    @Override
    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public int getItemCount() {
        return items != null
                ? items.size()
                : 0;
    }

    @NonNull
    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public int assignedViewType(@NonNull Class<? extends T> type) {
        return adaptSource.assignedViewType(type);
    }

    @NonNull
    private ItemView<T, Holder> itemView(int itemViewType) {
        return adaptSource.entry(itemViewType).itemView;
    }

    @NonNull
    private ItemView<T, Holder> itemView(@NonNull T item) {
        return adaptSource.entry(item).itemView;
    }

    private int itemViewType(@NonNull T item) {
        return adaptSource.assignedViewType(item);
    }

    @Nullable
    private ViewProcessor<T> viewProcessor(@NonNull T item) {
        return adaptSource.entry(item).viewProcessor;
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (layoutInflater == null) {
                layoutInflater = LayoutInflater.from(parent.getContext());
            }
            //noinspection ConstantConditions
            return itemView(viewType).createHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            onBindViewHolder(holder, position, Collections.emptyList());
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position, @NonNull List<Object> payloads) {

            final T item = items.get(position);

            final ItemView<T, Holder> itemView = itemView(item);
            itemView.bindHolder(holder, item, payloads);

            final ViewProcessor<T> viewProcessor = viewProcessor(item);
            if (viewProcessor != null) {
                viewProcessor.process(item, holder.itemView);
            }
        }

        @Override
        public int getItemCount() {
            return AdaptImpl.this.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            return itemViewType(getItem(position));
        }

        @Override
        public long getItemId(int position) {
            final T item = getItem(position);
            return itemView(item).itemId(item);
        }
    }
}
