package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
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
    id = "20210124212119",
    title = "Sticky GRID",
    description = "<tt>StickyItemDecoration</tt> with <tt>GridLayoutManager</tt>",
    tags = ["recyclerview", "grid"]
)
class RecyclerViewStickyGridSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_recycler_view_sticky

    override fun render(view: View) {

        // sticky item is actually added to layout, so we need ViewGroup to contain it
        //  (recyclerView and this viewGroup must have equal dimensions)
        val container = view.findViewById<ViewGroup>(R.id.recycler_container)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        // NB! sticky item must take all spans
        val spanCount = 2
        recyclerView.layoutManager =
            GridLayoutManager(view.context, spanCount).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

                    val sectionViewType =
                        AdaptRecyclerView.assignedViewType(SectionItem::class.java)

                    override fun getSpanSize(position: Int): Int {
                        return if (sectionViewType == recyclerView.adapter?.getItemViewType(position)) {
                            spanCount
                        } else {
                            1
                        }
                    }
                }
            }

        // create item (can be a mock, data will be taken from real list of items)
        val decoration = StickyItemDecoration.create(container, SectionItem("Mock"))
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