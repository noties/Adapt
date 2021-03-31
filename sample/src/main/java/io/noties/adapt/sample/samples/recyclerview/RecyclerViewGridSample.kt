package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.Item
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.ControlItem

@AdaptSample(
    id = "20210122143147",
    title = "Grid",
    description = "<b><tt>RecyclerView</tt></b> with <tt>GridLayoutManager</tt>",
    tags = ["recyclerview", "grid"]
)
class RecyclerViewGridSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.layoutManager = layoutManager
        val adapt = AdaptRecyclerView.init(recyclerView)
        val adapter = adapt.adapter()

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

            private val controlViewType = Item.Key
                .single(ControlItem::class.java)
                .viewType()

            override fun getSpanSize(position: Int): Int {
                if (controlViewType == adapter.getItemViewType(position)) {
                    return 2
                }
                return 1
            }
        }

        initSampleItems(adapt)
    }
}