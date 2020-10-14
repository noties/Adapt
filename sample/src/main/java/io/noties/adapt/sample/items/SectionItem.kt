package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import java.util.*

class SectionItem(val text: String) :
    Item<SectionItem.Holder>(Objects.hash(SectionItem::class, text).toLong()) {

    class Holder(view: View) : Item.Holder(view) {
        val textView: TextView = requireView(R.id.text)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_section, parent, false))
    }

    override fun render(holder: Holder) {
        holder.textView.text = text
    }
}