package io.noties.adapt.ui.app.text

import android.content.Context
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import io.noties.adapt.ui.app.App
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object TextSizes {
    /**
     * Obtain scaled-pixel dimension resource
     */
    fun res(@DimenRes resId: Int): Int = context
        .resources
        .let {
            val scaledDensity = it.displayMetrics.scaledDensity
            val value = it.getDimension(resId)
            (value / scaledDensity).roundToInt()
        }

    val context: Context get() = App.topMostActivity ?: App.shared
}

typealias TextSizesBuilder = TextSizes.() -> Int


fun resSp(@DimenRes resId: Int) = TextSizesProperty(TextSizes.res(resId))

fun rawSp(@Dimension sp: Int) = TextSizesProperty(sp)


class TextSizesProperty(private val value: Int) : ReadOnlyProperty<TextSizes, Int> {
    override fun getValue(thisRef: TextSizes, property: KProperty<*>): Int {
        return value
    }
}