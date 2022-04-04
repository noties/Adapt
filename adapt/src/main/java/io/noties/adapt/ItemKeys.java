package io.noties.adapt;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.noties.adapt.wrapper.ItemWrapper;

abstract class ItemKeys {

    private static final Map<Item.Key, Integer> CACHE = new HashMap<>(3);
    private static final AtomicInteger VIEW_TYPES = new AtomicInteger(0);
    private static final Object LOCK = new Object();

    @VisibleForTesting
    static void clearCache() {
        synchronized (LOCK) {
            CACHE.clear();
            VIEW_TYPES.set(0);
        }
    }

    static class KeyImpl extends Item.Key {

        private final List<Class<? extends Item<?>>> items;

        KeyImpl(@NonNull List<Class<? extends Item<?>>> items) {
            this.items = items;
        }

        @Override
        public int viewType() {
            Integer viewType = CACHE.get(this);
            if (viewType == null) {
                synchronized (LOCK) {
                    viewType = CACHE.get(this);
                    if (viewType == null) {
                        viewType = VIEW_TYPES.incrementAndGet();
                        CACHE.put(this, viewType);
                    }
                }
            }
            return viewType;
        }

        @NonNull
        @Override
        public List<Class<? extends Item<?>>> items() {
            return items;
        }

        @Override
        @NonNull
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            for (Class<? extends Item<?>> item : items) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(item.getName());
            }
            return "Item.Key[" +
                    "" + builder +
                    ']';
        }

        @NonNull
        @Override
        public String toShortString() {
            final StringBuilder builder = new StringBuilder();
            for (Class<? extends Item<?>> item : items) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(item.getSimpleName());
            }
            return "Item.Key[" +
                    "" + builder +
                    ']';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KeyImpl key = (KeyImpl) o;

            return items.equals(key.items);
        }

        @Override
        public int hashCode() {
            return items.hashCode();
        }

        static class BuilderImpl implements Builder {

            private final List<Class<? extends Item<?>>> items;

            BuilderImpl() {
                this(Collections.<Class<? extends Item<?>>>emptyList());
            }

            BuilderImpl(@NonNull List<Class<? extends Item<?>>> items) {
                this.items = items;
            }

            @NonNull
            @Override
            public Builder wrapped(@NonNull Class<? extends ItemWrapper> by) {
                return new BuilderImpl(with(by));
            }

            @NonNull
            @Override
            public Item.Key build(@NonNull Class<? extends Item<?>> item) {
                return new KeyImpl(with(item));
            }

            @NonNull
            @CheckResult
            List<Class<? extends Item<?>>> with(@NonNull Class<? extends Item<?>> item) {
                final List<Class<? extends Item<?>>> list = new ArrayList<>(items.size() + 1);
                list.addAll(items);
                list.add(item);
                return Collections.unmodifiableList(list);
            }
        }
    }

    @NonNull
    @CheckResult
    static Item.Key create(@NonNull Item<?> item) {
        final List<Class<? extends Item<?>>> items = new ArrayList<>(1);

        //noinspection unchecked
        items.add((Class<? extends Item<?>>) item.getClass());

        if (item instanceof ItemWrapper) {
            Item<?> wrapped = ((ItemWrapper) item).item();
            while (wrapped != null) {

                //noinspection unchecked
                items.add((Class<? extends Item<?>>) wrapped.getClass());

                wrapped = wrapped instanceof ItemWrapper
                        ? ((ItemWrapper) wrapped).item()
                        : null;
            }
        }

        return new KeyImpl(Collections.unmodifiableList(items));
    }

    private ItemKeys() {
    }
}
