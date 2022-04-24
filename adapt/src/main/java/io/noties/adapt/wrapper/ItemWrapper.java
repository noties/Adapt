package io.noties.adapt.wrapper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.adapt.Item;

/**
 * Wrapper that can process Holder of wrapped Item to modify or inspect {@code itemView}
 * (for example, add padding, special layout properties). Shares the same {@code id} as wrapped Item.
 * <p>
 * <strong>NB</strong> if your wrapper has a variable associated, for example certain padding
 * passed via constructor, then execute _binding_ in {@link #bind(Item.Holder)} method. If wrapper is
 * <em>static/immutable</em> then it can process in the also {@link #createHolder(LayoutInflater, ViewGroup)}
 *
 * <strong>NB!</strong> there could be a downside of using an {@code ItemWrapper}. As each wrapper
 * creates unique {@link io.noties.adapt.Item.Key}, the same Item (with the same id) would be
 * considered different if wrapped in a wrapper and if not wrapped. So, as a general rule consider
 * always using the same set of wrappers for an Item that should be considered the same (for example,
 * to be properly animated)
 */
public abstract class ItemWrapper extends Item<Item.Holder> {

    /**
     * Returns <em>source</em> item - original item that was wrapped.
     */
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

    /**
     * Returns first item of specified ItemWrapper {@code type} or null if none found
     *
     * @since 4.0.0
     */
    @Nullable
    public static <T extends ItemWrapper> T findWrapper(
            @NonNull Item<?> item,
            @NonNull Class<T> type
    ) {
        while (item instanceof ItemWrapper) {
            if (type.isAssignableFrom(item.getClass())) {
                //noinspection unchecked
                return (T) item;
            }
            item = ((ItemWrapper) item).item();
        }
        return null;
    }

    private final Item<?> item;

    public ItemWrapper(@NonNull Item<?> item, long id) {
        super(id);
        this.item = item;
    }

    public ItemWrapper(@NonNull Item<?> item) {
        this(item, item.id());
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
    @NonNull
    public String toString() {
        return getClass().getSimpleName() + "{" + item + "}";
    }
}
