package io.noties.adapt.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.element.VStackDefaultGravity
import io.noties.adapt.ui.util.Gravity

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.VStackReverseDrawingOrder(
    gravity: Gravity = VStackDefaultGravity,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
) = ElementGroup(
    {
        LinearLayoutReverseDrawingOrder(it).also { ll ->
            ll.orientation = LinearLayout.VERTICAL
            ll.gravity = gravity.value
        }
    },
    children
)

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