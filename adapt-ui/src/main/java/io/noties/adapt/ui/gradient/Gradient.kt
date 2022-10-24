package io.noties.adapt.ui.gradient

import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.SweepGradient
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import io.noties.adapt.ui.util.toHexString
import io.noties.adapt.ui.util.toStringPropertiesDefault
import kotlin.math.max

enum class GradientEdge {
    Leading,
    Top,
    Trailing,
    Bottom,
    LeadingTop,
    LeadingBottom,
    TopTrailing,
    BottomTrailing
}

abstract class Gradient {
    abstract fun createShader(bounds: Rect): Shader

    internal companion object {
        fun positionOfEdge(edge: GradientEdge, bounds: Rect): Pair<Float, Float> {
            val pair: Pair<Int, Int> = when (edge) {
                GradientEdge.Leading -> bounds.left to bounds.centerY()
                GradientEdge.Top -> bounds.centerX() to bounds.top
                GradientEdge.Trailing -> bounds.right to bounds.centerY()
                GradientEdge.Bottom -> bounds.centerX() to bounds.bottom
                GradientEdge.LeadingTop -> bounds.left to bounds.top
                GradientEdge.LeadingBottom -> bounds.left to bounds.bottom
                GradientEdge.TopTrailing -> bounds.right to bounds.top
                GradientEdge.BottomTrailing -> bounds.right to bounds.bottom
            }
            return pair.first.toFloat() to pair.second.toFloat()
        }
    }
}

class LinearGradient(
    private val edges: Pair<GradientEdge, GradientEdge>,
    @ColorInt private val startColor: Int,
    @ColorInt private val endColor: Int
) : Gradient() {
    override fun createShader(bounds: Rect): Shader {

        val (startX, startY) = positionOfEdge(edges.first, bounds)
        val (endX, endY) = positionOfEdge(edges.second, bounds)

        return LinearGradient(
            startX,
            startY,
            endX,
            endY,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }

    override fun toString(): String = toStringPropertiesDefault(this) {
        it(::edges)
        it(::startColor) { it.toHexString() }
        it(::endColor) { it.toHexString() }
    }
}

// If `edge` is null - gradient comes from the center,
//  else it comes from the angle specified
class RadialGradient(
    @ColorInt private val startColor: Int,
    @ColorInt private val endColor: Int,
    private val edge: GradientEdge? = null,
    @FloatRange(from = 0.0, to = 1.0) private val startColorRatio: Float = 0.5F
) : Gradient() {
    override fun createShader(bounds: Rect): Shader {

        val (startX, startY) = if (edge == null) {
            bounds.centerX().toFloat() to bounds.centerY().toFloat()
        } else {
            positionOfEdge(edge, bounds)
        }

        // we create diameter, calculate amount of start color (by default half)
        val radius = (max(bounds.width(), bounds.height()) * startColorRatio)

        // radial can take _power_ argument to indicate the radius
        //  so, 0.5F is equal, 0.25F initial color takes 1/4th with second one taking the rest 0.75
        return RadialGradient(
            startX,
            startY,
            radius,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }

    override fun toString(): String = toStringPropertiesDefault(this) {
        it(::startColor) { it.toHexString() }
        it(::endColor) { it.toHexString() }
        it(::edge)
        it(::startColorRatio)
    }
}

class SweepGradient(
    @ColorInt private val startColor: Int,
    @ColorInt private val endColor: Int,
    private val edge: GradientEdge? = null,
) : Gradient() {
    override fun createShader(bounds: Rect): Shader {
        val (x, y) = if (edge == null) {
            bounds.centerX().toFloat() to bounds.centerY().toFloat()
        } else {
            positionOfEdge(edge, bounds)
        }
        return SweepGradient(
            x,
            y,
            startColor,
            endColor
        )
    }

    override fun toString(): String = toStringPropertiesDefault(this) {
        it(::startColor) { it.toHexString() }
        it(::endColor) { it.toHexString() }
        it(::edge)
    }
}