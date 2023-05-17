package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import io.noties.adapt.ui.util.toHexString

// TODO: accept angle
class SweepGradient internal constructor(
    @ColorInt private val colors: IntArray,
    private val positions: FloatArray?,
    private val edge: GradientEdge? = null,
) : Gradient() {

    companion object {
        @CheckResult
        operator fun invoke(
            @ColorInt startColor: Int,
            @ColorInt endColor: Int,
            edge: GradientEdge? = null
        ): SweepGradient {
            return SweepGradient(
                intArrayOf(startColor, endColor),
                null,
                edge
            )
        }

        @CheckResult
        operator fun invoke(
            @ColorInt colors: IntArray,
            edge: GradientEdge? = null
        ): SweepGradient {
            return SweepGradient(
                colors,
                null,
                edge
            )
        }

        @CheckResult
        operator fun invoke(
            colorsAndPositions: List<Pair<Int, Float>>,
            edge: GradientEdge? = null
        ): SweepGradient {
            val colors = colorsAndPositions.map { it.first }.toIntArray()
            val positions = colorsAndPositions.map { it.second }.toFloatArray()
            return SweepGradient(
                colors,
                positions,
                edge
            )
        }
    }

    override fun createShader(bounds: Rect): Shader {
        val point = if (edge == null) {
            PointF(bounds.centerX().toFloat(), bounds.centerY().toFloat())
        } else {
            positionOfEdge(edge, bounds)
        }
        return android.graphics.SweepGradient(
            point.x,
            point.y,
            colors,
            positions
        )
    }

    override fun toString(): String {
        return "SweepGradient(colors=${colors.map { it.toHexString() }}, positions=${positions?.contentToString()}, edge=$edge)"
    }
}