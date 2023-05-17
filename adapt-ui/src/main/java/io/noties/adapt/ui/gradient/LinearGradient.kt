package io.noties.adapt.ui.gradient

import android.graphics.Rect
import android.graphics.Shader
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import io.noties.adapt.ui.util.toHexString

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

    override fun createShader(bounds: Rect): Shader {

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
        val colors = positions?.let { p ->
            p.withIndex()
                .map { (i, position) ->
                    "\"${colors[i].toHexString()}\":$position"
                }
        } ?: colors.map { "\"${it.toHexString()}\"" }
        val properties = listOf(
            "type" to type,
            "colors" to colors,
            "tileMode" to tileMode
        ).joinToString(", ") {
            "${it.first}=${it.second}"
        }
        return "LinearGradient($properties)"
    }

    sealed class Type

    data class Edges(val edges: Pair<GradientEdge, GradientEdge>) : Type() {
        override fun toString(): String {
            return "Type.Edges(edges=$edges)"
        }
    }

    data class Angle(val angle: Float) : Type() {
        override fun toString(): String {
            return "Type.Angle(angle=$angle)"
        }
    }

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
            require(colors.size >= 2) {
                "Minimum 2 colors are required, supplied:${colors.size} - ${colors.map { c -> "\"${c.toHexString()}\"" }}"
            }
            return LinearGradient(type, colors.copyOf(), null)
        }

        /**
         * setColors(Color.RED to 0F, Color.GREEN to 0.25F, Color.BLUE to 1F)
         */
        @CheckResult
        fun setColors(
            vararg colorsAndPositions: Pair</*@ColorInt*/ Int, Float>
        ): LinearGradient {
            require(colorsAndPositions.size >= 2) {
                val string = colorsAndPositions.joinToString(", ") { cap ->
                    "\"${cap.first.toHexString()}\":${cap.second}"
                }
                "Minimum 2 colors are required, supplied:${colorsAndPositions.size} - [$string]"
            }
            val colors = colorsAndPositions.map { cap -> cap.first }.toIntArray()
            val positions = colorsAndPositions.map { cap -> cap.second }.toFloatArray()
            return LinearGradient(
                type,
                colors,
                positions
            )
        }
    }
}