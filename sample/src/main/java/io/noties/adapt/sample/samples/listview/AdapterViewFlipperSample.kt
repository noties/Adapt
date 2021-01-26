package io.noties.adapt.sample.samples.listview

import android.view.View
import android.widget.AdapterViewFlipper
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample

@AdaptSample(
    id = "20210126232916",
    title = "AdapterViewFlipper",
    tags = ["listview"]
)
class AdapterViewFlipperSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_adapter_view_flipper

    override fun render(view: View) {
        val flipper: AdapterViewFlipper = view.findViewById(R.id.adapter_view_flipper)
        val adapt = AdaptListView.init(flipper)
        adapt.setItems(initialItems(adapt))

        flipper.flipInterval = 1000
        flipper.startFlipping()
    }
}