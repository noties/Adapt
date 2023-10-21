package io.noties.adapt.ui.gradient

import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt

class LinearGradient internal constructor(
    internal val type: Type,
    internal val colors: IntArray,
    internal val positions: FloatArray?
) : Gradient() {

    internal var tileMode: Shader.TileMode? = null

    companion object {
        /**
         * `LinearGradient.edges { top.leading to trailing }`
         */
        @CheckResult
        fun edges(
            block: GradientEdge.Companion.() -> Pair<GradientEdge, GradientEdge>
        ) = Builder(Edges(block(GradientEdge.Companion)))

        /**
         * `LinearGradient.angle(90F)`
         */
        @CheckResult
        fun angle(
            angle: Float
        ) = Builder(Angle(angle))
    }

    @CheckResult
    fun setTileMode(tileMode: Shader.TileMode?) = this.also {
        it.tileMode = tileMode
    }

    override fun createShader(bounds: Rect, density: Float): Shader {

        val colors = this.colors
        val positions = this.positions

        val (start, end) = when (val type = this.type) {
            is Edges -> {
                val start = positionOfEdge(type.edges.first, bounds)
                val end = positionOfEdge(type.edges.second, bounds)
                start to end
            }
            is Angle -> positionsOfAngle(type.angle, bounds)
        }

        return android.graphics.LinearGradient(
            start.x,
            start.y,
            end.x,
            end.y,
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
        return "LinearGradient($properties)"
    }

    sealed class Type
    data class Edges(val edges: Pair<GradientEdge, GradientEdge>) : Type()
    data class Angle(val angle: Float) : Type()

    class Builder(private val type: Type) {
        @CheckResult
        fun setColors(
            @ColorInt startColor: Int,
            @ColorInt endColor: Int
        ) = LinearGradient(type, intArrayOf(startColor, endColor), null)

        @CheckResult
        fun setColors(
            @ColorInt vararg colors: Int
        ): LinearGradient {
            return LinearGradient(
                type,
                createColors(*colors),
                null
            )
        }

        /**
         * setColors(Color.RED to 0F, Color.GREEN to 0.25F, Color.BLUE to 1F)
         */
        @CheckResult
        fun setColors(
            vararg colorsAndPositions: Pair</*@ColorInt*/ Int, Float>
        ): LinearGradient {
            val (colors, positions) = createColorsAndPositions(*colorsAndPositions)
            return LinearGradient(
                type,
                colors,
                positions
            )
        }
    }
}