package io.noties.adapt.next.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.noties.adapt.R;
import io.noties.adapt.next.AdaptException;
import io.noties.adapt.next.Item;

public class AdaptView<I extends Item<? extends Item.Holder>> {

    @NonNull
    public static <I extends Item<? extends Item.Holder>> AdaptView<I> init(
            @NonNull ViewGroup parent,
            @NonNull I item
    ) {
        return init(
                LayoutInflater.from(parent.getContext()),
                parent,
                item
        );
    }

    @NonNull
    public static <I extends Item<? extends Item.Holder>> AdaptView<I> init(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            @NonNull I item
    ) {
        final Item.Holder holder = item.createHolder(inflater, parent);
        final View view = holder.itemView();
        view.setTag(ID_HOLDER, holder);
        parent.addView(view);
        //noinspection unchecked,rawtypes,
        ((Item) item).render(holder);
        view.setTag(ID_ITEM, item);
        return new AdaptView<>(view);
    }

    private static final int ID_HOLDER = R.id.adapt_internal_holder;
    private static final int ID_ITEM = R.id.adapt_internal_item;

    private final View view;

    AdaptView(@NonNull View view) {
        this.view = view;
    }

    @NonNull
    public View view() {
        return view;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NonNull
    public I item() {
        final Item item = (Item) view.getTag(ID_ITEM);
        if (item == null) {
            // unexpected internal error, no item is specified (we cannot create an AdaptView without
            // item)
            throw AdaptException.create("Unexpected state, there is no item bound, " +
                    "view: %s", view);
        }
        return (I) item;
    }

    public void setItem(@NonNull I item) {

        final Item.Holder holder = (Item.Holder) view.getTag(ID_HOLDER);
        if (holder == null) {
            // it's required to have holder at this point, internal error
            throw AdaptException.create("Unexpected state, there is no Holder associated " +
                    "with this view, supplied item: %s, view: %s", item, view);
        }

        //noinspection unchecked,rawtypes,
        ((Item) item).render(holder);

        // save item information
        view.setTag(ID_ITEM, item);
    }

    // re-render
    public void invalidate() {
        setItem(item());
    }
}
