package io.noties.adapt.sample.samples.wrapper

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewLayout
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.util.dip
import io.noties.adapt.util.Edges
import io.noties.adapt.viewgroup.AdaptViewGroup

@AdaptSample(
    id = "20220404133658",
    title = "MarginWrapper",
    description = "<tt>MarginWrapper</tt> with different <tt>Edges</tt> usage"
)
class MarginWrapperSample : SampleViewLayout() {

    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup)

        val item = ItemGenerator.next(0).first()

        val items = listOf(
            item.id(1L)
                .background(0x20FF0000)
                .margin(Edges.vertical(48.dip))
                .frame()
                .background(0x2000ff00)
                .margin(Edges.horizontal(96.dip))
                .frame()
                .background(0x200000ff)
        )

        adapt.setItems(items)
    }
}