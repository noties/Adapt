package io.noties.adapt.sample.samples

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.TransitionChangeHandler

class ViewGroupTransitionSample: AbsSampleView() {

    override val sample: Sample = Sample(
        "ViewGroup Transition",
        text("Changes inside <tt><b>ViewGroup</b></tt> are animated by automatic <tt>Transition</tt>"),
        ::ViewGroupTransitionSample
    )

    override val layoutResId: Int = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup: ViewGroup = view.findViewById(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup) {
            it.changeHandler(TransitionChangeHandler.create())
        }
        adapt.setItems(initialItems(adapt))
    }
}