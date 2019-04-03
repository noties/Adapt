package ru.noties.adapt.next;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

import static ru.noties.adapt.next.Item.NO_ID;

/**
 * @since 2.0.0-SNAPSHOT
 */
public class DiffUtilDataSetChanged implements Adapt.DataSetChangeHandler {

    public interface PayloadProvider {

        @Nullable
        Object provide(@NonNull Item oldItem, @NonNull Item newItem);
    }

    @NonNull
    public static DiffUtilDataSetChanged create() {
        return create(false, null);
    }

    @NonNull
    public static DiffUtilDataSetChanged create(boolean detectMoves) {
        return create(detectMoves, null);
    }

    @NonNull
    public static DiffUtilDataSetChanged create(
            boolean detectMoves,
            @Nullable PayloadProvider payloadProvider) {
        return new DiffUtilDataSetChanged(detectMoves, payloadProvider);
    }

    private final boolean detectMoves;
    private final PayloadProvider payloadProvider;

    public DiffUtilDataSetChanged(boolean detectMoves, @Nullable PayloadProvider payloadProvider) {
        this.detectMoves = detectMoves;
        this.payloadProvider = payloadProvider;
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
                final long oldId = oldList.get(oldItemPosition).id();
                if (oldId == NO_ID) {
                    return false;
                }
                final long newId = newList.get(newItemPosition).id();
                if (newId == NO_ID) {
                    return false;
                }
                return oldId == newId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                return payloadProvider != null
                        ? payloadProvider.provide(oldList.get(oldItemPosition), newList.get(newItemPosition))
                        : null;
            }
        }, detectMoves);
    }
}
