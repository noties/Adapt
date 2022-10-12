package io.noties.adapt.ui.element

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ScrollView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.VScroll(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<ScrollView, LP> {
    return ViewElement<ScrollView, LP> {
        ElementViewFactory.VScroll(it).also { sv ->
            ViewFactory.addChildren(sv, children)
        }
    }.also(elements::add)
}

/**
 * @see ScrollView.setFillViewport
 */
fun <V : ScrollView, LP : LayoutParams> ViewElement<V, LP>.fillViewPort(
    fillViewPort: Boolean = true
): ViewElement<V, LP> = onView {
    this.isFillViewport = fillViewPort
}