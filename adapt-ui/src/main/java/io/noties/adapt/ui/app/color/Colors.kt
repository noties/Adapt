package io.noties.adapt.ui.app.color

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import io.noties.adapt.ui.app.App
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object Colors {
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

    // hm, if we create a-new, activity might be not initialized and thus
    //  we might use context from previous activity, or just application
    val context: Context get() = (App.topMostActivity ?: App.shared)
}


typealias ColorsBuilder = Colors.() -> /*@ColorInt*/ Int


fun resColor(
    @ColorRes resId: Int
) = ColorsProperty(Colors.res(resId))

fun hex(
    hex: String
) = ColorsProperty(Colors.hex(hex))

fun rgb(
    red: Int,
    green: Int,
    blue: Int
) = argb(1F, red, green, blue)

fun argb(
    alpha: Float,
    red: Int,
    green: Int,
    blue: Int
) = ColorsProperty(Colors.argb(alpha, red, green, blue))

fun rawColor(
    @ColorInt color: Int
) = ColorsProperty(color)


class ColorsProperty(@ColorInt val value: Int) : ReadOnlyProperty<Colors, /*@ColorInt*/ Int> {
    override fun getValue(thisRef: Colors, property: KProperty<*>): Int {
        return value
    }
}