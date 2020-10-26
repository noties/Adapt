package io.noties.adapt.sample.samples

import android.view.View
import android.widget.ListView
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample

class SampleListViewAllEnabled : AbsSampleView() {

    override val sample: Sample = Sample(
        "ListView all enabled",
        text("All items enabled via special configuration option (affects native ListView divider)"),
        ::SampleListViewAllEnabled
    )

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {
        val listView: ListView = view.findViewById(R.id.list_view)
        val adapt = AdaptListView.init(listView) {
            it.areAllItemsEnabled(true)
        }
        adapt.setItems(initialItems(adapt))
    }
}