package io.noties.adapt.sample.explore

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

object ExploreHexParser {

    object Colors {

//        private const val MAX = 255

        @ColorInt
        fun hex(color: String): Int = ExploreHexParser.hex(color)

        fun rgb(red: Int, green: Int, blue: Int): Int = argb(255, red, green, blue)
        fun rgb(red: Float, green: Float, blue: Float): Int = argb(1F, red, green, blue)

        fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int {
            return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
        }

        fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
            return argb(
                (alpha * 255F).roundToInt(),
                (red * 255F).roundToInt(),
                (green * 255F).roundToInt(),
                (blue * 255F).roundToInt()
            )
        }
    }

    // colorFromHex
    // fromHex
    // colorHex
    @ColorInt
    fun hex(color: String): Int {
        val hex = color
            .takeIf { it.startsWith('#') }
            ?.takeIf { it.length > 3 }
            ?.drop(1)
            ?: error("Invalid hex color format:'$color', must be at least 4 characters starting with '#'")
        return when (hex.length) {
            3 -> hex.flatMap { listOf(it, it) }.joinToString("").let { parse(it, true) }
            4 -> hex.flatMap { listOf(it, it) }.joinToString("").let { parse(it, false) }
            6 -> parse(hex, true)
            8 -> parse(hex, false)
            else -> error("Invalid hex color format:'$color'")
        }
    }

    private fun parse(hex: String, fullAlpha: Boolean): Int {
        val value = java.lang.Long.parseLong(hex, 16)
        return if (!fullAlpha) {
            value.toInt()
        } else {
            (value or 0x00000000ff000000).toInt()
        }
    }
}