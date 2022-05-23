package io.noties.adapt.sample.items

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.ui.CircleDrawable
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.util.Edges

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

        holder.itemView().also {
            it.background = StatefulShape.drawable {
                val base = RoundedRectangle(8) {
                    padding(4)
                    fill(Color.BLUE)
                    add(Circle()) {
                        fill(Color.MAGENTA)
                        padding(Edges(2))
                        size(24, 24, Gravity.END or Gravity.BOTTOM)
                        translate(x = -16, y = -16)
                    }
                }

                setPressed(base.copy {
                    alpha(0.25F)
                    stroke(Color.BLUE, 12, 8, 1)
                })

                setDefault(base)
            }
        }
        holder.itemView().setOnClickListener { }
    }
}