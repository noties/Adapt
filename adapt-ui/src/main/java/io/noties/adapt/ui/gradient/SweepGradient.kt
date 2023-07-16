package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt

class SweepGradient internal constructor(
    internal val type: Type,
    @ColorInt internal val colors: IntArray,
    internal val positions: FloatArray?
) : Gradient() {

    companion object {
        
        @CheckResult
        fun center() = Builder(Edge(null))

        @CheckResult
        fun edge(edge: GradientEdge) = Builder(Edge(edge))

        @CheckResult
        fun angle(angle: Float) = Builder(Angle(angle))
    }

    override fun createShader(bounds: Rect): Shader {

        val point = when (type) {
            is Edge -> if (type.edge == null) {
                PointF(bounds.centerX().toFloat(), bounds.centerY().toFloat())
            } else {
                positionOfEdge(type.edge, bounds)
            }
            is Angle -> {
                val (point, _) = positionsOfAngle(type.angle, bounds)
                point
            }
        }

        return android.graphics.SweepGradient(
            point.x,
            point.y,
            colors,
            positions
        )
    }

    override fun toString(): String {
        val properties = listOf(
            "type" to type,
            "colors" to colorsAndPositionsToString(colors, positions)
        ).joinToString(", ") {
            "${it.first}=${it.second}"
        }
        return "SweepGradient($properties)"
    }

    sealed class Type
    data class Edge(val edge: GradientEdge?) : Type()
    data class Angle(val angle: Float) : Type()

    class Builder(private val type: Type) {
        @CheckResult
        fun setColors(
            @ColorInt startColor: Int,
            @ColorInt endColor: Int
        ): SweepGradient = SweepGradient(
            type,
            intArrayOf(startColor, endColor),
            null
        )

        @CheckResult
        fun setColors(@ColorInt vararg colors: Int): SweepGradient {
            return SweepGradient(
                type,
                createColors(*colors),
                null
            )
        }

        @CheckResult
        fun setColors(
            vararg colorsAndPositions: Pair</*@ColorInt*/Int, Float>
        ): SweepGradient {
            val (colors, positions) = createColorsAndPositions(*colorsAndPositions)
            return SweepGradient(type, colors, positions)
        }
    }
}