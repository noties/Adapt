package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Item
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.recyclerview.DiffUtilDataSetChangedHandler
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.CardBigItem
import io.noties.adapt.sample.items.CollectionItem
import io.noties.adapt.sample.items.ControlItem

@AdaptSample(
    id = "20210122143154",
    title = "Nested Recycler",
    description = "Item contains nested <tt><b>RecyclerView</b></tt>, shared <tt>RecyclerViewPool</tt>",
    tags = ["recyclerview"]
)
class RecyclerViewNestedSample() : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapt = AdaptRecyclerView.init(recyclerView) {
            it.dataSetChangeHandler(DiffUtilDataSetChangedHandler.create(true))
        }

        fun addNewItems(items: List<Item<*>>) {
            val list = adapt.items().toMutableList().apply {
                addAll(items.map { item ->
                    if (item is CardBigItem) {
                        val list = mutableListOf<Item<*>>(item).apply {
                            addAll(ItemGenerator.next(0))
                        }
                        CollectionItem(list)
                    } else {
                        item
                    }
                })
            }
            adapt.setItems(list)
        }

        val generated = ItemGenerator.next(0).toMutableList().apply {
            add(ControlItem(
                { addNewItems(ItemGenerator.next(adapt.items().size)) },
                { adapt.setItems(ControlItem.shuffledItems(adapt.items())) }
            ))
        }
        adapt.setItems(generated)
    }
}