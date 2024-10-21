package io.noties.adapt.ui.app.color

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import io.noties.adapt.ui.app.ContextHolder
import kotlin.math.roundToInt

// define it as interface and implement it in companion
//  so all extensions on val Color.main .. are available to both
//  also gives ability to include in other _builders_ to provide defined in the project colors
interface Colors : ContextHolder {
    companion object : Colors

    @ColorInt
    fun res(@ColorRes resId: Int): Int = context.getColor(resId)

    @ColorInt
    fun hex(hex: String): Int = io.noties.adapt.ui.util.hex(hex)

    @ColorInt
    fun rgb(
        @IntRange(from = 0, to = 255) red: Int,
        @IntRange(from = 0, to = 255) green: Int,
        @IntRange(from = 0, to = 255) blue: Int
    ): Int = argb(1F, red, green, blue)

    @ColorInt
    fun argb(
        @FloatRange(from = 0.0, to = 1.0) alpha: Float,
        @IntRange(from = 0, to = 255) red: Int,
        @IntRange(from = 0, to = 255) green: Int,
        @IntRange(from = 0, to = 255) blue: Int
    ): Int = Color.argb(
        (255F * alpha).roundToInt(),
        red,
        green,
        blue
    )

    operator fun get(@ColorRes resId: Int) = res(resId)
}


typealias ColorsBuilder = Colors.() -> /*@ColorInt*/ Int