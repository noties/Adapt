package io.noties.adapt.sample.samples.viewgroup

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ItemWrapper
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.items.wrapper.ColorBackgroundWrapper
import io.noties.adapt.sample.items.wrapper.PaddingWrapper
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.TransitionChangeHandler

@AdaptSample(
    id = "20210209021444",
    title = "ViewGroup and ItemWrapper",
    description = "Display wrapped items in a <tt>ViewGroup</tt>",
    tags = ["viewgroup", "wrapper"]
)
class ViewGroupWrappedSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_view_group

    override fun render(view: View) {
        val container: ViewGroup = view.findViewById(R.id.view_group)
        val adapt = AdaptViewGroup.init(container) {
            it.changeHandler(TransitionChangeHandler.create())
        }

        fun onAdded() {
            // replace first unwrapped item with Padding, then Background, then add more
            val items = adapt.items().toMutableList()
            // itemWrapper is used to find an item that IS NOT WRAPPED
            val noPadding = items.firstOrNull { it !is ItemWrapper }
            if (noPadding == null) {
                val noColor = items.firstOrNull { it !is ColorBackgroundWrapper }
                if (noColor == null) {
                    adapt.setItems(ControlItem.addedItems(items))
                } else {
                    items.remove(noColor)
                    items.add(ColorBackgroundWrapper(ItemGenerator.nextColor()) {
                        noColor
                    })
                    adapt.setItems(items)
                }
            } else {
                items.remove(noPadding)
                items.add(PaddingWrapper(100) {
                    noPadding
                })
                adapt.setItems(items)
            }
        }

        val items = ItemGenerator.next(0)
            .toMutableList()
            .also {
                it.add(ControlItem(::onAdded, ControlItem.shuffle(adapt)))
            }

        adapt.setItems(items)
    }
}