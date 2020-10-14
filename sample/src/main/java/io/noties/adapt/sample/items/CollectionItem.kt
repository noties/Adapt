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
import java.util.*

class CollectionItem(val items: List<Item<*>>) : Item<CollectionItem.Holder>(hash(items)) {

    class Holder(view: View) : Item.Holder(view) {
        val recyclerView: RecyclerView = requireView(R.id.recycler_view)
        val adapt = AdaptRecyclerView.init(recyclerView)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        val view = inflater.inflate(R.layout.item_collection, parent, false)
        return Holder(view).apply {
            recyclerView.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
            if (parent is RecyclerView) {
                recyclerView.setRecycledViewPool(parent.recycledViewPool)
            }
        }
    }

    override fun render(holder: Holder) {
        holder.adapt.setItems(items)

        // state can persist between recycled stages, so it is required to reset state for
        //  potentially new view (if there is state associated with current id, then it will be restored)
        (holder.recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(0)

        ViewState.process(id(), holder.itemView())
    }

    companion object {
        fun hash(items: List<Item<*>>): Long {
            val array = items.map(Item<*>::id)
                .map { it as Any }
                .toMutableList()
                .apply { add(0, CollectionItem::class as Any) }
                .toTypedArray()
            return Objects.hash(array).toLong()
        }
    }
}