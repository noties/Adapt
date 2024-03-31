package io.noties.adapt.ui.element

import android.view.View
import android.widget.FrameLayout
import android.widget.ScrollView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.widget.FrameLayoutWrapHeightOrScroll

/**
 * @see FrameLayoutWrapHeightOrScroll
 */
@Suppress("FunctionName")
fun <V : View, LP : LayoutParams> ViewFactory<LP>.ZStackWrapHeightOrScroll(
    scrollStyle: (ViewElement<ScrollView, FrameLayout.LayoutParams>) -> Unit = {},
    child: ViewFactory<FrameLayout.LayoutParams>.() -> ViewElement<V, FrameLayout.LayoutParams>
) = Element(
    provider = { FrameLayoutWrapHeightOrScroll(it, scrollStyle, child) }
)