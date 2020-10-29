package ru.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NotifyDataSetChangedUpdate<T> implements AdaptUpdate<T> {

    @NonNull
    public static <T> NotifyDataSetChangedUpdate<T> create() {
        return new NotifyDataSetChangedUpdate<>();
    }

    @Override
    public void updateItems(
            @NonNull Source<T> source,
            @Nullable List<? extends T> oldItems,
            @Nullable List<? extends T> newItems) {
        source.updateItems(newItems);
        source.recyclerViewAdapter().notifyDataSetChanged();
    }
}
