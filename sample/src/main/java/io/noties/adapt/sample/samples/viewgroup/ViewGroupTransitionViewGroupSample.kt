package io.noties.adapt.sample.samples.viewgroup

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.ParentViewGroupProvider
import io.noties.adapt.viewgroup.TransitionChangeHandler

@AdaptSample(
    id = "20210205204648",
    title = "TransitionChangeHandler and ViewGroupProvider",
    description = "Begin transition in a different <tt>ViewGroup</tt> (not the one is used " +
            "to initialize Adapt) with the <tt>ViewGroupProvider</tt>"
)
class ViewGroupTransitionViewGroupSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup: ViewGroup = view.findViewById(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup) {
            it.changeHandler(TransitionChangeHandler.create { configuration ->
                configuration.viewGroupProvider(ParentViewGroupProvider())
            })
        }
        adapt.setItems(initialItems(adapt))
    }
}