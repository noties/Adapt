package io.noties.adapt;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

/**
 * Wrapper that can process Holder of wrapped Item to modify or inspect {@code itemView} (add padding,
 * special layout properties). Shares the same {@code id} as wrapped Item.
 *
 * <strong>NB</strong> by default equals/hashCode methods redirect to wrapped item. Be sure to check
 * for the {@link #viewType()} before calling equals or provide own implementation
 *
 * <strong>NB</strong> if your wrapper has a variable associated, for example certains padding
 * passed via constructor, then execute _binding_ in {@link #bind(Holder)} method. If wrapper is
 * <em>static/immutable</em> then it can process in the also {@link #createHolder(LayoutInflater, ViewGroup)}
 */
public abstract class ItemWrapper extends Item<Item.Holder> {

    public interface Provider {
        @NonNull
        Item<?> provide();
    }

    @NonNull
    public static Item<?> unwrap(@NonNull Item<?> item) {
        while (item instanceof ItemWrapper) {
            item = ((ItemWrapper) item).item();
        }
        return item;
    }

    public static boolean isWrapped(@NonNull Item<?> item) {
        return item instanceof ItemWrapper;
    }

    private final Item<?> item;

    public ItemWrapper(@NonNull Item<?> item) {
        super(item.id());
        this.item = item;
    }

    public ItemWrapper(@NonNull Provider provider) {
        this(provider.provide());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        //noinspection rawtypes
        return unwrap(item).equals(unwrap((Item) o));
    }

    @Override
    public int hashCode() {
        return unwrap(item).hashCode();
    }
}
