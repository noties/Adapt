package ru.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * An interface to trigger item updates
 *
 * @see NotifyDataSetChangedUpdate
 * @see DiffUtilUpdate
 */
public interface AdaptUpdate<T> {

    /**
     * Small abstraction to not expose implementation details about {@link Adapt}
     */
    interface Source<T> {

        /**
         * An implementation of {@link AdaptUpdate} must call this method in order
         * to update items of {@link Adapt}
         *
         * @param items to be applied as new data set for an {@link Adapt}
         */
        void updateItems(@Nullable List<? extends T> items);

        @NonNull
        RecyclerView.Adapter<? extends Holder> recyclerViewAdapter();
    }

    /**
     * {@link Adapt} does not handle item updates by itself instead it redirects
     * all the handling to a {@link AdaptUpdate}. This method should update items
     * by calling {@link Source#updateItems(List)} otherwise {@link Adapt} items won\'t
     * be updated
     *
     * @param source   {@link Source}
     * @param oldItems previous items (can be null)
     * @param newItems newly supplied items (can be null)
     * @see NotifyDataSetChangedUpdate#updateItems(Source, List, List)
     * @see DiffUtilUpdate#updateItems(Source, List, List)
     */
    void updateItems(
            @NonNull Source<T> source,
            @Nullable List<? extends T> oldItems,
            @Nullable List<? extends T> newItems);
}
