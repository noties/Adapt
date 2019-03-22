package ru.noties.adapt.sample.next;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.noties.adapt.next.Item;
import ru.noties.adapt.sample.R;
import ru.noties.debug.Debug;

public class NextDiff {

    interface Parent {

        void removeAt(int index);

        void move(int from, int to);

        @NonNull
        Item.Holder holderAt(int index);

        @NonNull
        Item.Holder addHolderAt(int index, @NonNull Item.Holder holder);
    }

    static void diff(@NonNull ViewGroup parent, @NonNull List<Item> previous, @NonNull List<Item> current) {

        // todo: what else can be done here:
        // - save hashCode of previous set and validate that we receive the same previous?...
        //      (we actually should not expose previous/current, just accept current)

        // we won't be exposing this one to public, but we use it for own calculations
        final List<Item> list = new ArrayList<>(previous);
//        final List<Op> ops = new ArrayList<>();

        // find removed items
        for (int i = previous.size() - 1; i >= 0; i--) {
            if (!current.contains(previous.get(i))) {
//                ops.add(new Op(OpType.REMOVE, list.remove(i)));
//                ops.add(new RemoveOp(i));
                list.remove(i);
                Debug.i("delete: %d", i);

                parent.removeViewAt(i);
            }
        }

        int index;
        Item item;

        for (int i = 0, count = current.size(); i < count; i++) {

            item = current.get(i);
            index = list.indexOf(item);

            Debug.i("i: %d, index: %d, item: %s, list: %s", i, index, item, list);

            if (index >= 0) {

                if (index != i) {

//                        Debug.e("move at : %d, item: %s", i, current.get(i));
//                        Debug.i("move");
//                    ops.add(new Op(OpType.MOVE, current.get(i)));
//                    ops.add(new MoveOp(index, i));

                    list.add(i, item);

                    final View view = parent.getChildAt(index);
                    parent.removeViewAt(index);
                    parent.addView(view, i);
                } else {
//                        Debug.i("not-changed");
                    // here we can check if it has changed
                }

                // we actually should generate a check here if an item has changed (in whatever outcome
                // if view was moved or not)
                // todo: for now just render, but further update this part? do we need it actually?
                final Item.Holder holder = (Item.Holder) parent.getChildAt(i).getTag(R.id.diff);
                item.render(holder);

            } else {

                // how to insert actually here
                    Debug.e("insert at: %d, size: %s, item: %s, list: %s", i, list.size(), item, list);
//                    previous.add(i, current.get(i));
//                    Debug.i("insert");

//                if (i < list.size()) {
//                    list.add(i, item);
//                } else {
//                    list.add(item);
//                }
                list.add(i, item);
//                list.set(i, current.get(i));

                final Item.Holder holder = item.createHolder(LayoutInflater.from(parent.getContext()), parent);
                parent.addView(holder.itemView, i);
                holder.itemView.setTag(R.id.diff, holder);

                // and render immediately
                item.render(holder);

//                ops.add(new Op(OpType.INSERT, current.get(i)));
//                ops.add(new InsertOp(i));
            }
        }

//        Debug.i(current);
//        Debug.i(list);
//        Debug.i("equals: %s", list.equals(current));
//        ops.forEach(op -> Debug.i(op));
    }
}
