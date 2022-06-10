package io.noties.adapt.recyclerview;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;

/**
 * If a significant amount of items is used - it is advisable to predefine an item factory
 * in order to achieve a better performance (by default {@link DynamicItemFactory} is used
 * that dynamically builds a store from items each time a new set of items is set)
 *
 * @see DynamicItemFactory
 * @since $UNRELEASED;
 */
public class FixedItemFactory implements AdaptRecyclerView.ItemFactory {

    /**
     * Those items would be used to create initial views.
     * NB! all items that would be used in an adapter must be present here
     */
    @NonNull
    public static FixedItemFactory create(@NonNull List<Item<?>> items) {
        final Map<Integer, Item<?>> store = new HashMap<>(items.size());
        for (Item<?> item : items) {
            store.put(item.viewType(), item);
        }
        return create(store);
    }

    @NonNull
    public static FixedItemFactory create(@NonNull Map<Integer, Item<?>> store) {
        return new FixedItemFactory(store);
    }

    private final Map<Integer, Item<?>> store;

    FixedItemFactory(@NonNull Map<Integer, Item<?>> store) {
        this.store = store;
    }

    @Override
    public void onNewItems(@NonNull List<Item<?>> items) {
        // no op
    }

    @NonNull
    @Override
    public Item<?> itemToCreateViewFrom(int viewType) {
        final Item<?> firstItem = store.get(viewType);
        if (firstItem == null) {
            throw AdaptException.create(String.format(
                    Locale.ROOT,
                    "[FixedItemFactory] viewType:%d is not registered with this instance",
                    viewType
            ));
        }
        return firstItem;
    }
}
