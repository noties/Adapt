package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.Collections;
import java.util.List;

public class DiffUtilUpdate<T> implements AdaptUpdate<T> {

    @SuppressWarnings("unused")
    @NonNull
    public static <T> DiffUtilUpdate<T> create(@NonNull Callback<T> callback) {
        return new DiffUtilUpdate<>(callback);
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <T> DiffUtilUpdate<T> create(@NonNull final AreItemsTheSame<T> areItemsTheSame) {
        return new DiffUtilUpdate<>(new Callback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return areItemsTheSame.areItemsTheSame(oldItem, newItem);
            }
        });
    }

    public interface AreItemsTheSame<T> {
        boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem);
    }

    @SuppressWarnings("WeakerAccess")
    public static abstract class Callback<T> {

        public abstract boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem);

        @SuppressWarnings("unused")
        public boolean detectMoves(
                @NonNull List<? extends T> oldItems,
                @NonNull List<? extends T> newItems) {
            return true;
        }

        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.equals(newItem);
        }

        @SuppressWarnings("unused")
        @Nullable
        public Object getChangePayload(@NonNull T oldItem, @NonNull T newItem) {
            return null;
        }
    }

    private final Callback<T> callback;

    @SuppressWarnings("WeakerAccess")
    DiffUtilUpdate(@NonNull Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void updateItems(
            @NonNull Source<T> source,
            @Nullable List<? extends T> oldItems,
            @Nullable List<? extends T> newItems) {

        oldItems = makeNonNull(oldItems);
        newItems = makeNonNull(newItems);

        final boolean detectMoves = callback.detectMoves(oldItems, newItems);
        final DiffUtil.Callback diffUtilCallback =
                new DiffUtilCallback<>(callback, oldItems, newItems);

        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffUtilCallback, detectMoves);
        source.updateItems(newItems);
        result.dispatchUpdatesTo(source.recyclerViewAdapter());
    }

    @NonNull
    private static <T> List<? extends T> makeNonNull(@Nullable List<? extends T> list) {
        return list == null
                ? Collections.<T>emptyList()
                : list;
    }

    private static class DiffUtilCallback<T> extends DiffUtil.Callback {

        private final Callback<T> callback;
        private final List<? extends T> oldItems;
        private final List<? extends T> newItems;

        DiffUtilCallback(
                @NonNull Callback<T> callback,
                @NonNull List<? extends T> oldItems,
                @NonNull List<? extends T> newItems) {
            this.callback = callback;
            this.oldItems = oldItems;
            this.newItems = newItems;
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return callback.areItemsTheSame(
                    oldItems.get(oldItemPosition),
                    newItems.get(newItemPosition)
            );
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return callback.areContentsTheSame(
                    oldItems.get(oldItemPosition),
                    newItems.get(newItemPosition)
            );
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return callback.getChangePayload(
                    oldItems.get(oldItemPosition),
                    newItems.get(newItemPosition)
            );
        }
    }
}
