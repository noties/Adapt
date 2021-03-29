package io.noties.adapt.sample.samples.viewgroup

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.items.NoIdItem
import io.noties.adapt.viewgroup.AdaptViewGroup

@AdaptSample(
    "20210122143249",
    "NO_ID in ViewGroup",
    "Indicates that an Item without ID (<tt>NO_ID</tt>) is recreated with each update",
    tags = ["viewgroup"]
)
class ViewGroupNoIdSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup)

        val items = ItemGenerator.next(0)
            .toMutableList()
            .apply {
                add(ControlItem.init(adapt))
                add(NoIdItem())
            }
        adapt.setItems(items)
    }
}