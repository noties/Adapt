package ru.noties.adapt.sample.java.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ru.noties.adapt.Holder;
import ru.noties.adapt.ItemView;
import ru.noties.adapt.sample.R;
import ru.noties.adapt.sample.core.ShapeItemDrawable;
import ru.noties.adapt.sample.core.item.ShapeItem;

public class ShapeView extends ItemView<ShapeItem, Holder> {

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.view_shape, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Holder holder, @NonNull ShapeItem item) {
        final Drawable background = holder.itemView.getBackground();
        if (background == null
                || !(background instanceof ShapeItemDrawable)) {
            final ShapeItemDrawable shapeItemDrawable = new ShapeItemDrawable(item.type(), item.color());
            holder.itemView.setBackground(shapeItemDrawable);
        } else {
            final ShapeItemDrawable shapeItemDrawable = (ShapeItemDrawable) background;
            shapeItemDrawable.update(item.type(), item.color());
        }
    }
}
