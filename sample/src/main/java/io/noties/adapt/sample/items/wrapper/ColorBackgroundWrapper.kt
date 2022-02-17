package io.noties.adapt.sample.items.wrapper

import android.graphics.drawable.ColorDrawable
import io.noties.adapt.Item
import io.noties.adapt.Item.Wrapper
import io.noties.adapt.ItemWrapper

class ColorBackgroundWrapper(val color: Int, item: Item<*>) : ItemWrapper(item) {

    companion object {
        fun create(color: Int): Wrapper = Wrapper {
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