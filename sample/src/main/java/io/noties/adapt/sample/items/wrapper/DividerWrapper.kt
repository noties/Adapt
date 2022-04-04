package io.noties.adapt.sample.items.wrapper

import io.noties.adapt.Item
import io.noties.adapt.wrapper.ItemWrapper
import io.noties.adapt.sample.ui.DividerOverlay

class DividerWrapper(item: Item<*>) : ItemWrapper(item) {
    override fun bind(holder: Holder) {
        super.bind(holder)

        DividerOverlay.init(holder.itemView())
    }
}