package io.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import static io.noties.adapt.Item.NO_ID;

/**
 * @since 2.0.0
 */
public class DiffUtilDataSetChanged implements Adapt.DataSetChangeHandler {

    @NonNull
    public static DiffUtilDataSetChanged create() {
        return create(false);
    }

    @NonNull
    public static DiffUtilDataSetChanged create(boolean detectMoves) {
        return new DiffUtilDataSetChanged(detectMoves);
    }

    private final boolean detectMoves;

    public DiffUtilDataSetChanged(boolean detectMoves) {
        this.detectMoves = detectMoves;
    }

    @Override
    public void handleDataSetChange(
            @NonNull Adapt adapt,
            @Nullable Adapt.ItemViewTypeFactory itemViewTypeFactory,
            @NonNull final List<Item> oldList,
            @NonNull final List<Item> newList) {
        final DiffUtil.DiffResult result = diffResult(oldList, newList);
        result.dispatchUpdatesTo(adapt.swapItemsBeforeUpdate(newList, itemViewTypeFactory));
    }

    @Override
    public void cancel() {
        // no op, we have no async operations
    }

    @NonNull
    public DiffUtil.DiffResult diffResult(
            @NonNull final List<Item> oldList,
            @NonNull final List<Item> newList) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {

            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

                // NO_ID has special case handling, if any of items has NO_ID than it's not the same
                //  otherwise in case when both items have NO_ID they will be considered as same,
                //  which is not true

                final Item oldItem = oldList.get(oldItemPosition);
                final long oldId = oldItem.id();
                if (oldId == NO_ID) {
                    return false;
                }

                final Item newItem = newList.get(newItemPosition);
                final long newId = newItem.id();
                if (newId == NO_ID) {
                    return false;
                }

                // we won't be checking against actual Class<?> of items, instead we will
                // use recyclerViewType

                // execute viewType check after id check (so, if ids are different, we won't
                // go to second condition)
                return oldId == newId
                        && oldItem.viewType() == newItem.viewType();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

        }, detectMoves);
    }
}
