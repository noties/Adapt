package io.noties.adapt.ui.element

import android.widget.FrameLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren

/**
 * @see FrameLayout
 */
@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.ZStack(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<FrameLayout, LP> {
    return ViewElement<FrameLayout, LP> {
        ElementViewFactory.ZStack(it).also { fl ->
            ViewFactory.addChildren(fl, children)
        }
    }.also(elements::add)
}