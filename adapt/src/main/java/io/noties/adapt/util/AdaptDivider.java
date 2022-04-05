package io.noties.adapt.util;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.noties.adapt.Item;

/**
 * @since $UNRELEASED;
 */
public abstract class AdaptDivider {

    public interface DividerProvider {
        @NonNull
        @CheckResult
        Item<?> provide(@NonNull Item<?> item);
    }

    public interface Divider {
        @NonNull
        @CheckResult
        List<Item<?>> divide(@NonNull Iterable<? extends Item<?>> iterable);
    }

    @NonNull
    @CheckResult
    public static Divider divider(@NonNull DividerProvider provider) {
        return iterable -> divide(iterable, provider);
    }

    /**
     * Inserts an item provided by the {@code provider} between each item in supplied {@code iterable}.
     * A divider would be inserted before item used in provider
     */
    @NonNull
    @CheckResult
    public static List<Item<?>> divide(
            @NonNull Iterable<? extends Item<?>> iterable,
            @NonNull DividerProvider provider
    ) {
        final List<Item<?>> items = new ArrayList<>();
        final Iterator<? extends Item<?>> iterator = iterable.iterator();
        boolean first = true;
        while (iterator.hasNext()) {
            final Item<?> item = iterator.next();
            if (first) {
                first = false;
            } else {
                final Item<?> divider = provider.provide(item);
                items.add(divider);
            }
            items.add(item);
        }
        return items;
    }

    private AdaptDivider() {
    }
}
