package io.noties.adapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

// TODO: check all implementation signatures (remove the `? extends Item.Holder`)
public interface Adapt {

    @NonNull
    List<Item<?>> items();

    void setItems(@Nullable List<Item<?>> items);

    void notifyAllItemsChanged();

    void notifyItemChanged(@NonNull Item<?> item);
}
