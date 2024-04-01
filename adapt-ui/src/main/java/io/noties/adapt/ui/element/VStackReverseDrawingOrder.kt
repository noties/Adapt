package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.widget.LinearLayoutReverseDrawingOrder

/**
 * @see LinearLayoutReverseDrawingOrder
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.VStackReverseDrawingOrder(
    gravity: Gravity = VStackDefaultGravity,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
) = ElementGroup(
    {
        LinearLayoutReverseDrawingOrder(it).also { ll ->
            ll.orientation = LinearLayout.VERTICAL
            ll.gravity = gravity.rawValue
        }
    },
    children
)