package io.noties.adapt.sample.samples.listview

import android.view.View
import android.widget.ListView
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210122143232",
    title = "ListView all enabled",
    description = "All items enabled via special configuration option (affects native ListView divider)",
    tags = ["listview"]
)
class ListViewAllEnabledSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {
        val listView: ListView = view.findViewById(R.id.list_view)
        val adapt = AdaptListView.init(listView) {
            it.areAllItemsEnabled(true)
        }

        initSampleItems(adapt)
    }
}