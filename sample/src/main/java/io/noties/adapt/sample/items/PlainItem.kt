package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.ui.CircleDrawable

class PlainItem(
    private val letter: String,
    private val color: Int,
    private val title: String
) : Item<PlainItem.Holder>(hash(PlainItem::class, letter, color, title)) {

    class Holder(view: View) : Item.Holder(view) {
        val letterView: TextView = requireView(R.id.letter_view)
        val titleView: TextView = requireView(R.id.title_view)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_plain, parent, false))
    }

    override fun bind(holder: Holder) {

        val drawable: CircleDrawable =
            (holder.letterView.background as? CircleDrawable) ?: CircleDrawable(color).apply {
                holder.letterView.background = this
            }
        drawable.color = color

        holder.letterView.text = letter
        holder.titleView.text = title
    }
}