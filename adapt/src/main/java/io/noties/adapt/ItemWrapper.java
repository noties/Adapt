package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

// 2 kind of wrappers, that return own holder and wrapped one
public abstract class ItemWrapper extends Item<Item.Holder> {

    @NonNull
    public static Item<?> unwrap(@NonNull Item<?> item) {
        while (item instanceof ItemWrapper) {
            item = ((ItemWrapper) item).item();
        }
        return item;
    }

    private final Item<?> item;

    public ItemWrapper(@NonNull Item<?> item) {
        super(item.id());
        this.item = item;
    }

    @NonNull
    @CheckResult
    public final Item<?> item() {
        return item;
    }

    @NonNull
    @Override
    @CallSuper
    public Item.Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return item.createHolder(inflater, parent);
    }

    @Override
    @CallSuper
    public void bind(@NonNull Item.Holder holder) {
        //noinspection unchecked,rawtypes
        ((Item) item).bind(holder);
    }
}
