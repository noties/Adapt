package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.util.Gravity

@Suppress("FunctionName")
fun <LP: LayoutParams> ViewFactory<LP>.VScrollStack(
    gravity: Gravity = VStackDefaultGravity,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
) = VScroll {
    VStack(gravity, children)
        .layout(FILL, WRAP)
}