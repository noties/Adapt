package io.noties.adapt.sample.items.wrapper

import android.graphics.drawable.ColorDrawable
import io.noties.adapt.Item
import io.noties.adapt.Item.WrapperBuilder
import io.noties.adapt.wrapper.ItemWrapper

class ColorBackgroundWrapper(val color: Int, item: Item<*>) : ItemWrapper(item) {

    companion object {
        fun create(color: Int): WrapperBuilder =
            WrapperBuilder {
                ColorBackgroundWrapper(color, it)
            }
    }

    override fun bind(holder: Holder) {
        super.bind(holder)

        val drawable = (holder.itemView().background as? ColorDrawable) ?: ColorDrawable(0).also {
            holder.itemView().background = it
        }
        drawable.color = color
    }
}

fun Item<*>.backgroundColor(color: Int): Item<*> = wrap(ColorBackgroundWrapper.create(color))