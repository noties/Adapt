package io.noties.adapt.ui.element

import android.view.ViewGroup
import android.widget.FrameLayout
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren

@Suppress("FunctionName", "unused")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.ZStack(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<FrameLayout, LP> {
    return ViewElement<FrameLayout, LP> {
        FrameLayout(it).also { fl ->
            ViewFactory.addChildren(fl, children)
        }
    }.also(elements::add)
}