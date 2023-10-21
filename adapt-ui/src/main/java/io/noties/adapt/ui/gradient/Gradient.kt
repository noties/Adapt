package io.noties.adapt.ui.gradient

import android.content.res.Resources
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import io.noties.adapt.ui.util.toHexString

abstract class Gradient {
    abstract fun createShader(
        bounds: Rect,
        density: Float = Resources.getSystem().displayMetrics.density
    ): Shader

    companion object {
        @CheckResult
        fun positionOfEdge(edge: GradientEdge, bounds: Rect): PointF {
            val pair: Pair<Int, Int> = when (edge.type) {
                GradientEdge.GradientEdgeType.Leading -> bounds.left to bounds.centerY()
                GradientEdge.GradientEdgeType.TopLeading -> bounds.left to bounds.top
                GradientEdge.GradientEdgeType.Top -> bounds.centerX() to bounds.top
                GradientEdge.GradientEdgeType.TopTrailing -> bounds.right to bounds.top
                GradientEdge.GradientEdgeType.Trailing -> bounds.right to bounds.centerY()
                GradientEdge.GradientEdgeType.BottomLeading -> bounds.left to bounds.bottom
                GradientEdge.GradientEdgeType.Bottom -> bounds.centerX() to bounds.bottom
                GradientEdge.GradientEdgeType.BottomTrailing -> bounds.right to bounds.bottom
            }
            return PointF(pair.first.toFloat(), pair.second.toFloat())
        }

        @CheckResult
        fun positionsOfAngle(angle: Float, bounds: Rect): Pair<PointF, PointF> {
            return PositionsOfAngle.positionsOfAngle(angle, bounds)
        }

        @CheckResult
        internal fun colorsAndPositionsToString(
            @ColorInt colors: IntArray,
            positions: FloatArray?
        ): String {
            val list = positions?.let { p ->
                p.withIndex()
                    .map { (i, position) ->
                        "\"${colors[i].toHexString()}\":$position"
                    }
            } ?: colors.map { "\"${it.toHexString()}\"" }
            return list.joinToString(", ")
        }

        @CheckResult
        internal fun createColors(@ColorInt vararg colors: Int): IntArray {
            require(colors.size >= 2) {
                "Minimum 2 colors are required, supplied:${colors.size} - ${colors.map { c -> "\"${c.toHexString()}\"" }}"
            }
            return colors.copyOf()
        }

        @CheckResult
        internal fun createColorsAndPositions(
            vararg colorsAndPositions: Pair</*@ColorInt*/ Int, Float>
        ): Pair<IntArray, FloatArray> {
            require(colorsAndPositions.size >= 2) {
                val string = colorsAndPositions.joinToString(", ") { cap ->
                    "\"${cap.first.toHexString()}\":${cap.second}"
                }
                "Minimum 2 colors are required, supplied:${colorsAndPositions.size} - [$string]"
            }
            val colors = colorsAndPositions.map { cap -> cap.first }.toIntArray()
            val positions = colorsAndPositions.map { cap -> cap.second }.toFloatArray()
            return colors to positions
        }
    }
}