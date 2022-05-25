package io.noties.adapt.ui.element

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.HScroll(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<HorizontalScrollView, LP> {
    return ViewElement<HorizontalScrollView, LP> {
        HorizontalScrollView(it).also { hsv ->
            ViewFactory.addChildren(hsv, children)
        }
    }.also(elements::add)
}

fun <V : HorizontalScrollView, LP : LayoutParams> ViewElement<V, LP>.fillViewPort(
    fillViewPort: Boolean
): ViewElement<V, LP> = onView {
    isFillViewport = fillViewPort
}