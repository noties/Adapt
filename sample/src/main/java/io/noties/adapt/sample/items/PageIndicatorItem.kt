package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.util.activate

class PageIndicatorItem(
    val title: String,
    var selected: Boolean,
    val onClick: (PageIndicatorItem) -> Unit
) : Item<PageIndicatorItem.Holder>(hash(title)) {

    class Holder(view: View) : Item.Holder(view) {
        val titleView: TextView = requireView(R.id.title)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_page_indicator, parent, false))
    }

    override fun bind(holder: Holder) {
        holder.titleView.text = title
        holder.itemView().also {
            it.setOnClickListener { onClick(this) }
            it.activate(selected)
        }
    }
}