package io.noties.adapt.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Locale;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.R;

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
        ((Item) item).bind(holder);
        view.setTag(ID_ITEM, item);
        return new AdaptView<>(view);
    }

    public interface HolderProvider<H extends Item.Holder> {
        @NonNull
        H provide(@NonNull View view);
    }

    @NonNull
    public static <H extends Item.Holder, I extends Item<H>> AdaptView<I> bind(
            @NonNull View view,
            @NonNull I item,
            @NonNull HolderProvider<H> provider
    ) {
        final H holder = provider.provide(view);
        view.setTag(ID_HOLDER, holder);

        //noinspection unchecked,rawtypes,
        ((Item) item).bind(holder);
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
                    "view: " + view);
        }
        return (I) item;
    }

    public void setItem(@NonNull I item) {

        final Item.Holder holder = (Item.Holder) view.getTag(ID_HOLDER);
        if (holder == null) {
            // it's required to have holder at this point, internal error
            throw AdaptException.create(String.format(
                    Locale.ROOT,
                    "Unexpected state, there is no Holder associated " +
                            "with this view, supplied item: %s, view: %s", item, view));
        }

        //noinspection unchecked,rawtypes,
        ((Item) item).bind(holder);

        // save item information
        view.setTag(ID_ITEM, item);
    }

    public void notifyChanged() {
        setItem(item());
    }
}
