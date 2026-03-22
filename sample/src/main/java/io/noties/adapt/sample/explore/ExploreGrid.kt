package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.ui.element.config.ConfigPicker
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.grid.Grid
import io.noties.adapt.ui.element.grid.GridRow
import io.noties.adapt.ui.element.grid.Spacer
import io.noties.adapt.ui.element.grid.Square
import io.noties.adapt.ui.element.grid.Squares
import io.noties.adapt.ui.element.grid.gridColumns
import io.noties.adapt.ui.element.grid.gridSpacing
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.util.AbsDrawable
import io.noties.adapt.ui.util.children
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.widget.grid.GridLayout
import io.noties.adapt.ui.widget.grid.GridRowLayout
import kotlin.math.roundToInt

@Deprecated("Implemented")
object ExploreGrid {
    // grid-draw
    // draw-grid

    // style: stroke, fill
    //  in the end can expose paint to create customizations
    // configuration: follow actual spans or draw no matter what
    // outer border

    // TODO: create GridCanvas, that would be able to draw with proper cells defined
    //  and also layout would be extensible to allow such modification (ignore def calculations)

    class GridBorders(
        val gridLayout: GridLayout,
        val onChanged: () -> Unit
    ) {

        sealed class Style {
            // strokes content (each cell bordered)
            data class Stroke(@Dimension(unit = Dimension.DP) val width: Int) : Style()

            // fills spacing
            data object Fill : Style()
        }

        sealed class Tint {
            // just raw color
            data class Color(
                @ColorInt val color: Int
            ) : Tint() {
                val paint = Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).also {
                    it.color = color
                }
            }

            // control paint with which it is drawn
            data class Paint(
                private val provider: (Rect) -> android.graphics.Paint
            ) : Tint() {

                private val rect = Rect()
                private var paint: android.graphics.Paint? = null

                fun paint(bounds: Rect): android.graphics.Paint {
                    val p = paint
                    return if (p == null || bounds != rect) {
                        provider(bounds).also { paint = it }
                    } else {
                        p
                    }
                }
            }
        }

        var style: Style = Style.Stroke(1)
            set(value) {
                field = value
                onChanged()
            }

        var tint: Tint = Tint.Color(Color.BLACK)
            set(value) {
                field = value
                onChanged()
            }

        var drawOuterBorder = false
            set(value) {
                field = value
                onChanged()
            }

        var includePadding = false
            set(value) {
                field = value
                onChanged()
            }

        private val bounds = Rect()
        private val rect = Rect()

        fun draw(canvas: Canvas) {
            val gridLayout = gridLayout
                .takeIf { it.childCount > 0 }
                ?: return

            // obtain gridlayout bounds
            bounds.set(
                0,
                0,
                gridLayout.width,
                gridLayout.height
            )

            val paint = when (val tint = tint) {
                is Tint.Color -> tint.paint
                is Tint.Paint -> tint.paint(bounds)
            }

            when (val style = style) {

                Style.Fill -> {
                    drawFill(canvas, paint)
                }

                is Style.Stroke -> {
                    drawStroke(canvas, paint, style)
                }
            }
        }

        private fun drawFill(canvas: Canvas, paint: Paint) {
            val gridLayout = gridLayout

            fun draw(rect: Rect) {
                canvas.drawRect(rect, paint)
            }

            val spacingVertical = gridLayout.verticalSpacingPx
            val spacingHorizontal = gridLayout.horizontalSpacingPx

            val columnWidth = gridLayout.columnWidth

            var left = gridLayout.paddingLeft
            var top = gridLayout.paddingTop

            var isFirst = true

            for (child in gridLayout.children) {
                val height = child.height

                if (isFirst) {
                    isFirst = false
                } else {
                    // draw vertical spacing
                    rect.set(
                        gridLayout.paddingLeft,
                        top - spacingVertical,
                        gridLayout.paddingLeft + (gridLayout.width - gridLayout.paddingLeft - gridLayout.paddingRight),
                        top
                    )
                    draw(rect)
                }

                if (child is GridRowLayout) {
                    var isFirstRow = true
                    for (rowChild in child.children) {
                        if (isFirstRow) {
                            isFirstRow = false
                        } else {
                            rect.set(
                                left - spacingHorizontal,
                                top,
                                left,
                                top + height
                            )
                            draw(rect)
                        }
                        val spans = rowChild.gridRowLayoutParams.spanColumns
                        val width = (spans * columnWidth) + (spacingHorizontal * (spans - 1))
                        left += width
                        left += spacingHorizontal
                    }
                } else {
                    // no op, whole width, no spacing
                }

                left = gridLayout.paddingLeft
                top += height
                top += spacingVertical
            }

            if (drawOuterBorder) {
                // cannot draw it fully, each border line is drawn individually
//                rect.set(
//                    gridLayout.paddingLeft,
//                    gridLayout.paddingTop,
//                    gridLayout.paddingLeft + (gridLayout.width - gridLayout.paddingLeft - gridLayout.paddingRight),
//                    gridLayout.paddingTop + (gridLayout.height - gridLayout.paddingTop - gridLayout.paddingBottom)
//                )
//                rect.inset(-spacingHorizontal, -spacingVertical)
//                draw(rect)

                val w = gridLayout.width
                val h = gridLayout.height

                val pL = gridLayout.paddingLeft
                val pT = gridLayout.paddingTop
                val pR = gridLayout.paddingRight
                val pB = gridLayout.paddingBottom

                if (includePadding) {

                    // top
                    rect.set(
                        0,
                        0,
                        w,
                        pT
                    )
                    draw(rect)

                    // right
                    rect.set(
                        w - pR,
                        0,
                        w,
                        h
                    )
                    draw(rect)

                    // bottom
                    rect.set(
                        0,
                        h - pB,
                        w,
                        h
                    )
                    draw(rect)

                    // left
                    rect.set(
                        0,
                        0,
                        pL,
                        h
                    )
                    draw(rect)

                } else {
                    val iH = (spacingHorizontal / -2F).roundToInt()
                    val iV = (spacingVertical / -2F).roundToInt()

                    val endH = pL + (w - pL - pR)
                    val endV = pT + (h - pT - pB)

                    // top
                    rect.set(
                        pL,
                        pT,
                        endH,
                        pT
                    )
                    rect.inset(iH, iV)
                    draw(rect)

                    // right
                    rect.set(
                        endH,
                        pT,
                        endH,
                        endV
                    )
                    rect.inset(iH, iV)
                    draw(rect)

                    // bottom
                    rect.set(
                        pL,
                        endV,
                        endH,
                        endV
                    )
                    rect.inset(iH, iV)
                    draw(rect)

                    // left
                    rect.set(
                        pL,
                        pT,
                        pL,
                        endV
                    )
                    rect.inset(iH, iV)
                    draw(rect)
                }
            }
        }

