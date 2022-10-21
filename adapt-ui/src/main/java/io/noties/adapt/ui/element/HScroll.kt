package io.noties.adapt.ui.element

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * Element for [HorizontalScrollView].
 * NB! this shares the condition to have only one direct child
 * @see scrollFillViewPort
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.HScroll(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<HorizontalScrollView, LP> = ElementGroup(
    ElementViewFactory.HScroll::invoke,
    children
)

/**
 * @see HorizontalScrollView.setFillViewport
 */
fun <V : HorizontalScrollView, LP : LayoutParams> ViewElement<V, LP>.scrollFillViewPort(
    fillViewPort: Boolean = true
): ViewElement<V, LP> = onView {
    isFillViewport = fillViewPort
}