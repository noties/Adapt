package io.noties.adapt.sample.samples.listview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ListView
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewLayout
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210122143237",
    title = "ListView",
    description = "Usage inside <tt><b>ListView</b></tt>",
    tags = ["listview"]
)
class ListViewSample : SampleViewLayout() {

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {
        val listView: ListView = view.findViewById(R.id.list_view)
        val adapt = AdaptListView.init(listView)

        initSampleItems(adapt)
    }

}

@Preview
@Suppress("ClassName", "unused")
private class Preview__ListViewSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = ListViewSample()
}