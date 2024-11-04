package io.noties.adapt.ui.widget

import android.content.Context
import android.widget.FrameLayout

/**
 * Square Frame layout that will use `width` as the base and assign height to be the same
 * @see io.noties.adapt.ui.element.ZStackSquare
 */
class SquareFrameLayout(context: Context) : FrameLayout(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // take size and ignore the mode, as this layout takes all available space always
        //  wrap_content is not supported, this is a static predefined layout, is not dynamic
        val size = MeasureSpec.getSize(widthMeasureSpec)
        if (size == 0) {
            error("SquareFrameLayout requires width dimension specified, " +
                    "use match_parent or specific dimension value - 24, 48, etc")
        }
        val spec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(spec, spec)
    }
}