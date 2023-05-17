package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import io.noties.adapt.ui.util.toHexString
import kotlin.math.min

/**
 * If `edge` is null - gradient comes from the center, else it comes from the angle specified
 */
// TODO: accept angle
class RadialGradient internal constructor(
    @ColorInt private val colors: IntArray,
    private val positions: FloatArray?,
    private val edge: GradientEdge?,
    mode: Shader.TileMode?
) : Gradient() {

    private val mode: Shader.TileMode = mode ?: Shader.TileMode.CLAMP

    companion object {
        @CheckResult
        operator fun invoke(
            @ColorInt startColor: Int,
            @ColorInt endColor: Int,
            edge: GradientEdge? = null,
            mode: Shader.TileMode? = null
        ): RadialGradient {
            return RadialGradient(
                intArrayOf(startColor, endColor),
                null,
                edge,
                mode
            )
        }

        @CheckResult
        operator fun invoke(
            @ColorInt colors: IntArray,
            edge: GradientEdge? = null,
            mode: Shader.TileMode? = null
        ): RadialGradient {
            return RadialGradient(
                colors,
                null,
                edge,
                mode
            )
        }

        @CheckResult
        operator fun invoke(
            colorsAndPositions: List<Pair<Int, Float>>,
            edge: GradientEdge? = null,
            mode: Shader.TileMode? = null
        ): RadialGradient {
            val colors = colorsAndPositions.map { it.first }.toIntArray()
            val positions = colorsAndPositions.map { it.second }.toFloatArray()
            return RadialGradient(
                colors,
                positions,
                edge,
                mode
            )
        }
    }

    override fun createShader(bounds: Rect): Shader {

        val point = if (edge == null) {
            PointF(bounds.centerX().toFloat(), bounds.centerY().toFloat())
        } else {
            positionOfEdge(edge, bounds)
        }

        val radius = (min(bounds.width(), bounds.height())).toFloat()

        // radial can take _power_ argument to indicate the radius
        //  so, 0.5F is equal, 0.25F initial color takes 1/4th with second one taking the rest 0.75
        return android.graphics.RadialGradient(
            point.x,
            point.y,
            radius,
            colors,
            positions,
            mode
        )
    }

    override fun toString(): String {
        return "RadialGradient(colors=${colors.map { it.toHexString() }}, positions=${positions?.contentToString()}, edge=$edge, mode=$mode)"
    }
}