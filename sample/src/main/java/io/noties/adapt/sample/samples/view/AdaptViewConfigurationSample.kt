package io.noties.adapt.sample.samples.view

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.view.AdaptView

@AdaptSample(
    id = "20230109182559",
    title = "AdaptView configuration",
    tags = ["view"]
)
class AdaptViewConfigurationSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        val container: ViewGroup = view.findViewById(R.id.view_group)

        // adaptView must contain Item of the same type only
        val item = ItemGenerator.next(0).first()
        val adaptView = AdaptView.init(container) {
            it.item(item)
            it.changeHandlerTransitionParent()
        }

        fun bind() {
            adaptView.view().setOnClickListener {

                adaptView.setItem(ItemGenerator.next(0).first())
                // internal view to which we did set onClickListener is not changed
                //  and we need new listener set
                bind()
            }
        }
        bind()
    }
}