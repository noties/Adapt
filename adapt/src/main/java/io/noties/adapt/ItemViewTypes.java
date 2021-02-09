package io.noties.adapt;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ItemViewTypes {

    // each item has a unique signature, depending on wrapped
    // [Padding] > [Margin] > [Item] is not the same as [Margin] > [Padding] > [Item]

    /**
     * @return viewType for specified Item if it is not wrapped by {@link ItemWrapper},
     * in which case view type would be different
     */
    @CheckResult
    public static int expectedViewTypeIfNotWrapped(@NonNull Class<? extends Item<?>> itemType) {
        return viewType(new Key(Collections.<Class<?>>singletonList(itemType)));
    }

    @CheckResult
    public static int viewType(@NonNull Item<?> item) {
        return viewType(extractKey(item));
    }

    static int viewType(@NonNull Key key) {

        Integer value = CACHE.get(key);

        if (value == null) {
            synchronized (LOCK) {
                value = CACHE.get(key);
                if (value == null) {
                    value = VIEW_TYPES.incrementAndGet();
                    CACHE.put(key, value);
                }
            }
        }

        return value;
    }

    @NonNull
    @CheckResult
    static Key extractKey(@NonNull Item<?> item) {

        final List<Class<?>> list = new ArrayList<>(3);

        while (item instanceof ItemWrapper) {
            list.add(item.getClass());
            item = ((ItemWrapper) item).item();
        }

        list.add(item.getClass());

        return new Key(list);
    }

    private static final Map<Key, Integer> CACHE = new HashMap<>(3);
    private static final AtomicInteger VIEW_TYPES = new AtomicInteger(0);
    private static final Object LOCK = new Object();

    static class Key {
        final List<Class<?>> classes;

        Key(@NonNull List<Class<?>> classes) {
            this.classes = classes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return classes.equals(key.classes);
        }

        @Override
        public int hashCode() {
            return classes.hashCode();
        }
    }
}
