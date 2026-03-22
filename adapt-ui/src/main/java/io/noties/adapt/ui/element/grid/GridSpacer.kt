package io.noties.adapt.ui.element.grid

import android.widget.FrameLayout
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.widget.grid.GridRowLayout

@Suppress("FunctionName", "FINAL_UPPER_BOUND")
fun <LP : GridRowLayout.LayoutParams> ViewFactory<LP>.GridSpacer(
    children: ViewFactory<FrameLayout.LayoutParams>.() -> Unit = {}
) = ElementGroup(
    provider = { io.noties.adapt.ui.widget.grid.GridSpacer(it) },
    children = children
)