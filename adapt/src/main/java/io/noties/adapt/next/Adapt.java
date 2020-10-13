package io.noties.adapt.next;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface Adapt {

    @NonNull
    List<Item<? extends Item.Holder>> items();

    void setItems(@Nullable List<Item<? extends Item.Holder>> items);
}
