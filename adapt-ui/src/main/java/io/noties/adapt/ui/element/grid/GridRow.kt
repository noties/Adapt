@file:Suppress("FINAL_UPPER_BOUND")

package io.noties.adapt.ui.element.grid

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.element.ZStackSquare
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.GravityBuilder
import io.noties.adapt.ui.widget.SquareFrameLayout
import io.noties.adapt.ui.widget.grid.GridLayout
import io.noties.adapt.ui.widget.grid.GridRowLayout

@Suppress("FunctionName")
fun <LP : GridLayout.LayoutParams> ViewFactory<LP>.GridRow(
    children: ViewFactory<GridRowLayout.LayoutParams>.() -> Unit
) = ElementGroup(
    provider = { GridRowLayout(it) },
    children = children
)

@Suppress("FunctionName")
fun <LP : GridRowLayout.LayoutParams> ViewFactory<LP>.Squares(
    count: Int,
    onEach: (ViewElement<SquareFrameLayout, LP>) -> Unit = {}
) {
    for (i in 0 until count) {
        ZStackSquare { }
            .also(onEach)
    }
}

@Suppress("FunctionName")
fun ViewFactory<GridRowLayout.LayoutParams>.Square() = ZStackSquare { }

@Suppress("FunctionName")
fun ViewFactory<GridRowLayout.LayoutParams>.Spacer() = GridSpacer()


fun <V : View, LP : GridRowLayout.LayoutParams> ViewElement<V, LP>.gridColumns(columns: Int) =
    this.onLayoutParams {
        it.spanColumns = columns
    }

fun <V : View, LP : GridRowLayout.LayoutParams> ViewElement<V, LP>.gridCellGravity(
    gravity: GravityBuilder
) = this.onLayoutParams {
    it.gravity = gravity(Gravity)
}

// Helper function to keep similar to other views API scope (layoutGravity is known and
//  might be the first to look for)
fun <V : View, LP : GridRowLayout.LayoutParams> ViewElement<V, LP>.layoutGravity(
    gravity: GravityBuilder
) = gridCellGravity(gravity)

fun <V : GridRowLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridRowContentGravity(
    gravity: GravityBuilder
) = onView { it.contentGravity = gravity(Gravity) }