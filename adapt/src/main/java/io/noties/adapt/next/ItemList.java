package io.noties.adapt.next;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

// helper type define a list of items
public class ItemList extends ArrayList<Item<? extends Item.Holder>> {
    public ItemList(int initialCapacity) {
        super(initialCapacity);
    }

    public ItemList() {
        super();
    }

    public ItemList(@NonNull Collection<? extends Item<? extends Item.Holder>> c) {
        super(c);
    }
}
