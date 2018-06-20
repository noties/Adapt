package ru.noties.adapt.sample.core.item;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import ru.noties.adapt.sample.core.ShapeType;

public class ShapeItem extends Item {

    private final ShapeType type;
    private final int color;

    public ShapeItem(long id, @NonNull ShapeType type, @ColorInt int color) {
        super(id);
        this.type = type;
        this.color = color;
    }

    @NonNull
    public ShapeType type() {
        return type;
    }

    @ColorInt
    public int color() {
        return color;
    }
}
