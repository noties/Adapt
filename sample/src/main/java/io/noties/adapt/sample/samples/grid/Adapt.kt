package io.noties.adapt.sample.samples.grid

import android.transition.TransitionManager
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Square
import io.noties.adapt.ui.element.textStyle
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.widget.grid.GridLayout
import io.noties.adapt.ui.widget.grid.GridRowLayout
import kotlin.properties.ReadOnlyProperty

// `Adapt.kt` just a _random_ name to be on top of folder to indicate its _base_ status, can be anything

/**
 * Expects supplied gridLayout that has the first row as [GridRowLayout] (columns are added/removed from it),
 * rows are added by adding a grid-row with 1 single element - square.
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.GridControls(
    grid: ViewElement<GridLayout, *>
) = Grid {

    // convenience getter
    val gridLayout: GridLayout by ReadOnlyProperty { _, _ -> grid.view }

    fun beginTransition() = TransitionManager.beginDelayedTransition(gridLayout.parent as ViewGroup)

    GridRow {

        PlusMinus(
            label = "Row / Y",
            onPlus = {
                beginTransition()
                val row = ViewFactory.newView(context)
                    .layoutParams(GridLayout.LayoutParams())
                    .create {
                        GridRow { Square() }
                    }
                gridLayout.addView(row)
            },
            onMinus = {
                // keep at least one (do not use overlays for this grid)
                //  otherwise we might remove an overlay
                if (gridLayout.rows.isNotEmpty()) {
                    beginTransition()
                    gridLayout.removeView(grid.view.rows.last())
                }
            }
        )

        PlusMinus(
            label = "Column / X",
            onPlus = {
                val row = gridLayout.getChildAt(0) as GridRowLayout
                ViewFactory.newView(row)
                    .layoutParams(GridRowLayout.LayoutParams())
                    .create {
                        Square()
                    }
                    .also {
                        beginTransition()
                        row.addView(it)
                    }
            },
            onMinus = {
                val row = gridLayout.getChildAt(0) as GridRowLayout
                // keep as least one
                if (row.childCount > 0) {
                    beginTransition()
                    row.removeViewAt(row.childCount - 1)
                }
            }
        )

    }.indent()

}.indent()

@Suppress("FunctionName")
private fun <LP : LayoutParams> ViewFactory<LP>.PlusMinus(
    label: String,
    onPlus: () -> Unit,
    onMinus: () -> Unit
) = HStack {
    Image(R.drawable.ic_remove_24)
        .background { RoundedRectangle(6).fill { black } }
        .foregroundDefaultSelectable()
        .clipToOutline()
        .onClick(onMinus)
    Text(label)
        .padding(horizontal = 12)
        .textStyle { body }
    Image(R.drawable.ic_add_24)
        .background { RoundedRectangle(6).fill { black } }
        .foregroundDefaultSelectable()
        .clipToOutline()
        .onClick(onPlus)
}