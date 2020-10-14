package io.noties.adapt.sample.samples

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.recyclerview.StickyItemDecoration
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.items.SectionItem
import java.util.*

class SampleRecyclerViewSticky : AbsSampleView() {

    override val sample: Sample = Sample(
        "Sticky",
        text("<b>Sticky</b> item decoration in <tt><b>RecyclerView</b></tt>"),
        ::SampleRecyclerViewSticky
    )

    override val layoutResId: Int = R.layout.view_sample_recycler_view_sticky

    override fun render(view: View) {
        // sticky item is actually added to layout, so we need ViewGroup to contain it
        //  (recyclerView and this viewGroup must have equal dimensions)
        val container = view.findViewById<ViewGroup>(R.id.recycler_container)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        // create item (can be a mock, data will be taken from real list of items)
        val decoration = StickyItemDecoration.create(container, SectionItem("Mock"))
        recyclerView.addItemDecoration(decoration)

        val adapt = AdaptRecyclerView.init(recyclerView)

        val items = ItemGenerator.next(0).toMutableList().apply {
            add(
                ControlItem(
                    adapt,
                    {
                        val items = adapt.items().toMutableList().apply {
                            val s = size
                            add(SectionItem(Date().toString()))
                            addAll(ItemGenerator.next(s))
                        }
                        adapt.setItems(items)
                    }
                )
            )
        }
        adapt.setItems(items)
    }
}