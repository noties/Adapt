package io.noties.adapt.sample.samples

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.view.AdaptView

@AdaptSample(
    id = "20210127234203",
    title = "AdaptView",
    description = "Usage of <tt>Item</tt> directly with <tt>AdaptView</tt>",
    tags = ["view"]
)
class ViewSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        val container: ViewGroup = view.findViewById(R.id.view_group)

        // adaptView must contain Item of the same type only
        val item = ItemGenerator.next(0).first()
        val adaptView = AdaptView.init(container, item)

        adaptView.view().setOnClickListener {
            adaptView.setItem(ItemGenerator.next(1).first())
        }
    }
}