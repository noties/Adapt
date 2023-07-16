package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.util.Gravity

/**
 * Element for [LinearLayout] in HORIZONTAL orientation
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.HStack(
    gravity: Gravity = HStackDefaultGravity,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
): ViewElement<LinearLayout, LP> = ElementGroup(
    ElementViewFactory.HStack,
    {
        it.orientation = LinearLayout.HORIZONTAL
        it.gravity = gravity.value
    },
    children
)