package io.noties.adapt.wrapper;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.adapt.Item;

/**
 * @since $UNRELEASED;
 */
public class BackgroundWrapper extends ItemWrapper {

    @NonNull
    public static WrapperBuilder clear() {
        return init(null);
    }

    @NonNull
    public static WrapperBuilder init(@ColorInt final int color) {
        return item -> new BackgroundWrapper(item, new ColorDrawable(color));
    }

    @NonNull
    public static WrapperBuilder init(@Nullable Drawable drawable) {
        return item -> new BackgroundWrapper(item, drawable);
    }

    @Nullable
    private final Drawable drawable;

    public BackgroundWrapper(@NonNull Item<?> item, @Nullable Drawable drawable) {
        super(item);
        this.drawable = drawable;
    }

    @Nullable
    public Drawable drawable() {
        return drawable;
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);

        holder.itemView()
                .setBackground(drawable);
    }
}
