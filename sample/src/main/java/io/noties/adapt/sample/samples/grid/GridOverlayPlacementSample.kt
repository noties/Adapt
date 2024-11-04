package io.noties.adapt.sample.samples.grid

import android.content.Context
import android.util.AttributeSet
import android.view.View
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.purple
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridOverlayFactory
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Square
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.element.grid.gridBackground
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.widget.grid.GridOverlayLayout
import kotlin.random.Random

@AdaptSample(
    id = "20241104022011",
    title = "Grid overlay layout placements",
    description = "Various samples of different span rules to layout overlay elements",
    tags = ["grid", "adapt-ui", "widget"]
)
class GridOverlayPlacementSample : AdaptUISampleView() {
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
                        Shape(x = fill(), y = fill()) {
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

                        // left: second, right: previous to last
                        // top: second, bottom: previous to last
                        // if less than 3 spans in any of the dimensions -> not shown
                        Shape(
                            x = { columns, _ -> 1 until (columns - 1) },
                            y = { _, rows -> 1 until (rows - 1) }
                        ) {
                            Rectangle { fill { black } }
                        }

                        // always in center when grid counted columns and rows divided by 2
                        //  with remainder (so takes at most 1 square cell), and gone when count
                        //  divides without remainder.
                        Shape(
                            x = { columns, _ -> if (columns % 2 == 0) skip else just(columns / 2) },
                            y = { _, rows -> if (rows % 2 == 0) skip else just(rows / 2) }
                        ) {
                            Circle {
                                fill { hex("#f00") }
                                size(24, 24)
                                gravity { center }
                            }
                        }
                    }
                    .onView {
                        val overlay = it.getOrCreateOverlay(1)
                        overlay.setOnClickListener {
                            overlay.set(
                                GridOverlayLayout.Key(
                                    x = GridOverlayLayout.Axis { column, _ ->
                                        GridOverlayFactory.just(
                                            Random.nextInt(column)
                                        )
                                    },
                                    y = GridOverlayLayout.Axis { _, row ->
                                        GridOverlayFactory.just(
                                            Random.nextInt(row)
                                        )
                                    }
                                ),
                                View(it.context).also {
                                    it.background = ShapeDrawable.invoke {
                                        Rectangle { fill { orange } }
                                    }
                                }
                            )
                        }
                    }
                    .preview(true) {
                        it.previewBounds()
                    }


                GridControls(grid)
                    .layoutMargin(vertical = 24)
            }
        }.layoutFill()
    }
}

@Preview
private class PreviewGridOverlayPlacementSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = GridOverlayPlacementSample()
}
