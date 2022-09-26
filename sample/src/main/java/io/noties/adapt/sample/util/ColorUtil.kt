package io.noties.adapt.sample.util

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

fun Int.withAlphaComponent(alpha: Float): Int {
    return ColorUtils.setAlphaComponent(this, (alpha * 255).roundToInt())
}

fun hex(color: String): Int = Color.parseColor(color)