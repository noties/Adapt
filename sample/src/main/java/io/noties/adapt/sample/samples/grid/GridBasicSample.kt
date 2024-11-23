package io.noties.adapt.sample.samples.grid

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.naplesYellow
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.ui.color.purpureus
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.GridSpacer
import io.noties.adapt.ui.element.grid.Spacer
import io.noties.adapt.ui.element.grid.Square
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.element.grid.gridSpacing
import io.noties.adapt.ui.element.grid.layoutGravity
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textStyle
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.withAlphaComponent

@AdaptSample(
    id = "20241103154220",
    title = "Grid sample",
    tags = [Tags.grid, Tags.adaptUi, Tags.widget]
)
class GridBasicSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        Grid {

            // normal grid cells should be added to GridRow
            GridRow {
                // predefined square view
                Square()

                // views can be wrap_content (placed in cell dimensions)
                Text("CELL")
                    .layoutWrap()
                    .layoutGravity { bottom.trailing }
                    .textStyle { body }

                // multiple squares with customization
                Squares(2) { it.backgroundColor { steelBlue } }
            }

            // if view is added to Grid directly it takes all available width
            //  and do not contribute to column count
            View()
                .layout(fill, 12)
                .backgroundColor { salmonRed }

            GridRow {
                Square().backgroundColor { naplesYellow }

                // special cell that _fills_ all available column spans
                //  (short version of GridSpacer element)
                Spacer()

                Square().backgroundColor { emeraldGreen.withAlphaComponent(0.5F) }
            }

            GridRow {

                // GridSpacer actually takes view-builder, so normal views can be added
                GridSpacer {
                    Text("¡SPACER!")
                        .layoutWrap()
                        .textSize { 21 }
                        .textColor { white }
                        .background { RoundedRectangle(8) { fill { purpureus } } }
                        .layoutGravity { center }
                        .padding(8)
                }

                Square()

            }.indent()
                .layout(fill, 128)

        }.indent()
            .gridSpacing(vertical = 4, horizontal = 12)
            .preview(true) {
                it.previewBounds()
            }
    }
}

@Preview
private class PreviewGridSample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = GridBasicSample()
}