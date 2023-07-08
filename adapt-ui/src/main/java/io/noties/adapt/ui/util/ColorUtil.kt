package io.noties.adapt.ui.util

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