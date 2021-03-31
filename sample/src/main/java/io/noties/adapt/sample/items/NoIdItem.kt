package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import java.util.*

class NoIdItem : Item<NoIdItem.Holder>(NO_ID) {

    class Holder(view: View) : Item.Holder(view) {
        val textView: TextView = requireView(R.id.text)
        val created: Date = Date()
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_no_id, parent, false))
    }

    override fun bind(holder: Holder) {
        holder.textView.text = holder.created.toString()
    }
}