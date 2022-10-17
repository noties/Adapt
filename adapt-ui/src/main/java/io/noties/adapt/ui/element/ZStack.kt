package io.noties.adapt.ui.element

import android.widget.FrameLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * @see FrameLayout
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.ZStack(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
): ViewElement<FrameLayout, LP> = ElementGroup(
    ElementViewFactory.ZStack,
    children
)