        private fun drawStroke(canvas: Canvas, paint: Paint, stroke: Style.Stroke) {
            val gridLayout = gridLayout

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = stroke.width.dip.toFloat()

            fun draw(rect: Rect) {
                canvas.drawRect(rect, paint)
            }

            val spacingVertical = gridLayout.verticalSpacingPx
            val spacingHorizontal = gridLayout.horizontalSpacingPx

            val columnWidth = gridLayout.columnWidth

            var left = gridLayout.paddingLeft
            var top = gridLayout.paddingTop

            for (child in gridLayout.children) {
                val height = child.height

                if (child is GridRowLayout) {

                    for (rowChild in child.children) {
                        val spans = rowChild.gridRowLayoutParams.spanColumns
                        val width = (columnWidth * spans) + (spacingHorizontal * (spans - 1))
                        rect.set(left, top, left + width, top + height)
                        draw(rect)

                        left += width
                        left += spacingHorizontal
                    }

                } else {
                    // else takes the whole width
                    rect.set(
                        left,
                        top,
                        left + (gridLayout.width - gridLayout.paddingLeft - gridLayout.paddingRight),
                        top + height
                    )
                    draw(rect)
                }

                left = gridLayout.paddingLeft
                top += height
                top += spacingVertical
            }

            if (drawOuterBorder) {
                if (includePadding) {
                    rect.set(
                        0,
                        0,
                        gridLayout.width,
                        gridLayout.height
                    )
                    rect.inset(
                        (stroke.width / 2F).roundToInt(),
                        (stroke.width / 2F).roundToInt()
                    )
                } else {
                    rect.set(
                        gridLayout.paddingLeft,
                        gridLayout.paddingTop,
                        gridLayout.paddingLeft + (gridLayout.width - gridLayout.paddingLeft - gridLayout.paddingRight),
                        gridLayout.paddingTop + (gridLayout.height - gridLayout.paddingTop - gridLayout.paddingBottom)
                    )
                    rect.inset(-spacingHorizontal, -spacingVertical)
                    rect.inset(
                        (stroke.width / 2F).roundToInt(),
                        (stroke.width / 2F).roundToInt()
                    )
                }
                draw(rect)
            }
        }

        private val View.gridRowLayoutParams: GridRowLayout.LayoutParams get() = layoutParams as GridRowLayout.LayoutParams
    }
}

@Preview
private class PreviewGrid(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
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
                .gridSpacing(4)
                .padding(vertical = 4, horizontal = 16)
                .onView {
                    val borders = ExploreGrid.GridBorders(it) {
                        it.invalidate()
                    }
                    borders.drawOuterBorder = true
                    borders.tint = ExploreGrid.GridBorders.Tint.Paint { bounds ->
                        Paint(Paint.ANTI_ALIAS_FLAG).also {
                            it.shader = LinearGradient.edges { top.leading to bottom.trailing }
                                .setColors(Color.MAGENTA, Color.BLUE)
                                .createShader(bounds)
                        }
                    }
                    borders.style = ExploreGrid.GridBorders.Style.Fill
                    borders.includePadding = true
                    val drawable = object : AbsDrawable() {
                        override fun draw(canvas: Canvas) {
                            borders.draw(canvas)
                        }
                    }
                    it.background = drawable
                }

            ConfigPicker(
                title = "Style",
                values = listOf(
                    ExploreGrid.GridBorders.Style.Fill,
                    ExploreGrid.GridBorders.Style.Stroke(1)
                ),
                onSelectedChanged = {

                }
            )

            ConfigPicker(
                title = "Padding",
                values = listOf(),
                onSelectedChanged = {}
            )

        }.indent()
    }
}