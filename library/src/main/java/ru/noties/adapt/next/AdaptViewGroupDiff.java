package ru.noties.adapt.next;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static ru.noties.adapt.next.TypeUtils.sameClass;

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
                if (!current.contains(previous.get(i))) {
                    list.remove(i);
                    parent.removeAt(i);
                }
            }

            int index;
            Item item;

            for (int i = 0, count = current.size(); i < count; i++) {

                item = current.get(i);
                index = list.indexOf(item);

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

//    static void diff(@NonNull ViewGroup parent, @NonNull List<Item> previous, @NonNull List<Item> current) {
//
//        // todo: what else can be done here:
//        // - save hashCode of previous set and validate that we receive the same previous?...
//        //      (we actually should not expose previous/current, just accept current)
//
//        // we won't be exposing this one to public, but we use it for own calculations
//        final List<Item> list = new ArrayList<>(previous);
////        final List<Op> ops = new ArrayList<>();
//
//        // find removed items
//        for (int i = previous.size() - 1; i >= 0; i--) {
//            if (!current.contains(previous.get(i))) {
////                ops.add(new Op(OpType.REMOVE, list.remove(i)));
////                ops.add(new RemoveOp(i));
//                list.remove(i);
//                Debug.i("delete: %d", i);
//
//                parent.removeViewAt(i);
//            }
//        }
//
//        int index;
//        Item item;
//
//        for (int i = 0, count = current.size(); i < count; i++) {
//
//            item = current.get(i);
//            index = list.indexOf(item);
//
//            Debug.i("i: %d, index: %d, item: %s, list: %s", i, index, item, list);
//
//            if (index >= 0) {
//
//                if (index != i) {
//
////                        Debug.e("move at : %d, item: %s", i, current.get(i));
////                        Debug.i("move");
////                    ops.add(new Op(OpType.MOVE, current.get(i)));
////                    ops.add(new MoveOp(index, i));
//
//                    list.add(i, item);
//
//                    final View view = parent.getChildAt(index);
//                    parent.removeViewAt(index);
//                    parent.addView(view, i);
//                } else {
////                        Debug.i("not-changed");
//                    // here we can check if it has changed
//                }
//
//                // we actually should generate a check here if an item has changed (in whatever outcome
//                // if view was moved or not)
//                // todo: for now just render, but further update this part? do we need it actually?
//                final Item.Holder holder = (Item.Holder) parent.getChildAt(i).getTag(R.id.diff);
//                item.render(holder);
//
//            } else {
//
//                // how to insert actually here
//                Debug.e("insert at: %d, size: %s, item: %s, list: %s", i, list.size(), item, list);
////                    previous.add(i, current.get(i));
////                    Debug.i("insert");
//
////                if (i < list.size()) {
////                    list.add(i, item);
////                } else {
////                    list.add(item);
////                }
//                list.add(i, item);
////                list.set(i, current.get(i));
//
//                final Item.Holder holder = item.createHolder(LayoutInflater.from(parent.getContext()), parent);
//                parent.addView(holder.itemView, i);
//                holder.itemView.setTag(R.id.diff, holder);
//
//                // and render immediately
//                item.render(holder);
//
////                ops.add(new Op(OpType.INSERT, current.get(i)));
////                ops.add(new InsertOp(i));
//            }
//        }

//        Debug.i(current);
//        Debug.i(list);
//        Debug.i("equals: %s", list.equals(current));
//        ops.forEach(op -> Debug.i(op));
}
