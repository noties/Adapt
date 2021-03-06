package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Item
import io.noties.adapt.ViewState
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R

class CollectionItem(val items: List<Item<*>>) : Item<CollectionItem.Holder>(hash(items)) {

    class Holder(view: View) : Item.Holder(view) {
        val recyclerView: RecyclerView = requireView(R.id.recycler_view)
        lateinit var adapt: AdaptRecyclerView
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        val view = inflater.inflate(R.layout.item_collection, parent, false)
        return Holder(view).apply {
            recyclerView.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
            // if not used inside a recyclerView it is still possible to use the same pool
            if (parent is RecyclerView) {
                recyclerView.setRecycledViewPool(parent.recycledViewPool)
            }
            adapt = AdaptRecyclerView.init(recyclerView)
        }
    }

    override fun bind(holder: Holder) {
        holder.adapt.setItems(items)

        // state can persist between recycled stages, so it is required to reset state for
        //  potentially new view (if there is state associated with current id, then it will be restored)
        (holder.recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(0)

        ViewState.process(id(), holder.itemView())
    }

    companion object {
        @Suppress("USELESS_CAST")
        fun hash(items: List<Item<*>>): Long {
            val array = items.map(Item<*>::id)
                .map { it as Any }
                .toMutableList()
                .apply { add(0, CollectionItem::class as Any) }
                .toTypedArray()
            return hash(array)
        }
    }


}