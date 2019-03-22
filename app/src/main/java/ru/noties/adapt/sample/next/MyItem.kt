package ru.noties.adapt.sample.next

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.noties.adapt.next.Item
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.core.ShapeItemDrawable
import ru.noties.adapt.sample.core.ShapeType

data class MyItem(
        val shapeType: ShapeType,
        val color: Int,
        val title: String,
        val subtitle: String) : Item<MyItem.Holder>(title.hashCode().toLong()) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.adapt_item, parent, false))
    }

    override fun render(holder: Holder) {

        val drawable = holder.icon.background as? ShapeItemDrawable
                ?: ShapeItemDrawable(shapeType, color).also {
                    holder.icon.background = it
                }
        drawable.update(shapeType, color)

        holder.title.text = title
        holder.subtitle.text = subtitle
    }

    class Holder(view: View) : Item.Holder(view) {
        val icon = requireView<View>(R.id.icon)
        val title = requireView<TextView>(R.id.title)
        val subtitle = requireView<TextView>(R.id.subtitle)
    }
}