package io.noties.adapt.sample.items

import android.widget.TextView
import io.noties.adapt.ItemLayout
import io.noties.adapt.sample.R
import io.noties.adapt.sample.util.activate

class PageIndicatorItem(
    val title: String,
    var selected: Boolean,
    val onClick: (PageIndicatorItem) -> Unit
) : ItemLayout(hash(title), R.layout.item_page_indicator) {

    override fun bind(holder: Holder) {
        val titleView: TextView = holder.requireView(R.id.title)
        titleView.text = title
        holder.itemView().also {
            it.setOnClickListener { onClick(this) }
            it.activate(selected)
        }
    }
}