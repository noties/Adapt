package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R

class CardBigItem(
    private val letter: String,
    private val color: Int,
    private val title: String
) : Item<CardBigItem.Holder>(hash(CardBigItem::class, letter, color, title)) {

    class Holder(view: View) : Item.Holder(view) {
        val letterView: TextView = requireView(R.id.letter_view)
        val titleView: TextView = requireView(R.id.title_view)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_card_big, parent, false))
    }

    override fun render(holder: Holder) {
        holder.letterView.setBackgroundColor(color)
        holder.letterView.text = letter
        holder.titleView.text = title
    }
}