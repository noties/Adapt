package io.noties.adapt.sample.items

import android.widget.TextView
import io.noties.adapt.InflatedItem
import io.noties.adapt.sample.R

class InflatedCardItem : InflatedItem(hash(InflatedItem::class.java), R.layout.item_card) {
    override fun render(holder: Holder) {
        val textView: TextView = holder.requireView(0)

        textView.text = ""
    }
}