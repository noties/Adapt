package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.util.Gravity

@Suppress("FunctionName")
fun <LP: LayoutParams> ViewFactory<LP>.HScrollStack(
    gravity: Gravity = HStackDefaultGravity,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
) = HScroll {
    HStack(gravity, children)
        .layout(WRAP, FILL)
}