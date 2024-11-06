package io.noties.adapt.sample.samples.grid

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.R
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStackWrapHeightOrScroll
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Square
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.element.grid.gridBackground
import io.noties.adapt.ui.element.grid.gridForeground
import io.noties.adapt.ui.element.grid.gridSpan
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.shape.Circle

@AdaptSample(
    id = "20241102225551",
    title = "GridOverlay",
    description = "Usage of Grid overlays (background, foreground, etc) with TransitionManager to build dynamic and animatable grids",
    tags = ["grid", "adapt-ui", "widget"]
)
class GridOverlaySample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        ZStackWrapHeightOrScroll {

            VStack {

                val grid = Grid {
                    // here we only define actual grid structure, content is going to be added in overlays
                    GridRow { Squares(5) }

                    repeat(4) {
                        GridRow { Square() }
                    }
                }.indent()
                    // background, drawn under primary content
                    .gridBackground {

                        // first row, first column
                        Text("\uD83D\uDE0E")
                            .textSize { 21 }
                            .textColor { black }
                            .textGravity { center }
                            // first row, first column
                            .gridSpan(
                                x = first(),
                                // or with builder
                                y = { first() }
                            )

                        // span multiple, take whole row
                        View()
                            .backgroundColor { orange }
                            .gridSpan(x = fill(), y = just(1))

                        // span multiple, take whole column
                        View()
                            .backgroundColor { primary }
                            .gridSpan(x = just(1), y = fill())

                        // dynamically positioned cell, always last row/column
                        View()
                            .background { Circle { fill { accent } } }
                            .gridSpan(
                                x = { (c, _) -> last(c) },
                                y = { (_, r) -> last(r) }
                            )
                    }
                    .gridForeground {
                        // dynamic foreground cell, always center
                        Image(R.drawable.ic_launcher_foreground)
                            .imageScaleType { fitCenter }
                            .padding(8)
                            .imageTint { black }
                            .gridSpan(
                                // provide own calculation logic
                                x = { (columns, _) -> center(columns) },
                                y = { (_, rows) -> center(rows) }
                            )

                        // dynamic foreground cell, second to last row, second column
                        View()
                            .background {
                                Circle {
                                    fill { hex("#f00") }
                                    size(24, 24)
                                    gravity { center }
                                }
                            }
                            .gridSpan(
                                x = first() + 1,
                                y = { (_, row) -> last(row) - 1 }
                            )
                    }
                    .preview(true) {
                        it.previewBounds()
                    }

                GridControls(grid)
                    .layoutMargin(vertical = 24)
            }

        }.indent()
            .layoutFill()
    }

    private fun center(value: Int): IntRange {
        // make it fir center, if division has reminder, take 2 cells
        //  in order to center view properly
        val i = value / 2
        return if (value != (i * 2)) {
            // if we have reminder, then take it
            i until (i + 1)
        } else {
            //
            // otherwise take multiple center cells
            (i - 1)..i
        }
    }
}

@Preview
private class PreviewGridOverlaySample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = GridOverlaySample()
}
