package io.noties.adapt.viewgroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;

/**
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AdaptViewGroupDiff {

    @NonNull
    public static AdaptViewGroupDiff create() {
        return new Impl();
    }

    public interface Parent {

        void removeAt(int index);

        void move(int from, int to);

        void insertAt(int index, @NonNull Item<? extends Item.Holder> item);

        void render(int index, @NonNull Item<? extends Item.Holder> item);
    }

    public abstract void diff(
            @NonNull Parent parent,
            @NonNull List<Item<? extends Item.Holder>> previous,
            @NonNull List<Item<? extends Item.Holder>> current);


    // took a hint from here: https://stackoverflow.com/a/6202307/6745174
    static class Impl extends AdaptViewGroupDiff {

        @Override
        public void diff(
                @NonNull Parent parent,
                @NonNull List<Item<? extends Item.Holder>> previous,
                @NonNull List<Item<? extends Item.Holder>> current
        ) {

            // we won't be exposing this one to public, but we use it for own calculations
            final List<Item<? extends Item.Holder>> list = new ArrayList<>(previous);

            // find removed items
            for (int i = previous.size() - 1; i >= 0; i--) {
                if (!contains(current, previous.get(i))) {
                    list.remove(i);
                    parent.removeAt(i);
                }
            }

            int index;
            Item<? extends Item.Holder> item;

            for (int i = 0, count = current.size(); i < count; i++) {

                item = current.get(i);
                index = indexOf(list, item);

                // item is present in both lists
                if (index >= 0) {

                    // if item has different position in old list, then we move it
                    if (index != i) {

                        // remove item (otherwise we will duplicate the same item)
                        list.remove(index);

                        if (i >= list.size()) {
                            // when we have a duplicate it is first removed (previous occurrence)
                            //  and then add operation fail due to the different size of the list (other than expected)
                            // TODO: should it mention equal ids?
                            throw AdaptException.create("A duplicate item is found at indices " +
                                    "%d and %d, item: %s, items: %s", index, i, item, current);
                        }

                        // add it at new position
                        list.add(i, item);

                        // signal parent about operation
                        parent.move(index, i);
                    }

                    // else branch here would mean that item is the same position
                    // and doesn't need to be moved

                } else {
                    // item is not present in previous list, we should insert it
                    list.add(i, item);
                    parent.insertAt(i, item);
                }

                parent.render(i, item);
            }
        }
    }

    private static int indexOf(@NonNull List<Item<? extends Item.Holder>> list, @NonNull Item item) {

        final Class<? extends Item> type = item.getClass();
        final long id = item.id();

        Item other;

        for (int i = 0, size = list.size(); i < size; i++) {
            other = list.get(i);
            if (type == other.getClass()
                    && id == other.id()) {
                return i;
            }
        }

        return -1;
    }

    private static boolean contains(@NonNull List<Item<? extends Item.Holder>> list, @NonNull Item item) {
        return indexOf(list, item) >= 0;
    }
}
