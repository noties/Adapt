package io.noties.adapt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @since 2.0.0-SNAPSHOT
 */
public class NotifyDataSetChanged implements Adapt.DataSetChangeHandler {

    @NonNull
    public static NotifyDataSetChanged create() {
        return new NotifyDataSetChanged();
    }

    @Override
    public void handleDataSetChange(
            @NonNull Adapt adapt,
            @Nullable Adapt.ItemViewTypeFactory itemViewTypeFactory,
            @NonNull List<Item> oldList,
            @NonNull List<Item> newList) {
        adapt
                .swapItemsBeforeUpdate(newList, itemViewTypeFactory)
                .notifyDataSetChanged();
    }

    @Override
    public void cancel() {
        // no op, we are totally synchronous
    }
}
