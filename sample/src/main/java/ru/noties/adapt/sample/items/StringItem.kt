package ru.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.noties.adapt.Item
import ru.noties.adapt.sample.R

class StringItem(private val text: String) : Item<StringItem.Holder>(text.hashCode().toLong()) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(0, parent, false))
    }

    override fun render(holder: Holder) {
        holder.text.text = text
    }

    class Holder(view: View) : Item.Holder(view) {
        val text = requireView<TextView>(R.id.text)
    }
}