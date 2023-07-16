package io.noties.adapt.sample.samples.listview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView

@AdaptSample(
    id = "20210126234155",
    title = "GridView",
    tags = ["listview"]
)
class GridViewSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_grid_view

    override fun render(view: View) {
        val gridView: GridView = view.findViewById(R.id.grid_view)
        val adapt = AdaptListView.init(gridView)

        initSampleItems(adapt)
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__GridViewSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = GridViewSample()
}