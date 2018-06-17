package ru.noties.adapt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public abstract class ItemView<T, H extends Holder> {

    @NonNull
    public abstract H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void bindHolder(@NonNull H holder, @NonNull T item);

    public void bindHolder(@NonNull H holder, @NonNull T item, @NonNull List<Object> payloads) {
        bindHolder(holder, item);
    }

    public long itemId(@NonNull T item) {
        return item.hashCode();
    }

    @SuppressWarnings("unused")
    @NonNull
    protected Context context(@NonNull Holder holder) {
        return holder.itemView.getContext();
    }
}
