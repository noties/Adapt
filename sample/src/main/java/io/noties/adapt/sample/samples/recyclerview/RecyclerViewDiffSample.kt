package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.recyclerview.DiffUtilDataSetChangedHandler
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210122143122",
    title = "DiffUtil",
    description = "<b>DiffUtil</b> with the <b><tt>RecyclerView</tt></b>",
    tags = ["recyclerview"]
)
class RecyclerViewDiffSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        val adapt = AdaptRecyclerView.init(recyclerView) {
            it.dataSetChangeHandler(DiffUtilDataSetChangedHandler.create(true))
        }

        initSampleItems(adapt)
    }
}