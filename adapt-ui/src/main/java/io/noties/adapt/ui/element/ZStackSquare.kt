package io.noties.adapt.ui.element

import android.widget.FrameLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.widget.SquareFrameLayout

/**
 * @see SquareFrameLayout
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.ZStackSquare(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
) = ElementGroup(
    { SquareFrameLayout(it) },
    children
)