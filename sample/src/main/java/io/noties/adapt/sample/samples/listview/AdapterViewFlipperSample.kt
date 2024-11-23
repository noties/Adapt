package io.noties.adapt.sample.samples.listview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterViewFlipper
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewLayout
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210126232916",
    title = "AdapterViewFlipper",
    tags = ["listview"]
)
class AdapterViewFlipperSample : SampleViewLayout() {
    override val layoutResId: Int
        get() = R.layout.view_sample_adapter_view_flipper

    override fun render(view: View) {
        val flipper: AdapterViewFlipper = view.findViewById(R.id.adapter_view_flipper)
        val adapt = AdaptListView.init(flipper)

        initSampleItems(adapt)

        flipper.flipInterval = 1000
        flipper.startFlipping()
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdapterViewFlipperSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdapterViewFlipperSample()
}