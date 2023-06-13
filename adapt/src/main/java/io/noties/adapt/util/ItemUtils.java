package io.noties.adapt.util;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.noties.adapt.Item;
import io.noties.adapt.wrapper.IdWrapper;

public abstract class ItemUtils {
    private ItemUtils() {
    }

    /**
     * Wraps all incoming items with {@link IdWrapper} assigning each item id based on its index
     * in supplied list. Intended to be used in previews, when single Item (having same id) could
     * be displayed multiple times.
     */
    @NonNull
    @CheckResult
    public static List<Item<?>> assignIdsAccordingToIndex(@Nullable List<Item<?>> items) {
        if (items == null) {
            return Collections.emptyList();
        }

        final int size = items.size();
        final List<Item<?>> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(IdWrapper.init(i).build(items.get(i)));
        }
        return list;
    }
}
