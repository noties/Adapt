package io.noties.adapt.sample.samples.listview

import android.graphics.Color
import android.view.View
import android.widget.Spinner
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.PlainItem

@AdaptSample(
    "20210123235615",
    "Spinner",
    "Usage with <tt>Spinner</tt>, (single view type only, Spinner requirement)",
    tags = ["listview"]
)
class SpinnerSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_spinner

    override fun render(view: View) {
        // Unfortunately spinner allows only one item view

        val spinner: Spinner = view.findViewById(R.id.spinner)
        val adapt = AdaptListView.createSingleViewType(view.context)
        spinner.adapter = adapt.adapter()

        val items = (0 until 100)
            .toList()
            .map {
                PlainItem("A$it", Color.RED, "Item #$it")
            }

        adapt.setItems(items)
    }
}