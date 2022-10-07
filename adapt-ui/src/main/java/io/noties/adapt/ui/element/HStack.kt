package io.noties.adapt.ui.element

import android.view.ViewGroup
import android.widget.LinearLayout
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.util.Gravity

/**
 * Element for [LinearLayout] in HORIZONTAL orientation
 */
@Suppress("FunctionName", "unused")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.HStack(
    gravity: Gravity = Gravity.center.leading,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
): ViewElement<LinearLayout, LP> {
    return ViewElement<LinearLayout, LP> {
        LinearLayout(it).also { ll ->
            ll.orientation = LinearLayout.HORIZONTAL
            ll.gravity = gravity.gravityValue
            ViewFactory.addChildren(ll, children)
        }
    }.also(elements::add)
}