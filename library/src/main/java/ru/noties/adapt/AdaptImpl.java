package ru.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public RecyclerView.Adapter<? extends Holder> recyclerViewAdapter() {
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
    public int itemViewType(int position) {
        return itemViewType(getItem(position));
    }

    @Override
    public int assignedViewType(@NonNull Class<? extends T> type) {
        return adaptSource.assignedViewType(type);
    }

    @Override
    public boolean supportsItems(@Nullable List<? extends T> items) {

        if (items == null
                || items.size() == 0) {
            return true;
        }

        final Set<Class<?>> set = new HashSet<>(3);
        for (T item : items) {

            // null elements are not supported
            if (item == null) {
                return false;
            }

            set.add(item.getClass());
        }

        return supportsItemTypes(set);
    }

    @Override
    public boolean supportsItemTypes(@Nullable Collection<Class<?>> itemTypes) {

        if (itemTypes == null
                || itemTypes.size() == 0) {
            return true;
        }

        // maybe we could execute a check if supplied collection is a set
        // if not -> copy its contents to a new set (to eliminate duplicates)

        for (Class<?> type : itemTypes) {

            // cannot process nulls
            if (type == null) {
                return false;
            }

            try {
                //noinspection unchecked
                adaptSource.assignedViewType((Class<? extends T>) type);
            } catch (AdaptRuntimeError e) {
                return false;
            }
        }

        return true;
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

        @Override
        public void onViewRecycled(@NonNull Holder holder) {
            final int position = holder.getAdapterPosition();
            if (position > -1) {
                itemView(getItem(position)).onViewRecycled(holder);
            }
        }
    }
}
