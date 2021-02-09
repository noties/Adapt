package io.noties.adapt.sample.items.wrapper

import android.graphics.drawable.ColorDrawable
import io.noties.adapt.ItemWrapper

class ColorBackgroundWrapper(val color: Int, provider: Provider) : ItemWrapper(provider.provide()) {
    override fun bind(holder: Holder) {
        super.bind(holder)

        val drawable = (holder.itemView().background as? ColorDrawable) ?: ColorDrawable(0).also {
            holder.itemView().background = it
        }
        drawable.color = color
    }
}