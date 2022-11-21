package io.noties.adapt.ui.element

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ScrollView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.VScroll(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<ScrollView, LP> = ElementGroup(
    ElementViewFactory.VScroll,
    children
)

/**
 * @see ScrollView.setFillViewport
 */
fun <V : ScrollView, LP : LayoutParams> ViewElement<V, LP>.scrollFillViewPort(
    fillViewPort: Boolean = true
): ViewElement<V, LP> = onView {
    this.isFillViewport = fillViewPort
}