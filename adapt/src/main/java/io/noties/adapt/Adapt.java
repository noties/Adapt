package io.noties.adapt;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface Adapt {

    @NonNull
    @CheckResult
    List<Item<?>> items();

    void setItems(@Nullable List<Item<?>> items);

    void notifyAllItemsChanged();

    void notifyItemChanged(@NonNull Item<?> item);
}
