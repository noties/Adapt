package io.noties.adapt.sample.screen.stickyheader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Item
import io.noties.adapt.StickyItemDecoration
import io.noties.adapt.sample.R

class HeaderItem(private val text: CharSequence) : Item<HeaderItem.Holder>(text.hashCode().toLong()) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_header, parent, false))
    }

    override fun render(holder: Holder) {
        holder.textView.text = text
    }

    override fun recyclerDecoration(recyclerView: RecyclerView): RecyclerView.ItemDecoration? {
        // calling this factory method means that our RecyclerView is wrapped inside FrameLayout
        // if it's not possible/desired use a different method with AdaptView manually placed in layout
        return StickyItemDecoration.create(recyclerView, this)
    }

    class Holder(view: View) : Item.Holder(view) {
        val textView = requireView<TextView>(R.id.text)
    }
}