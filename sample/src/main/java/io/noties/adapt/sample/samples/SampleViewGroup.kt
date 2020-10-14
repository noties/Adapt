package io.noties.adapt.sample.samples

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample
import io.noties.adapt.viewgroup.AdaptViewGroup

class SampleViewGroup : AbsSampleView() {

    override val sample: Sample = Sample(
        "ViewGroup",
        text("Usage of Adapt inside <tt><b>ViewGroup</b></tt> (<tt>LinearLayout</tt> wrapped inside <tt>ScrollView</tt>)"),
        ::SampleViewGroup
    )

    override val layoutResId: Int = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup)
        adapt.setItems(initialItems(adapt))
    }

}