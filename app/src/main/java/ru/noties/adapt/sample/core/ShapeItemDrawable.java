package ru.noties.adapt.sample.core;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ShapeItemDrawable extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    private ShapeType type;

    public ShapeItemDrawable(@NonNull ShapeType shapeType, @ColorInt int color) {
        this.paint.setStyle(Paint.Style.FILL);
        update(shapeType, color);
    }

    public void update(@NonNull ShapeType shapeType, @ColorInt int color) {
        this.type = shapeType;
        this.paint.setColor(color);

        path.reset();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        path.reset();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        bindPathIfEmpty();
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // no op
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // no op
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    private void bindPathIfEmpty() {

        if (!path.isEmpty() || type == null) {
            return;
        }

        final Rect bounds = getBounds();

        switch (type) {

            case CIRCLE:
                path.addCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2, Path.Direction.CW);
                break;

            case TRIANGLE:
                path.moveTo(0, bounds.height());
                path.lineTo(bounds.width() / 2, 0);
                path.lineTo(bounds.width(), bounds.height());
                path.lineTo(0, bounds.height());
                break;

            case RECTANGLE:
                path.moveTo(0, bounds.height());
                path.lineTo(0, 0);
                path.lineTo(bounds.width(), 0);
                path.lineTo(bounds.width(), bounds.height());
                path.lineTo(0, bounds.height());
                break;

            default:
                throw new RuntimeException("Unknown type: " + type);
        }

        path.close();
    }
}
