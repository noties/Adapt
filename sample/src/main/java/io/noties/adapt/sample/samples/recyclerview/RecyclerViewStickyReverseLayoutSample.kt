package io.noties.adapt.sample.samples.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210126223631",
    title = "Sticky (reverse)",
    description = "<tt>reverseLayout</tt> in RecyclerView with <tt>LinearLayoutManager</tt>",
    tags = ["recyclerview"]
)
class RecyclerViewStickyReverseLayoutSample : RecyclerViewStickySample() {
    override fun render(view: View) {
        super.render(view)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        (recyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
    }
}