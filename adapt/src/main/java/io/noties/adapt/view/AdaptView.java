package io.noties.adapt.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Locale;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;

public class AdaptView {

    @NonNull
    public static AdaptView init(
            @NonNull ViewGroup parent,
            @NonNull Item<?> item
    ) {
        return init(
                LayoutInflater.from(parent.getContext()),
                parent,
                item
        );
    }

    @NonNull
    public static AdaptView init(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            @NonNull Item<?> item
    ) {
        final Item.Holder holder = item.createHolder(inflater, parent);
        final View view = holder.itemView();
        view.setTag(ID_HOLDER, holder);
        parent.addView(view);
        //noinspection unchecked,rawtypes,
        ((Item) item).bind(holder);
        view.setTag(ID_ITEM, item);
        return new AdaptView(inflater, parent, view);
    }

    private static final int ID_HOLDER = R.id.adapt_internal_holder;
    private static final int ID_ITEM = R.id.adapt_internal_item;

    private final LayoutInflater inflater;
    private final ViewGroup viewGroup;

    private View view;

    AdaptView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup viewGroup,
            @NonNull View view
    ) {
        this.inflater = inflater;
        this.viewGroup = viewGroup;
        this.view = view;
    }

    @NonNull
    public LayoutInflater inflater() {
        return inflater;
    }

    @NonNull
    public ViewGroup viewGroup() {
        return viewGroup;
    }

    /**
     * As multiple types can be bound, returned view might be different between different items
     */
    @NonNull
    public View view() {
        return view;
    }

    @NonNull
    public Item<?> item() {
        final Item<?> item = (Item<?>) view.getTag(ID_ITEM);
        if (item == null) {
            // unexpected internal error, no item is specified (we cannot create an AdaptView without
            // item)
            throw AdaptException.create("Unexpected state, there is no item bound, " +
                    "view: " + view);
        }
        return item;
    }

    public void setItem(@NonNull Item<?> item) {

        final Item.Holder holder = (Item.Holder) view.getTag(ID_HOLDER);

        if (holder == null
                || !bind(item, holder)) {
            // create new
            createHolder(item);
        }

        // save item information
        view.setTag(ID_ITEM, item);
    }

    public void notifyChanged() {
        setItem(item());
    }

    private boolean bind(@NonNull Item<?> item, @NonNull Item.Holder holder) {
        try {
            //noinspection unchecked,rawtypes
            ((Item) item).bind(holder);
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    private void createHolder(@NonNull Item<?> item) {

        final int index = viewGroup.indexOfChild(view);
        if (index < 0) {
            throw AdaptException.create(String.format(Locale.ROOT,
                    "View is not attached to parent, view: %s, parent: %s", view, viewGroup));
        }

        final Item.Holder holder = item.createHolder(inflater, viewGroup);

        viewGroup.removeViewAt(index);
        viewGroup.addView(holder.itemView(), index);

        //noinspection unchecked,rawtypes
        ((Item) item).bind(holder);

        view = holder.itemView();
        view.setTag(ID_HOLDER, holder);
    }
}
