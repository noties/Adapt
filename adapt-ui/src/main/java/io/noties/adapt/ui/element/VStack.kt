package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.util.Gravity

/**
 * @see LinearLayout
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.VStack(
    gravity: Gravity = VStackDefaultGravity,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
): ViewElement<LinearLayout, LP> = ElementGroup(
    ElementViewFactory.VStack,
    {
        it.orientation = LinearLayout.VERTICAL
        it.gravity = gravity.value
    },
    children
)

val VStackDefaultGravity: Gravity get() = Gravity.center.top