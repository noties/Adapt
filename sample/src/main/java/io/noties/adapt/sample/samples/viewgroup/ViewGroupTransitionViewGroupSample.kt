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
import io.noties.adapt.viewgroup.TransitionChangeHandler

@AdaptSample(
    id = "20210205204648",
    title = "ViewGroupProvider",
    description = "Begin transition in <tt>TransitionChangeHandler</tt> in a different " +
            "<tt>ViewGroup</tt> (not the one is used " +
            "to initialize Adapt) with the <tt>ViewGroupProvider</tt>",
    tags = ["viewgroup", "transition"]
)
class ViewGroupTransitionViewGroupSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup: ViewGroup = view.findViewById(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup) {
            it.changeHandler(TransitionChangeHandler.createTransitionOnParent())
        }

        initSampleItems(adapt)
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__ViewGroupTransitionViewGroupSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = ViewGroupTransitionViewGroupSample()
}