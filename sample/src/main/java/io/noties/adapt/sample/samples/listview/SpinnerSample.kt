package io.noties.adapt.sample.samples.listview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.Spinner
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.PlainItem
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView

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

        // `createSingleViewType` can be used for list with single item and which is disabled
        //  which does not make _much_ sense for Spinner (as item won't be clickable)
//        val adapt = AdaptListView.createSingleViewType(view.context)

        val adapt = AdaptListView.create(view.context) {
            it.include(PlainItem::class.java, true)
        }

        spinner.adapter = adapt.adapter()

        val items = (0 until 100)
            .toList()
            .map {
                PlainItem("A$it", Color.RED, "Item #$it")
            }

        adapt.setItems(items)
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__SpinnerSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = SpinnerSample()
}