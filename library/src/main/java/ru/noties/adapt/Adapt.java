package ru.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.List;

/**
 * @see AdaptBuilder
 */
public abstract class Adapt<T> {

    /**
     * Factory method to obtain a {@link AdaptBuilder} instance
     *
     * @param baseItemType base type of items that {@link Adapt} will hold
     * @return {@link AdaptBuilder}
     */
    @NonNull
    public static <T> AdaptBuilder<T> builder(@NonNull Class<T> baseItemType) {
        return new AdaptBuilder<>(baseItemType);
    }

    /**
     * @return RecyclerView.Adapter. Please note that the same instance will be returned
     * between multiple calls
     */
    @NonNull
    public abstract RecyclerView.Adapter<? extends Holder> recyclerViewAdapter();

    /**
     * Triggers update of the underlying items collection. {@link Adapt} does not execute
     * any checks on supplied collection but instead redirects all the handling to a {@link AdaptUpdate}
     *
     * @param items collection of items to be displayed
     * @see AdaptUpdate
     * @see AdaptBuilder#adaptUpdate(AdaptUpdate)
     */
    public abstract void setItems(@Nullable List<? extends T> items);

    /**
     * @return collection of items that this instance hold. Returns unmodifiable or empty list
     * but never null
     */
    @NonNull
    public abstract List<? extends T> getItems();

    /**
     * Helper method to check if this instance doesn\'t contain any items.
     * Shorthand to: {@code adapt.getItemCount() == 0}
     *
     * @return a boolean indicating if this adapt instance doesn't hold any items
     */
    public abstract boolean isEmpty();

    /**
     * @return size of items that this instance hold
     */
    public abstract int getItemCount();

    /**
     * @param position index of an element to obtain
     * @return an item at specified `position`
     */
    @NonNull
    public abstract T getItem(int position);

    /**
     * @param position of an item
     * @return assigned item view type at specified position
     */
    public abstract int itemViewType(int position);

    /**
     * @param type of an item to obtain assigned view type for
     * @return assigned view type
     * @throws AdaptRuntimeError if specified type is not registered
     *                           via {@link AdaptBuilder#include(Class, ItemView)}
     *                           or {@link AdaptBuilder#include(Class, ItemView, ViewProcessor)}
     */
    public abstract int assignedViewType(@NonNull Class<? extends T> type) throws AdaptRuntimeError;

    /**
     * Helper method to iterate over a collection of items and call {@link #assignedViewType(Class)}
     * on each, ignoring runtime exception. If any of the items are not supported - whole list
     * is considered to be invalid (not-supported). In case of null (or empty) collection `true` will
     * be returned. If a collection contains `null`s then collection is also considered invalid
     *
     * @param items to validate
     * @return a boolean indicating if all supplied items are supported (registered) with this instance
     */
    public abstract boolean supportsItems(@Nullable List<? extends T> items);

    /**
     * Helper method to iterate over supplied item types and validate that all of them
     * have assigned view type via {@link #assignedViewType(Class)} call. Runtime exception is
     * ignored. Null of empty collection is considered valid. If collection contains null elements
     * it would be considered as invalid
     *
     * @param itemTypes collection of item types to validate
     * @return a boolean indicating if all supplied item types are supported (registered) with this instance
     */
    public abstract boolean supportsItemTypes(@Nullable Collection<Class<?>> itemTypes);
}
