package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.wrapper.DividerWrapper

@AdaptSample(
    id = "20210329212452",
    title = "Recycler divider item wrapper",
    description = "Sample usage of <tt>ItemWrapper</tt> to add dividers to items inside a <tt>RecyclerView</tt>",
    tags = ["recyclerview", "wrapper"]
)
class RecyclerViewDividerWrapperSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.also {
            it.layoutManager = LinearLayoutManager(view.context)
        }

        val adapt = AdaptRecyclerView.init(recyclerView)

        initSampleItems(
            adapt,
            processItem = {
                if (it !is DividerWrapper) {
                    DividerWrapper(it)
                } else {
                    it
                }
            }
        )
    }
}