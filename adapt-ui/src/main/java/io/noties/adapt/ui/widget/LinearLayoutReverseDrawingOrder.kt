package io.noties.adapt.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Version of vertical `LinearLayout` that draws children in reverse order (from bottom to top).
 * This allows _sticking_ prior views on top of others. For example, like in [io.noties.adapt.ui.sticky.StickyVerticalScroll]
 * which allows sticking section-views in a scrollable list
 *
 * @see io.noties.adapt.ui.element.VStackReverseDrawingOrder
 *
 */
// a reverse linear layout would actually help keeping the top view on top
open class LinearLayoutReverseDrawingOrder : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        isChildrenDrawingOrderEnabled = true
    }

    public override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        // 10-0 => 9
        // 10-1 => 8
        return childCount - drawingPosition - 1
    }
}