package ru.noties.adapt.next;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class Item<H extends Item.Holder> {

    // todo: if we have a different item with the same id, we will render it wrong
    //      so, user submits one list, then another and one item has the same id althought it's now in a different item
    //      this will actually cause class-cast-exception (as we won't be able to cast holder)

    // todo: make equals and hashcode non-final and do not implement them at all
    //      keep id final, but allow items to have different equals and hashcode

    private final long id;

    private int viewType;

    protected Item(long id) {
        this.id = id;
    }

    public final long id() {
        return id;
    }

    @NonNull
    public abstract H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void render(@NonNull H holder);

    /**
     * Only available when displayed in RecyclerView. By default ignores payload argument and calls
     * {@link #render(Holder)}
     */
    public void recyclerRender(@NonNull H holder, @NonNull List<Object> payload) {
        render(holder);
    }

    /**
     * Used only in RecyclerView context. By default returns hashCode of `getClass().getName()`.
     */
    public int recyclerViewType() {
        int viewType = this.viewType;
        if (viewType == 0) {
            viewType = this.viewType = getClass().getName().hashCode();
        }
        return viewType;
    }

    /**
     * Used only in RecyclerView
     */
    @Nullable
    public RecyclerView.ItemDecoration recyclerDecoration() {
        return null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
//        if (o == null || getClass() != o.getClass()) return false;

        // _IF_ we are going to use ItemWrapper then we must check for instanceof
        // because the final thing to consider is id which should be the same for wrapped/non-wrapped item
        // this is not good because when received a new set of items, they can be different,
        // for example have new wrapped items that returns different views, and this is not good
        Item<?> item = (Item<?>) o;

        return id == item.id;
    }

    @Override
    public final int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @SuppressWarnings("WeakerAccess")
    public static class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

        @NonNull
        protected <V extends View> V requireView(@IdRes int id) {
            return requireView(itemView, id);
        }

        @NonNull
        protected <V extends View> V requireView(@NonNull View view, @IdRes int id) {
            return ViewUtils.requireView(view, id);
        }
    }
}
