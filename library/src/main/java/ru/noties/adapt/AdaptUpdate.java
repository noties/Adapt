package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public interface AdaptUpdate<T> {

    interface Source<T> {

        void updateItems(@Nullable List<? extends T> items);

        @NonNull
        RecyclerView.Adapter<? extends RecyclerView.ViewHolder> recyclerViewAdapter();
    }

    void updateItems(
            @NonNull Source<T> source,
            @Nullable List<? extends T> oldItems,
            @Nullable List<? extends T> newItems);
}
