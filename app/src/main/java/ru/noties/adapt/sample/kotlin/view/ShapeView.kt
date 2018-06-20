package ru.noties.adapt.sample.kotlin.view

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.noties.adapt.Holder
import ru.noties.adapt.ItemView
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.core.ShapeItemDrawable
import ru.noties.adapt.sample.core.item.ShapeItem

class ShapeView : ItemView<ShapeItem, Holder>() {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder =
            Holder(inflater.inflate(R.layout.view_shape, parent, false))

    override fun bindHolder(holder: Holder, item: ShapeItem) {
        val background = holder.itemView.background
        if (background == null || background !is ShapeItemDrawable) {
            val drawable = ShapeItemDrawable(item.type(), item.color())
            holder.itemView.background = drawable
        } else {
            background.update(item.type(), item.color())
        }
    }
}