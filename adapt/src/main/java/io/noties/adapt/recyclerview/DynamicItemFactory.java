package io.noties.adapt.recyclerview;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;

/**
 * @see FixedItemFactory
 * @since $UNRELEASED;
 */
public class DynamicItemFactory implements AdaptRecyclerView.ItemFactory {

    // special storage to keep track of items and view-types
    private final Map<Integer, Item<?>> store = new HashMap<>(3);

    @Override
    public void onNewItems(@NonNull List<Item<?>> items) {
        // release old items from referencing
        store.clear();

        for (Item<?> item : items) {
            store.put(item.viewType(), item);
        }
    }

    @NonNull
    @Override
    public Item<?> itemToCreateViewFrom(int viewType) {
        final Item<?> firstItem = store.get(viewType);
        if (firstItem == null) {
            throw AdaptException.create("Unexpected viewType: " + viewType);
        }
        return firstItem;
    }
}
