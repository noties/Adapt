package ru.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.noties.adapt.Item
import ru.noties.adapt.sample.IconDrawable
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.Shape

class TriangleItem(id: Long, private val color: Int) : Item<TriangleItem.Holder>(id) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_shaped, parent, false))
    }

    override fun render(holder: Holder) {
        val bg = holder.icon.background as? IconDrawable
                ?: IconDrawable().also { holder.icon.background = it }
        bg.update(color, Shape.TRIANGLE)
    }

    class Holder(view: View) : Item.Holder(view) {
        val icon = requireView<View>(R.id.icon)
    }
}