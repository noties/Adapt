package io.noties.adapt.ui.widget

import android.content.Context
import android.widget.FrameLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.ZStackSquare(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
) = ElementGroup(
    { SquareFrameLayout(it) },
    children
)

/**
 * Square Frame layout that will use `width` as the base and assign height to be the same
 */
class SquareFrameLayout(context: Context) : FrameLayout(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}