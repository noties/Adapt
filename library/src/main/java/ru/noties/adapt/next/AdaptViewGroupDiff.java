package ru.noties.adapt.next;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static ru.noties.adapt.next.TypeUtils.sameClass;

/**
 * @since 2.0.0-SNAPSHOT
 */
public abstract class AdaptViewGroupDiff {

    @NonNull
    public static AdaptViewGroupDiff create() {
        return new Impl();
    }

    public interface Parent {

        void removeAt(int index);

        void move(int from, int to);

        void insertAt(int index, @NonNull Item item);

        void render(int index, @NonNull Item item);
    }

    public abstract void diff(
            @NonNull Parent parent,
            @NonNull List<Item> previous,
            @NonNull List<Item> current);


    // took a hint from here: https://stackoverflow.com/a/6202307/6745174
    static class Impl extends AdaptViewGroupDiff {

        @Override
        public void diff(@NonNull Parent parent, @NonNull List<Item> previous, @NonNull List<Item> current) {

            // we won't be exposing this one to public, but we use it for own calculations
            final List<Item> list = new ArrayList<>(previous);

            // find removed items
            for (int i = previous.size() - 1; i >= 0; i--) {
                if (!contains(current, previous.get(i))) {
                    list.remove(i);
                    parent.removeAt(i);
                }
            }

            int index;
            Item item;

            for (int i = 0, count = current.size(); i < count; i++) {

                item = current.get(i);
                index = indexOf(list, item);

                // item is present in both lists
                if (index >= 0) {

                    // validate that both items are of the same type
                    // this is required because different items can have the same id
                    if (sameClass(item, list.get(index))) {

                        // if item has different position in old list, then we move it
                        if (index != i) {

                            list.add(i, item);
                            parent.move(index, i);
                        }

                        // else branch here would mean that item is the same position
                        // and doesn't need to be moved

                    } else {

                        // items have different types, what we can do here is only remove old one
                        // and create a new one

                        // first remove from parent
                        list.remove(index);
                        parent.removeAt(index);

                        // then add newly created item
                        list.add(i, item);
                        parent.insertAt(i, item);
                    }

                } else {
                    // item is not present in previous list, we should insert it
                    list.add(i, item);
                    parent.insertAt(i, item);
                }

                parent.render(i, item);
            }
        }
    }

    private static int indexOf(@NonNull List<Item> list, @NonNull Item item) {
        final long id = item.id();
        for (int i = 0, size = list.size(); i < size; i++) {
            if (id == list.get(i).id()) {
                return i;
            }
        }
        return -1;
    }

    private static boolean contains(@NonNull List<Item> list, @NonNull Item item) {
        return indexOf(list, item) >= 0;
    }
}
