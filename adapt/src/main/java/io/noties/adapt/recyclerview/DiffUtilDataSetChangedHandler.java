package io.noties.adapt.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.noties.adapt.Item;

import static io.noties.adapt.Item.NO_ID;

public class DiffUtilDataSetChangedHandler implements AdaptRecyclerView.DataSetChangeHandler {

    @NonNull
    public static DiffUtilDataSetChangedHandler create() {
        return create(false);
    }

    @NonNull
    public static DiffUtilDataSetChangedHandler create(boolean detectMoves) {
        return new DiffUtilDataSetChangedHandler(detectMoves);
    }

    private final boolean detectMoves;

    public DiffUtilDataSetChangedHandler(boolean detectMoves) {
        this.detectMoves = detectMoves;
    }

    @Override
    public void handleDataSetChange(
            @NonNull List<Item<?>> oldList,
            @NonNull List<Item<?>> newList,
            @NonNull AdaptRecyclerView.DataSetChangeResultCallback callback
    ) {
        final DiffUtil.DiffResult result = diffResult(oldList, newList);
        final RecyclerView.Adapter<?> adapter = callback.applyItemsChange(newList);
        result.dispatchUpdatesTo(adapter);
    }

    @NonNull
    public DiffUtil.DiffResult diffResult(
            @NonNull final List<Item<?>> oldList,
            @NonNull final List<Item<?>> newList) {
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

                // NO_ID has special case handling, if any of items has NO_ID
                //  then it's not the same as other

                final Item<?> oldItem = oldList.get(oldItemPosition);
                final long oldId = oldItem.id();
                if (oldId == NO_ID) return false;

                final Item<?> newItem = newList.get(newItemPosition);
                final long newId = newItem.id();
                if (newId == NO_ID) return false;

                // check class before id
                if (oldItem.getClass() != newItem.getClass()) return false;
                if (oldItem == newItem) return true;

                return oldId == newId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition)
                        .equals(newList.get(newItemPosition));
            }

        }, detectMoves);
    }
}
