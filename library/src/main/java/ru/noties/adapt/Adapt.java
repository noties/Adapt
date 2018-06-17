package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class Adapt<T> {

    @NonNull
    public static <T> AdaptBuilder<T> builder(@NonNull Class<T> baseItemType) {
        return new AdaptBuilder<>(baseItemType);
    }

    @NonNull
    public abstract RecyclerView.Adapter<? extends Holder> toRecyclerViewAdapter();

    public abstract void setItems(@Nullable List<? extends T> items);

    @NonNull
    public abstract List<? extends T> getItems();

    public abstract boolean isEmpty();

    public abstract int getItemCount();

    @NonNull
    public abstract T getItem(int position);

    public abstract int assignedViewType(@NonNull Class<? extends T> type);
}
