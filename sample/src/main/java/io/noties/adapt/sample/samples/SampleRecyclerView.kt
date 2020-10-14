package io.noties.adapt.sample.samples

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample
import io.noties.adapt.sample.SampleViewList

class SampleRecyclerView : AbsSampleView() {

    override val sample = Sample(
        "RecyclerView",
        text("Usage of multiple items inside <b>RecyclerView</b>"),
        ::SampleRecyclerView
    )

    override val layoutResId = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val adapt = AdaptRecyclerView.init(recyclerView)
        adapt.setItems(listOf(SampleViewList.SampleItem(sample)))
    }
}