package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210122143200",
    title = "RecyclerView",
    description = "Usage of multiple items inside <tt><b>RecyclerView</b></tt>",
    tags = ["recyclerview"]
)
class RecyclerViewSample : SampleView() {

    override val layoutResId = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val adapt = AdaptRecyclerView.init(recyclerView)
        adapt.setItems(initialItems(adapt))
    }
}