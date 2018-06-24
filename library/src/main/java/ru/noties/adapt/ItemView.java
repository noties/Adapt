package ru.noties.adapt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class ItemView<T, H extends Holder> {

    /**
     * @param inflater LayoutInflater
     * @param parent   ViewGroup to which newly created view _will_ be attached
     * @return an instance of H (Holder)
     */
    @NonNull
    public abstract H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void bindHolder(@NonNull H holder, @NonNull T item);

    @SuppressWarnings("unused")
    public void bindHolder(@NonNull H holder, @NonNull T item, @NonNull List<Object> payloads) {
        bindHolder(holder, item);
    }

    /**
     * @param item to obtain id from
     * @return id of an item, by default `object#hashcode()` is used
     */
    public long itemId(@NonNull T item) {
        return item.hashCode();
    }

    /**
     * This method will be redirected from `RecyclerView.Adapter#onViewRecycled`
     *
     * @param holder of this ItemView
     */
    public void onViewRecycled(@NonNull H holder) {
        // no op
    }

    /**
     * Helper method to obtain Context
     */
    @SuppressWarnings("unused")
    @NonNull
    protected Context context(@NonNull Holder holder) {
        return holder.itemView.getContext();
    }
}
