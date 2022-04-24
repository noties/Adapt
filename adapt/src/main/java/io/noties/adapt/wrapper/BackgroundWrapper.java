package io.noties.adapt.wrapper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.adapt.Item;

/**
 * @since 4.0.0
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

    @NonNull
    public static WrapperBuilder initSelectableItemBackground(@NonNull Context context) {
        final int[] attrs = {android.R.attr.selectableItemBackground};
        final TypedArray array = context.obtainStyledAttributes(attrs);
        final Drawable drawable;
        try {
            drawable = array.getDrawable(0);
        } finally {
            array.recycle();
        }
        return init(drawable);
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
