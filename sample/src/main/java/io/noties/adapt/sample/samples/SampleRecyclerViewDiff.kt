package io.noties.adapt.sample.samples

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.recyclerview.DiffUtilDataSetChangedHandler
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample

class SampleRecyclerViewDiff : AbsSampleView() {

    override val sample: Sample = Sample(
        "DiffUtil",
        text("<b>DiffUtil</b> with the <b><tt>RecyclerView</tt></b>"),
        ::SampleRecyclerViewDiff
    )

    override val layoutResId: Int = R.layout.view_sample_recycler_view

    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        val adapt = AdaptRecyclerView.init(recyclerView) {
            it.dataSetChangeHandler(DiffUtilDataSetChangedHandler.create(true))
        }
        adapt.setItems(initialItems(adapt))
    }
}