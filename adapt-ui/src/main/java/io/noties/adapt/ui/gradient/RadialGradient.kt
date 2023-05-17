package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import kotlin.math.min

/**
 * If `edge` is null - gradient comes from the center, else it comes from the angle specified
 */
class RadialGradient internal constructor(
    internal val type: Type,
    @ColorInt internal val colors: IntArray,
    internal val positions: FloatArray?
) : Gradient() {

    internal var tileMode: Shader.TileMode? = null

    companion object {

        @CheckResult
        fun center() = Builder(Edge(null))

        @CheckResult
        fun edge(edge: GradientEdge) = Builder(Edge(edge))

        @CheckResult
        fun angle(angle: Float) = Builder(Angle(angle))

        internal fun radius(bounds: Rect) = min(bounds.width(), bounds.height())
    }

    @CheckResult
    fun setTileMode(tileMode: Shader.TileMode?) = this.also {
        it.tileMode = tileMode
    }

    override fun createShader(bounds: Rect): Shader {

        val point = when (type) {
            is Edge -> if (type.edge == null) {
                PointF(bounds.centerX().toFloat(), bounds.centerY().toFloat())
            } else {
                positionOfEdge(type.edge, bounds)
            }
            is Angle -> {
                val (start, _) = positionsOfAngle(type.angle, bounds)
                start
            }
        }

        val radius = radius(bounds).toFloat()

        // radial can take _power_ argument to indicate the radius
        //  so, 0.5F is equal, 0.25F initial color takes 1/4th with second one taking the rest 0.75
        return android.graphics.RadialGradient(
            point.x,
            point.y,
            radius,
            colors,
            positions,
            tileMode ?: Shader.TileMode.CLAMP
        )
    }

    override fun toString(): String {
        val properties = listOf(
            "type" to type,
            "colors" to colorsAndPositionsToString(colors, positions),
            "tileMode" to tileMode
        ).joinToString(", ") {
            "${it.first}=${it.second}"
        }
        return "RadialGradient($properties)"
    }

    sealed class Type
    data class Edge(val edge: GradientEdge?) : Type()
    data class Angle(val angle: Float) : Type()

    class Builder(private val type: Type) {

        @CheckResult
        fun setColors(
            @ColorInt startColor: Int,
            @ColorInt endColor: Int
        ): RadialGradient = RadialGradient(
            type,
            intArrayOf(startColor, endColor),
            null
        )

        @CheckResult
        fun setColors(@ColorInt vararg colors: Int): RadialGradient {
            return RadialGradient(
                type,
                createColors(*colors),
                null
            )
        }

        @CheckResult
        fun setColors(
            vararg colorsAndPositions: Pair</*@ColorInt*/Int, Float>
        ): RadialGradient {
            val (colors, positions) = createColorsAndPositions(*colorsAndPositions)
            return RadialGradient(type, colors, positions)
        }
    }
}