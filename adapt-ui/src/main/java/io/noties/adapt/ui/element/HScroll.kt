package io.noties.adapt.ui.element

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren

/**
 * Element for [HorizontalScrollView].
 * NB! this shares the condition to have only one direct child
 * @see fillViewPort
 */
@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.HScroll(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<HorizontalScrollView, LP> {
    return ViewElement<HorizontalScrollView, LP> {
        ElementViewFactory.HScroll(it).also { hsv ->
            ViewFactory.addChildren(hsv, children)
        }
    }.also(elements::add)
}

/**
 * @see HorizontalScrollView.setFillViewport
 */
fun <V : HorizontalScrollView, LP : LayoutParams> ViewElement<V, LP>.fillViewPort(
    fillViewPort: Boolean = true
): ViewElement<V, LP> = onView {
    isFillViewport = fillViewPort
}