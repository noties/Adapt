package io.noties.adapt.sample.samples.grid

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.naplesYellow
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.ui.color.purple
import io.noties.adapt.sample.ui.color.purpureus
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.util.random
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Square
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.element.grid.gridBackground
import io.noties.adapt.ui.element.grid.gridSpan
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.elevation
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.withAlphaComponent
import kotlin.random.Random

@AdaptSample(
    id = "20241104022011",
    title = "Grid overlay layout placements",
    description = "Various samples of different span rules to layout overlay elements",
    tags = [Tags.grid, Tags.adaptUi, Tags.widget, Tags.interactive]
)
class GridOverlayPlacementSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {

                val grid = Grid {

                    val columns = 5
                    val rows = 5

                    GridRow { Squares(columns) }

                    repeat(rows - 1) { GridRow { Square() } }

                }.indent()
                    // NB! multiple calls to `gridBackground` would contribute to the same overlay instance
                    //  if they need to be different another overlay could be created with different priority
                    .gridBackground {

                        // matches whole grid (vertically and horizontally)
                        View()
                            .background {
                                Label {
                                    text("x:fill y:fill")
                                    textSize(17)
                                    textGravity { bottom.trailing }
                                    fill(LinearGradient.edges { bottom.trailing to top.leading }
                                        .setColors(
                                            Colors.white,
                                            Colors.purple.withAlphaComponent(0.2F)
                                        ))
                                }
                            }
                            .gridSpan(x = fill(), y = fill())

                        // left: second, right: previous to last
                        // top: second, bottom: previous to last
                        // if less than 3 spans in any of the dimensions -> not shown
                        View()
                            .background {
                                Rectangle { fill { black } }
                            }
                            .gridSpan(
                                x = { 1 until (it.columns - 1) },
                                y = { 1 until (it.rows - 1) }
                            )

                        // always in center when grid counted columns and rows divided by 2
                        //  with remainder (so takes at most 1 square cell), and gone when count
                        //  divides without remainder.
                        View()
                            .background {
                                Circle {
                                    fill { salmonRed }
                                    size(24, 24)
                                    gravity { center }
                                }
                            }
                            .gridSpan(
                                x = { (columns, _) -> if (columns % 2 == 0) skip else just(columns / 2) },
                                y = { (_, rows) -> if (rows % 2 == 0) skip else just(rows / 2) }
                            )
                    }
                    .preview(true) {
                        it.previewBounds()
                    }


                GridControls(grid)
                    .layoutMargin(vertical = 24)

                Text("Add random square on overlay(1)")
                    .layout(fill, wrap)
                    .layoutMargin(16)
                    .padding(horizontal = 16, vertical = 12)
                    .textSize { 17 }
                    .textColor { white }
                    .textGravity { center }
                    .background {
                        RoundedRectangle(6) { fill { purpureus } }
                    }
                    .foregroundDefaultSelectable()
                    .elevation(6)
                    .onClick {
                        val overlay = grid.view.getOrCreateOverlay(1)
                        overlay.addViews {
                            View()
                                .background {
                                    Rectangle { fill { random(steelBlue, naplesYellow) } }
                                }
                                .gridSpan(
                                    x = { (column, _) -> just(Random.nextInt(column)) },
                                    y = { (_, row) -> just(Random.nextInt(row)) }
                                )

                        }
                    }
            }
        }.layoutFill()
    }
}

@Preview
private class PreviewGridOverlayPlacementSample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = GridOverlayPlacementSample()
}
