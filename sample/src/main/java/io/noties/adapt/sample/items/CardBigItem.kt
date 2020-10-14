package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.debug.Debug
import java.util.*

class CardBigItem(
    val letter: String,
    val color: Int,
    val title: String
) : Item<CardBigItem.Holder>(Objects.hash(CardBigItem::class, letter, color, title).toLong()) {

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

        if (true) {
            holder.itemView().setOnClickListener {
                Debug.i("clicked: $letter, $title (${id()})")
            }
        }
    }

    override fun toString(): String {
        return "CardBigItem(letter='$letter', color=$color, title='$title')"
    }


}