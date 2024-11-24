package io.noties.adapt.ui.widget

import android.content.Context
import android.widget.FrameLayout

/**
 * Square Frame layout that will use `axis` dimension as the base for other dimension to follow.
 * For example, when axis =  horizontal, then SquareFrameLayout would use `width` as the base,
 * and when axis = vertical -> `height`
 * @see io.noties.adapt.ui.element.ZStackSquare
 */
class SquareFrameLayout(context: Context) : FrameLayout(context) {

    enum class Axis {
        Horizontal,
        Vertical;

        companion object {
            val horizontal get() = Horizontal
            val vertical get() = Vertical
        }
    }

    var axis: Axis = Axis.Horizontal
        set(value) {
            field = value
            requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // take size and ignore the mode, as this layout takes all available space always
        //  wrap_content is not supported, this is a static predefined layout, is not dynamic
        val sizeSpec = when (axis) {
            Axis.Horizontal -> widthMeasureSpec
            Axis.Vertical -> heightMeasureSpec
        }
        val size = MeasureSpec.getSize(sizeSpec)
        if (size == 0) {
            error(
                "SquareFrameLayout{axis:$axis} requires dimension specified, " +
                        "use match_parent or exact value - 24, 48, etc; " +
                        "widthSpec:${MeasureSpec.getSize(widthMeasureSpec)} " +
                        "heightSpec:${MeasureSpec.getSize(heightMeasureSpec)}"
            )
        }
        val spec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(spec, spec)
    }
}