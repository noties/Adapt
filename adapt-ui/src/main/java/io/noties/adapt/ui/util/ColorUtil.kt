package io.noties.adapt.ui.util

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import java.util.Locale
import kotlin.math.roundToInt

// all colors on android implicitly defined with alpha channel,
//  so `#000000` => `#FF000000` (with 255 alpha)
fun Int.toHexString(): String = String.format(Locale.ROOT, "#%08X", this)

fun Int.withAlphaComponent(@FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
    val alphaValue = (alpha.coerceIn(0F, 1F) * 255).roundToInt()
    // 1. clear alpha from incoming color
    // 2. apply supplied alpha
    return (this and 0x00ffffff) or (alphaValue shl 24)
}

@ColorInt
fun hex(color: String): Int {
    val hex = color
        .takeIf { it.startsWith('#') }
        ?.takeIf { it.length > 3 }
        ?.drop(1)
        ?: error("Invalid hex color format:'$color', must be at least 4 characters including leading '#'")
    return try {
        when (hex.length) {
            3 -> hex.flatMap { listOf(it, it) }.joinToString("").let { parse(it, true) }
            4 -> hex.flatMap { listOf(it, it) }.joinToString("").let { parse(it, false) }
            6 -> parse(hex, true)
            8 -> parse(hex, false)
            else -> error("Invalid hex color format:'$color'")
        }
    } catch (t: NumberFormatException) {
        error("Invalid hex color format:'$color'")
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