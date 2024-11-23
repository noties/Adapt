package io.noties.adapt.sample.samples.grid

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.element.config.ConfigPicker
import io.noties.adapt.sample.ui.element.config.ConfigToggle
import io.noties.adapt.sample.ui.string.configGridBordersTitle
import io.noties.adapt.sample.ui.string.configGridLayoutTitle
import io.noties.adapt.sample.ui.text.subHeadline
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.string.Strings
import io.noties.adapt.ui.app.string.StringsBuilder
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.VStackReverseDrawingOrder
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridBorders
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Spacer
import io.noties.adapt.ui.element.grid.Square
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.element.grid.gridBorders
import io.noties.adapt.ui.element.grid.gridColumns
import io.noties.adapt.ui.element.grid.gridSpacing
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textTypeface
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.sticky.stickyVerticalScrollContainer
import io.noties.adapt.ui.sticky.stickyView
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.widget.grid.GridLayout

@AdaptSample(
    id = "20241110154706",
    title = "Grid borders",
    tags = [Tags.grid, Tags.adaptUi, Tags.widget, Tags.interactive]
)
class GridBordersSample : SampleViewUI() {

    lateinit var gridLayout: GridLayout
    lateinit var gridBorders: GridBorders

    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Grid {

                View().layout(fill, 48)

                GridRow {
                    Squares(5)
                }

                GridRow {
                    Square()
                    Spacer()
                    Square()
                }

                GridRow {
                    Square()
                        .gridColumns(2)
                    Spacer()
                }

                GridRow {
                    View()
                        .layout(fill, 48)
                        .gridColumns(3)
                    Spacer()
                        .gridColumns(2)
                }

            }.indent()
                .reference(::gridLayout)
                .gridSpacing(4)
                .padding(vertical = 4, horizontal = 16)
                // using default placement - background (this one we will configure)
                .gridBorders {
                    this@GridBordersSample.gridBorders = gridBorders

                    tint {
                        gradient {
                            linear {
                                edges { top.leading to bottom.trailing }
                                    .setColors(Color.MAGENTA, Color.BLUE)
                            }
                        }
                    }
                    style { fill }
                    drawOuterBorder()
                }
//                .gridBorders {
//                    // default values, install as grid-layout foreground
//                    install { foreground }
//                }

            VScroll {

                VStackReverseDrawingOrder {

                    StickySection { configGridBordersTitle }
                    GridBorderConfiguration()

                    StickySection { configGridLayoutTitle }
                        .layoutMargin(top = 16)
                    GridLayoutConfiguration()

                }.indent()
                    .padding(top = 16, bottom = 32)
                    .noClip()

            }.indent()
                .layoutFill()
                .stickyVerticalScrollContainer()
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.StickySection(title: StringsBuilder) =
        Text(title(Strings))
            .textSize { subHeadline }
            .textTypeface { bold }
            .textColor { text }
            .textGravity { center }
            .padding(horizontal = 16, vertical = 6)
            .backgroundColor { hex("#eeeeee").withAlphaComponent(0.82F) }
            .stickyView()
            .onView {
                it.setOnClickListener { }
                it.isSoundEffectsEnabled = false
            }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.GridBorderConfiguration() {
        VStack {
            ConfigPicker(
                title = "style",
                values = listOf(
                    gridBorders.style,
                    GridBorders.Style.create { stroke(2) },
                    GridBorders.Style.create { stroke() },
                ),
                selected = gridBorders.style,
                toString = {
                    when (it) {
                        GridBorders.Style.Fill -> "fill"
                        is GridBorders.Style.Stroke -> "stroke(${it.width})"
                    }
                },
                onSelectedChanged = {
                    gridBorders.style = it
                    gridLayout.invalidate()
                }
            )

            ConfigPicker(
                title = "tint",
                values = listOf(
                    gridBorders.tint,
                    GridBorders.Tint.create { color { black } },
                ),
                selected = gridBorders.tint,
                toString = {
                    when (it) {
                        is GridBorders.Tint.Color -> "color { #${Integer.toHexString(it.color)} }"
                        is GridBorders.Tint.Paint -> "gradient"
                    }
                },
                onSelectedChanged = {
                    gridBorders.tint = it
                    gridLayout.invalidate()
                }
            )

            ConfigToggle(
                title = "drawOuterBorder",
                isChecked = gridBorders.drawOuterBorder,
                onCheckedChanged = {
                    gridBorders.drawOuterBorder = it
                    gridLayout.invalidate()
                }
            )

            ConfigToggle(
                title = "includeOuterBorderPadding",
                isChecked = gridBorders.includeOuterBorderPadding,
                onCheckedChanged = {
                    gridBorders.includeOuterBorderPadding = it
                    gridLayout.invalidate()
                }
            )
        }
    }

    @Suppress("FunctionName")
    private fun <LP : LayoutParams> ViewFactory<LP>.GridLayoutConfiguration() {
        VStack {

            ConfigPicker(
                title = "padding",
                values = listOf(
                    Rect(),
                    Rect(1, 1, 1, 1),
                    Rect(2, 2, 2, 2),
                    Rect(4, 4, 4, 4),
                    Rect(8, 8, 8, 8),
                    Rect(16, 16, 16, 16),
                    Rect(0, 4, 0, 4),
                    Rect(4, 0, 4, 0),
                    Rect(0, 8, 16, 24),
                ),
                selected = null,
                toString = {
                    if (it.left == it.right && it.top == it.bottom) {
                        listOfNotNull(
                            "horizontal:${it.left}".takeIf { _ -> it.left != 0 },
                            "vertical:${it.top}".takeIf { _ -> it.top != 0 }
                        ).joinToString(" ")
                            .takeIf { it.isNotEmpty() }
                            ?: "all:0"
                    } else {
                        "leading:${it.left} top:${it.top} trailing:${it.right} bottom:${it.bottom}"
                    }
                },
                onSelectedChanged = {
                    gridLayout.setPadding(
                        it.left.dip,
                        it.top.dip,
                        it.right.dip,
                        it.bottom.dip
                    )
                }
            )

            ConfigPicker(
                title = "spacing",
                values = listOf(
                    0 to 0,
                    1 to 1,
                    2 to 2,
                    4 to 4,
                    8 to 16
                ),
                selected = null,
                toString = {
                    listOfNotNull(
                        "horizontal:${it.first}".takeIf { _ -> it.first != 0 },
                        "vertical:${it.second}".takeIf { _ -> it.second != 0 }
                    ).joinToString(" ")
                        .takeIf { it.isNotEmpty() }
                        ?: "all:0"
                },
                onSelectedChanged = {
                    gridLayout.horizontalSpacingPx = it.first.dip
                    gridLayout.verticalSpacingPx = it.second.dip
                }
            )
        }
    }
}

@Preview
private class PreviewGridBordersSample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = GridBordersSample()
}