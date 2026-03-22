@file:Suppress("FINAL_UPPER_BOUND")

package io.noties.adapt.ui.element.grid

import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.widget.grid.GridLayout

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Grid(
    children: ViewFactory<GridLayout.LayoutParams>.() -> Unit
) = ElementGroup(
    provider = { GridLayout(it) },
    children = children
)


fun <V : GridLayout, LP : LayoutParams> ViewElement<V, LP>.gridSpacing(
    spacing: Int
) = gridSpacing(vertical = spacing, horizontal = spacing)

fun <V : GridLayout, LP : LayoutParams> ViewElement<V, LP>.gridSpacing(
    vertical: Int? = null,
    horizontal: Int? = null
) = this.onView { v ->
    vertical?.dip?.also { v.verticalSpacingPx = it }
    horizontal?.dip?.also { v.horizontalSpacingPx = it }
}