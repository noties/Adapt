package io.noties.adapt.ui.gradient

import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.SweepGradient
import androidx.annotation.ColorInt
import io.noties.adapt.ui.util.toHexString
import kotlin.math.min

enum class GradientEdge {
    Leading,
    Top,
    Trailing,
    Bottom,
    TopLeading,
    BottomLeading,
    TopTrailing,
    BottomTrailing
}

abstract class Gradient {
    abstract fun createShader(bounds: Rect): Shader

    internal companion object {
        fun positionOfEdge(edge: GradientEdge, bounds: Rect): Pair<Float, Float> {
            val pair: Pair<Int, Int> = when (edge) {
                GradientEdge.Leading -> bounds.left to bounds.centerY()
                GradientEdge.TopLeading -> bounds.left to bounds.top
                GradientEdge.Top -> bounds.centerX() to bounds.top
                GradientEdge.TopTrailing -> bounds.right to bounds.top
                GradientEdge.Trailing -> bounds.right to bounds.centerY()
                GradientEdge.BottomLeading -> bounds.left to bounds.bottom
                GradientEdge.Bottom -> bounds.centerX() to bounds.bottom
                GradientEdge.BottomTrailing -> bounds.right to bounds.bottom
            }
            return pair.first.toFloat() to pair.second.toFloat()
        }
    }
}

class LinearGradient internal constructor(
    private val edges: Pair<GradientEdge, GradientEdge>,
    private val colors: IntArray,
    private val positions: FloatArray?,
    mode: Shader.TileMode?
) : Gradient() {

    private val mode: Shader.TileMode = mode ?: Shader.TileMode.CLAMP

    companion object {
        operator fun invoke(
            edges: Pair<GradientEdge, GradientEdge>,
            @ColorInt startColor: Int,
            @ColorInt endColor: Int,
            mode: Shader.TileMode? = null
        ): io.noties.adapt.ui.gradient.LinearGradient {
            return LinearGradient(
                edges,
                intArrayOf(startColor, endColor),
                null,
                mode
            )
        }

        operator fun invoke(
            edges: Pair<GradientEdge, GradientEdge>,
            @ColorInt colors: IntArray,
            mode: Shader.TileMode? = null
        ): io.noties.adapt.ui.gradient.LinearGradient {
            return LinearGradient(
                edges,
                colors,
                null,
                mode
            )
        }

        operator fun invoke(
            edges: Pair<GradientEdge, GradientEdge>,
            colorsAndPositions: List<Pair<Int, Float>>,
            mode: Shader.TileMode? = null
        ): io.noties.adapt.ui.gradient.LinearGradient {
            // extract colors to int array
            // extract positions to float array
            val colors = colorsAndPositions.map { it.first }.toIntArray()
            val positions = colorsAndPositions.map { it.second }.toFloatArray()
            return LinearGradient(
                edges,
                colors,
                positions,
                mode
            )
        }
    }

    override fun createShader(bounds: Rect): Shader {

        val (startX, startY) = positionOfEdge(edges.first, bounds)
        val (endX, endY) = positionOfEdge(edges.second, bounds)

        return LinearGradient(
            startX,
            startY,
            endX,
            endY,
            colors,
            positions,
            mode
        )
    }

    override fun toString(): String {
        return "LinearGradient(edges=$edges, colors=${colors.map { it.toHexString() }}, positions=${positions?.contentToString()}, mode=$mode)"
    }
}

// If `edge` is null - gradient comes from the center,
//  else it comes from the angle specified
class RadialGradient internal constructor(
    @ColorInt private val colors: IntArray,
    private val positions: FloatArray?,
    private val edge: GradientEdge?,
    mode: Shader.TileMode?
) : Gradient() {

    private val mode: Shader.TileMode = mode ?: Shader.TileMode.CLAMP

    companion object {
        operator fun invoke(
            @ColorInt startColor: Int,
            @ColorInt endColor: Int,
            edge: GradientEdge? = null,
            mode: Shader.TileMode? = null
        ): io.noties.adapt.ui.gradient.RadialGradient {
            return RadialGradient(
                intArrayOf(startColor, endColor),
                null,
                edge,
                mode
            )
        }

        operator fun invoke(
            @ColorInt colors: IntArray,
            edge: GradientEdge? = null,
            mode: Shader.TileMode? = null
        ): io.noties.adapt.ui.gradient.RadialGradient {
            return RadialGradient(
                colors,
                null,
                edge,
                mode
            )
        }

        operator fun invoke(
            colorsAndPositions: List<Pair<Int, Float>>,
            edge: GradientEdge? = null,
            mode: Shader.TileMode? = null
        ): io.noties.adapt.ui.gradient.RadialGradient {
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

        val (startX, startY) = if (edge == null) {
            bounds.centerX().toFloat() to bounds.centerY().toFloat()
        } else {
            positionOfEdge(edge, bounds)
        }

        val radius = (min(bounds.width(), bounds.height())).toFloat()

        // radial can take _power_ argument to indicate the radius
        //  so, 0.5F is equal, 0.25F initial color takes 1/4th with second one taking the rest 0.75
        return RadialGradient(
            startX,
            startY,
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

class SweepGradient internal constructor(
    @ColorInt private val colors: IntArray,
    private val positions: FloatArray?,
    private val edge: GradientEdge? = null,
) : Gradient() {

    companion object {
        operator fun invoke(
            @ColorInt startColor: Int,
            @ColorInt endColor: Int,
            edge: GradientEdge? = null
        ): io.noties.adapt.ui.gradient.SweepGradient {
            return SweepGradient(
                intArrayOf(startColor, endColor),
                null,
                edge
            )
        }

        operator fun invoke(
            @ColorInt colors: IntArray,
            edge: GradientEdge? = null
        ): io.noties.adapt.ui.gradient.SweepGradient {
            return SweepGradient(
                colors,
                null,
                edge
            )
        }

        operator fun invoke(
            colorsAndPositions: List<Pair<Int, Float>>,
            edge: GradientEdge? = null
        ): io.noties.adapt.ui.gradient.SweepGradient {
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
        val (x, y) = if (edge == null) {
            bounds.centerX().toFloat() to bounds.centerY().toFloat()
        } else {
            positionOfEdge(edge, bounds)
        }
        return SweepGradient(
            x,
            y,
            colors,
            positions
        )
    }

    override fun toString(): String {
        return "SweepGradient(colors=${colors.map { it.toHexString() }}, positions=${positions?.contentToString()}, edge=$edge)"
    }
}