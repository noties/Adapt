package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.util.Gravity

/**
 * @see LinearLayout
 */
@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.VStack(
    gravity: Gravity = Gravity.center.top,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
): ViewElement<LinearLayout, LP> {
    return ViewElement<LinearLayout, LP> {
        ElementViewFactory.VStack(it).also { ll ->
            ll.orientation = LinearLayout.VERTICAL
            ll.gravity = gravity.value
            ViewFactory.addChildren(ll, children)
        }
    }.also(elements::add)
}