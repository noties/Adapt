package io.noties.adapt.next.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.noties.adapt.R;
import io.noties.adapt.next.Item;

public class AdaptView<I extends Item<? extends Item.Holder>> {

    @NonNull
    public static <I extends Item<? extends Item.Holder>> AdaptView<I> create(
            @NonNull ViewGroup parent,
            @NonNull I item
    ) {
        return create(
                LayoutInflater.from(parent.getContext()),
                parent,
                item
        );
    }

    @NonNull
    public static <I extends Item<? extends Item.Holder>> AdaptView<I> create(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            @NonNull I item
    ) {
        final Item.Holder holder = item.createHolder(inflater, parent);
        final View view = holder.itemView();
        view.setTag(R.id.adapt_internal_holder, holder);
        //noinspection unchecked,rawtypes,
        ((Item) item).render(holder);
        view.setTag(R.id.adapt_internal_item, item);
        return new AdaptView<>(view);
    }

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
        final Item item = (Item) view.getTag(R.id.adapt_internal_item);
        // todo: ensure non-null
        return (I) item;
    }

    public void setItem(@NonNull I item) {

    }
}
