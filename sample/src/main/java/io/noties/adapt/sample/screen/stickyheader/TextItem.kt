package io.noties.adapt.sample.screen.stickyheader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R

class TextItem(private val text: CharSequence): Item<TextItem.Holder>(text.hashCode().toLong()) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_text, parent, false))
    }

    override fun render(holder: Holder) {
        holder.textView.text = text
    }

    class Holder(view: View): Item.Holder(view) {
        val textView = requireView<TextView>(R.id.text)
    }
}