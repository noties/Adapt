package io.noties.adapt.wrapper;

import androidx.annotation.NonNull;

import io.noties.adapt.Item;

public class OnBindItemWrapper extends ItemWrapper {

    public interface Action<H extends Item.Holder, T extends Item<H>> {
        void apply(@NonNull T item, @NonNull H holder);
    }

    @NonNull
    public static <H extends Item.Holder, T extends Item<H>> Item<?> wrap(
            @NonNull T item,
            @NonNull Action<H, T> action
    ) {
        //noinspection unchecked
        return new OnBindItemWrapper(
                item,
                (Action<Holder, Item<Holder>>) action
        );
    }

    private final Action<Item.Holder, Item<Item.Holder>> action;

    private OnBindItemWrapper(@NonNull Item<?> item, @NonNull Action<Item.Holder, Item<Item.Holder>> action) {
        super(item);
        this.action = action;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        //noinspection unchecked
        action.apply(
                (Item<Holder>) item(),
                holder
        );
    }
}
