package io.noties.adapt.ui.element.grid

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.app.color.ColorsBuilder
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.gradient.GradientBuilder
import io.noties.adapt.ui.util.AbsDrawable
import io.noties.adapt.ui.util.children
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.widget.grid.GridLayout
import io.noties.adapt.ui.widget.grid.GridRowLayout
import kotlin.math.roundToInt

class GridBorders private constructor() {

    /**
     * Controls style of borders, can be `fill` or `stroke`
     * @see Style
     */
    var style: Style = Style.Stroke(1)

    /**
     * Controls int of borders, can be `color`, `gradient` or `paint`
     * @see Tint
     */
    var tint: Tint = Tint.Color(Color.BLACK)

    /**
     * Controls if grid outer border should be drawn. Outer here means border of the
     * grid-layout itself.
     * __Note__ that setting this value might result in drawing outside view
     * bounds if grid-spacing exceeds grid-layout's padding values. The outer border
     * is drawn vertical/horizontal spacing outset from grid-content.
     */
    var drawOuterBorder = false

    /**
     * Controls if outer border should include grid-layout padding.
     * Has effect only if [drawOuterBorder] is `true`
     */
    var includeOuterBorderPadding = false


    interface Factory {
        companion object {

            fun create(): Factory = object : Factory {
                override val gridBorders: GridBorders = default()
            }
        }

        val gridBorders: GridBorders

        fun style(style: Style.Factory.() -> Style) = this
            .also { gridBorders.style = style(Style) }

        fun tint(tint: Tint.Factory.() -> Tint) = this
            .also { gridBorders.tint = tint(Tint) }

        fun drawOuterBorder(drawOuterBorder: Boolean = true) = this
            .also { gridBorders.drawOuterBorder = drawOuterBorder }

        fun includeOuterBorderPadding(includePadding: Boolean = true) = this
            .also { gridBorders.includeOuterBorderPadding = includePadding }
    }

    companion object {
        fun default() = GridBorders()

        fun create(borders: Factory.() -> Unit): GridBorders {
            val factory = Factory.create()
            borders(factory)
            return factory.gridBorders
        }
    }

    sealed class Style {
        interface Factory {
            val fill: Fill get() = Fill

            fun stroke(@Dimension(unit = Dimension.DP) width: Int = 1): Stroke {
                return Stroke(width)
            }
        }

        companion object : Factory {
            fun create(style: Factory.() -> Style): Style {
                return style(this)
            }

            // impossible
//             interface H: Factory.() -> Style

            // useless
//            fun interface H: (Factory) -> Style

            // not a type, actual impl
//            val Builder: Factory.() -> Style = {  }
        }

        // strokes content (each cell bordered)
        data class Stroke(@Dimension(unit = Dimension.DP) val width: Int) : Style()

        // fills spacing
        data object Fill : Style()
    }

    sealed class Tint {
        interface Factory {

            fun color(color: ColorsBuilder) = Color(color(Colors))

            fun gradient(gradient: GradientBuilder): Paint {
                val g = gradient(Gradient)
                val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
                return Paint { bounds ->
                    paint.also {
                        it.shader = g.createShader(bounds)
                    }
                }
            }

            fun paint(provider: (Rect) -> android.graphics.Paint) = Paint(provider)
        }

        companion object : Factory {
            fun create(tint: Factory.() -> Tint): Tint {
                return tint(this)
            }
        }

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
}

typealias GridBordersPlacementSetter = (gridLayout: GridLayout, gridBordersDrawable: android.graphics.drawable.Drawable) -> Unit

open class GridBordersPlacement(
    val setter: GridBordersPlacementSetter
) {
    interface Factory {
        val background get() = Background
        val foreground get() = Foreground

        fun drawable(
            setter: GridBordersPlacementSetter
        ) = CustomDrawable(setter)
    }

    companion object : Factory

    data object Background : GridBordersPlacement({ v, d -> v.background = d })
    data object Foreground : GridBordersPlacement({ v, d -> v.foreground = d })
    class CustomDrawable(
        setter: GridBordersPlacementSetter
    ) : GridBordersPlacement({ v, d -> setter(v, d) })
}

interface GridLayoutGridBordersFactory : GridBorders.Factory {
    companion object {
        fun create(gridLayout: GridLayout): GridLayoutGridBordersFactory =
            object : GridLayoutGridBordersFactory {
                override val gridLayout = gridLayout
                override val gridBorders = GridBorders.default()
                override var gridBordersPlacement: GridBordersPlacement? = null
            }
    }

    val gridLayout: GridLayout
    var gridBordersPlacement: GridBordersPlacement?

    // by default uses gridLayout itself as target view,
    //  but can be any view (for example, overlay: background, foreground, etc)
    fun install(
        placement: GridBordersPlacement.Factory.() -> GridBordersPlacement = { background }
    ) {
        this.gridBordersPlacement = placement(GridBordersPlacement)
    }
}

@Suppress("FINAL_UPPER_BOUND")
fun <V : GridLayout, LP : LayoutParams> ViewElement<V, LP>.gridBorders(
    borders: GridLayoutGridBordersFactory.(GridLayout) -> Unit = { }
) = this.onView { layout ->

    val factory = GridLayoutGridBordersFactory.create(layout)
    borders(factory, layout)

    val drawable = GridBordersDrawable(
        gridLayout = layout,
        gridBorders = factory.gridBorders
    )

    val placement = (factory.gridBordersPlacement ?: GridBordersPlacement.Background)

    placement
        .setter.invoke(layout, drawable)
}

private class GridBordersDrawable(
    val gridLayout: GridLayout,
    val gridBorders: GridBorders
) : AbsDrawable() {
    // should we additionally listen to the changes in the layout? no, caller must establish proper connection
    // yes, also should check for being attached, and if not -> stop any listeners

    private val style by gridBorders::style
    private val tint by gridBorders::tint
    private val drawOuterBorder by gridBorders::drawOuterBorder
    private val includeOuterBorderPadding by gridBorders::includeOuterBorderPadding

    private val rect = Rect()

    override fun draw(canvas: Canvas) {

        val paint = when (val tint = tint) {
            is GridBorders.Tint.Color -> tint.paint
            is GridBorders.Tint.Paint -> tint.paint(bounds)
        }

        when (val style = style) {

            GridBorders.Style.Fill -> {
                drawFill(canvas, paint)
            }

            is GridBorders.Style.Stroke -> {
                drawStroke(canvas, paint, style)
            }
        }
    }

    private fun drawFill(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.FILL

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

            if (includeOuterBorderPadding) {

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


    private fun drawStroke(canvas: Canvas, paint: Paint, stroke: GridBorders.Style.Stroke) {
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
            if (includeOuterBorderPadding) {
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