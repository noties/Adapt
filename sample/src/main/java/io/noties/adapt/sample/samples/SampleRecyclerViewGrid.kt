package io.noties.adapt.sample.samples

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample
import io.noties.adapt.sample.items.ControlItem

class SampleRecyclerViewGrid : AbsSampleView() {
    override val sample: Sample = Sample(
        "Grid",
        text("<b><tt>RecyclerView</tt></b> with <tt>GridLayoutManager</tt>"),
        ::SampleRecyclerViewGrid
    )

    override val layoutResId: Int = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.layoutManager = layoutManager
        val adapt = AdaptRecyclerView.init(recyclerView)
        val adapter = adapt.adapter()

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

            private val controlViewType =
                AdaptRecyclerView.assignedViewType(ControlItem::class.java)

            override fun getSpanSize(position: Int): Int {
                if (controlViewType == adapter.getItemViewType(position)) {
                    return 2
                }
                return 1
            }
        }

        adapt.setItems(initialItems(adapt))
    }
}