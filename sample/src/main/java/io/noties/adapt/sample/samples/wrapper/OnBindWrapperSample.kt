package io.noties.adapt.sample.samples.wrapper

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.debug.Debug

@AdaptSample(
    id = "20220220214955",
    title = "OnBindWrapper",
    description = "Add <tt>OnBindWrapper</tt> to each generated item",
    tags = ["wrapper"]
)
class OnBindWrapperSample : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup)

        initSampleItems(
            adapt,
            processItem = { item ->
                item.onBind {
                    Debug.i("On bind of $item with holder=$it, adapt=${it.adapt()}")
                }
            }
        )
    }
}