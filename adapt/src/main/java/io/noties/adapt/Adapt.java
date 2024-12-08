package io.noties.adapt;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface Adapt {

    interface OnItemsChangedListener {
        void onItemsChanged(@Nullable List<Item<?>> items);
    }

    @NonNull
    @CheckResult
    List<Item<?>> items();

    void setItems(@Nullable List<Item<?>> items);

    void notifyAllItemsChanged();

    void notifyItemChanged(@NonNull Item<?> item);

    void registerOnItemsChangedListener(@NonNull OnItemsChangedListener listener);

    void unregisterOnItemsChangedListener(@NonNull OnItemsChangedListener listener);
}
