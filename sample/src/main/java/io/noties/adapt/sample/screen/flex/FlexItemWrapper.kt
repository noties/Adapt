package io.noties.adapt.sample.screen.flex

import com.google.android.flexbox.FlexboxLayout
import io.noties.adapt.Item
import io.noties.adapt.ItemWrapper

class FlexItemWrapper<H : Item.Holder>(private val flex: Int, item: Item<H>) : ItemWrapper<H>(item) {
    override fun render(holder: H) {
        super.render(holder)

        // unfortunately it's not possible to use the same wrapper for regular FlexLayout and FlexLayoutManager
        // as FlexItem interface that both LayoutParams implement is package-private (limited to library)
        val params = holder.itemView.layoutParams as? FlexboxLayout.LayoutParams
                ?: throw IllegalStateException("Not in FlexLayout")

        params.flexBasisPercent = 1.0F / 6 * flex

        holder.itemView.requestLayout()
    }
}