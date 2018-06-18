package ru.noties.adapt;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

abstract class AdaptSource<T> {

    @NonNull
    abstract AdaptEntry<T> entry(@NonNull T item) throws AdaptRuntimeError;

    @NonNull
    abstract AdaptEntry<T> entry(int assignedViewType) throws AdaptRuntimeError;

    abstract int assignedViewType(@NonNull T item) throws AdaptRuntimeError;

    abstract int assignedViewType(@NonNull Class<? extends T> type) throws AdaptRuntimeError;


    @SuppressWarnings("unused")
    @NonNull
    static <T> AdaptSource.Builder<T> builder(@NonNull Class<T> baseItemType) {
        return new Builder<>(new KeyProvider());
    }

    static class KeyProvider {
        int provideKey(@NonNull Class<?> type) {
            return type.hashCode();
        }
    }

    static class Builder<T> {

        private final Map<Class<? extends T>, AdaptEntry<? extends T>> map = new HashMap<>(3);

        private final KeyProvider keyProvider;

        Builder(@NonNull KeyProvider keyProvider) {
            this.keyProvider = keyProvider;
        }

        // returns true if underlying collection has been modified, false if not (duplicate entry)
        <I extends T> boolean append(@NonNull Class<I> type, @NonNull AdaptEntry<I> entry) {
            return map.put(type, entry) == null;
        }

        @NonNull
        AdaptSource<T> build() {

            final int size = map.size();
            if (size == 0) {
                throw new AdaptConfigurationError("No entries were added");
            }

            // now, generate our source collection
            // iterate over classes (types), put them in an array, sort it

            final SparseArray<AdaptEntry<? extends T>> sparseArray = new SparseArray<>(size);
            for (Map.Entry<Class<? extends T>, AdaptEntry<? extends T>> entry : map.entrySet()) {
                sparseArray.append(keyProvider.provideKey(entry.getKey()), entry.getValue());
            }

            return new Impl<>(keyProvider, sparseArray);
        }
    }

    private static class Impl<T> extends AdaptSource<T> {

        private final KeyProvider keyProvider;
        private final SparseArray<AdaptEntry<? extends T>> sparseArray;

        Impl(@NonNull KeyProvider keyProvider, @NonNull SparseArray<AdaptEntry<? extends T>> sparseArray) {
            this.keyProvider = keyProvider;
            this.sparseArray = sparseArray;
        }

        @NonNull
        @Override
        AdaptEntry<T> entry(@NonNull T item) throws AdaptRuntimeError {
            //noinspection unchecked
            final AdaptEntry<T> entry = (AdaptEntry<T>) sparseArray.get(keyProvider.provideKey(item.getClass()));
            if (entry == null) {
                throw new AdaptRuntimeError("Specified type is not registered with this " +
                        "Adapt instance:" + item.getClass().getName());
            }
            return entry;
        }

        @NonNull
        @Override
        AdaptEntry<T> entry(int assignedViewType) throws AdaptRuntimeError {
            //noinspection unchecked
            final AdaptEntry<T> entry = (AdaptEntry<T>) sparseArray.get(assignedViewType);
            if (entry == null) {
                throw new AdaptRuntimeError("Specified viewType is not registered with this " +
                        "Adapt instance: " + assignedViewType);
            }
            return entry;
        }

        @Override
        int assignedViewType(@NonNull T item) throws AdaptRuntimeError {
            //noinspection unchecked
            return assignedViewType((Class<? extends T>) item.getClass());
        }

        @Override
        int assignedViewType(@NonNull Class<? extends T> type) throws AdaptRuntimeError {

            final int key = keyProvider.provideKey(type);

            // additionally validate that requested type is initially registered
            if (sparseArray.indexOfKey(key) < 0) {
                throw new AdaptRuntimeError("Specified type is not registered with this " +
                        "Adapt instance: " + type.getName());
            }

            return key;
        }
    }
}
