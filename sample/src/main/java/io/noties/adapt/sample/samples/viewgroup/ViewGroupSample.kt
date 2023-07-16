package io.noties.adapt.sample.samples.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.viewgroup.AdaptViewGroup

@AdaptSample(
    id = "20210122143305",
    title = "ViewGroup",
    description = "Usage of Adapt inside <tt><b>ViewGroup</b></tt> (<tt>LinearLayout</tt> wrapped inside <tt>ScrollView</tt>)",
    tags = ["viewgroup"]
)
class ViewGroupSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup)

        initSampleItems(adapt)
    }

}

@Preview
@Suppress("ClassName", "unused")
private class Preview__ViewGroupSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = ViewGroupSample()
}