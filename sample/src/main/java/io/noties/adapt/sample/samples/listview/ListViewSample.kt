package io.noties.adapt.sample.samples.listview

import android.view.View
import android.widget.ListView
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210122143237",
    title = "ListView",
    description = "Usage inside <tt><b>ListView</b></tt>",
    tags = ["listview"]
)
class ListViewSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {
        val listView: ListView = view.findViewById(R.id.list_view)
        val adapt = AdaptListView.init(listView)

        initSampleItems(adapt)
    }

}