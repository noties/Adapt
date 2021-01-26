package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.recyclerview.StickyItemDecoration
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.items.SectionItem
import java.util.*

@AdaptSample(
    id = "20210122143205",
    title = "Sticky",
    description = "<b>Sticky</b> item decoration in <tt><b>RecyclerView</b></tt>",
    tags = ["recyclerview"]
)
open class RecyclerViewStickySample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_recycler_view_sticky

    override fun render(view: View) {
        // sticky item is actually added to layout, so we need ViewGroup to contain it
        //  (recyclerView and this viewGroup must have equal dimensions)
//        val container = view.findViewById<ViewGroup>(R.id.recycler_container)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        // create item (can be a mock, data will be taken from real list of items)
        val decoration = StickyItemDecoration.create(recyclerView, SectionItem("Mock"))
        recyclerView.addItemDecoration(decoration)

        val adapt = AdaptRecyclerView.init(recyclerView)

        val items = ItemGenerator.next(0).toMutableList().apply {
            add(
                ControlItem(
                    {
                        val items = adapt.items().toMutableList().apply {
                            val s = size
                            add(SectionItem(Date().toString()))
                            addAll(ItemGenerator.next(s))
                        }
                        adapt.setItems(items)
                    },
                    { adapt.setItems(ControlItem.shuffledItems(adapt.items())) }
                )
            )
        }
        adapt.setItems(items)
    }
}