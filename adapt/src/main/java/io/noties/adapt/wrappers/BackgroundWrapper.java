package io.noties.adapt.wrappers;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import io.noties.adapt.Item;
import io.noties.adapt.ItemWrapper;

/**
 * @since $UNRELEASED;
 */
public class BackgroundWrapper extends ItemWrapper {

    @NonNull
    public static Wrapper create(@ColorInt final int color) {
        return new Wrapper() {
            @NonNull
            @Override
            public Item<?> build(@NonNull Item<?> original) {
                return new BackgroundWrapper(color, original);
            }
        };
    }

    @ColorInt
    private final int color;

    public BackgroundWrapper(@ColorInt int color, @NonNull Item<?> item) {
        super(item);
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
