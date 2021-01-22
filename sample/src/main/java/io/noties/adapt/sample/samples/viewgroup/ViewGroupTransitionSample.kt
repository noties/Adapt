package io.noties.adapt.sample.samples.viewgroup

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.TransitionChangeHandler

@AdaptSample(
    id = "20210122143311",
    title = "ViewGroup Transition",
    description = "Changes inside <tt><b>ViewGroup</b></tt> are animated by automatic <tt>Transition</tt>",
    tags = ["viewgroup", "transition"]
)
class ViewGroupTransitionSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup: ViewGroup = view.findViewById(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup) {
            it.changeHandler(TransitionChangeHandler.create())
        }
        adapt.setItems(initialItems(adapt))
    }
}