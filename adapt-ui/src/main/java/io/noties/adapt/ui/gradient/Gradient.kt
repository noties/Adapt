package io.noties.adapt.ui.gradient

import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

sealed class Gradient {
    abstract fun createShader(bounds: Rect): Shader

    protected fun positionOfAngle(angle: Float, bounds: Rect): Pair<Float, Float> {
        val radius = radius(bounds)
        val radians = Math.toRadians(angle.toDouble())
        val x = bounds.centerX() + (radius * cos(radians))
        val y = bounds.centerY() + (radius * sin(radians))
        return x.toFloat() to y.toFloat()
    }

    protected fun radius(bounds: Rect): Float {
        @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
        return (sqrt(
            Math.pow(
                bounds.width().toDouble() + bounds.height().toDouble(),
                2.0
            )
        ) / 2.0).toFloat()
    }
}

class LinearGradient(
    @FloatRange(from = 0.0, to = 360.0) val angle: Float,
    @ColorInt val startColor: Int,
    @ColorInt val endColor: Int
) : Gradient() {
    override fun createShader(bounds: Rect): Shader {
        val (startX, startY) = positionOfAngle(angle, bounds)
        val (endX, endY) = positionOfAngle((angle + 180F) % 360F, bounds)

        return android.graphics.LinearGradient(
            startX, startY,
            endX, endY,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }
}

// If `angle` is null - gradient comes from the center,
//  else it comes from the angle specified
class RadialGradient(
    @ColorInt val startColor: Int,
    @ColorInt val endColor: Int,
    @FloatRange(from = 0.0, to = 360.0) val angle: Float? = null,
    @FloatRange(from = 0.0, to = 1.0) val startColorRatio: Float = 0.5F
) : Gradient() {
    override fun createShader(bounds: Rect): Shader {

        val (startX, startY) = if (angle == null) {
            bounds.centerX().toFloat() to bounds.centerY().toFloat()
        } else {
            positionOfAngle(angle, bounds)
        }

        // we create diameter, calculate amount of start color (by default half)
        val startColorRadius = ((radius(bounds) * 2F) * startColorRatio)

        // radial can take _power_ argument to indicate the radius
        //  so, 0.5F is equal, 0.25F initial color takes 1/4th with second one taking the rest 0.75
        return RadialGradient(
            startX,
            startY,
            startColorRadius,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }
}