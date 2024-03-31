package io.noties.adapt.ui.widget

import android.content.Context
import android.widget.FrameLayout

/**
 * Square Frame layout that will use `width` as the base and assign height to be the same
 * @see io.noties.adapt.ui.element.ZStackSquare
 */
class SquareFrameLayout(context: Context) : FrameLayout(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}