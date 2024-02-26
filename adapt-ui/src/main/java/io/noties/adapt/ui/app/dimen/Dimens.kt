package io.noties.adapt.ui.app.dimen

import android.content.Context
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import io.noties.adapt.ui.app.App
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object Dimens {
    fun res(@DimenRes resId: Int): Int = context.resources
        .let {
            val density = it.displayMetrics.density
            val value = it.getDimension(resId)
            (value / density).roundToInt()
        }

    val context: Context get() = App.topMostActivity ?: App.shared
}

typealias DimensBuilder = Dimens.() -> Int


fun resDip(@DimenRes resId: Int) = DimensProperty(Dimens.res(resId))

fun rawDip(@Dimension dip: Int) = DimensProperty(dip)


class DimensProperty(private val value: Int) : ReadOnlyProperty<Dimens, Int> {
    override fun getValue(thisRef: Dimens, property: KProperty<*>): Int {
        return value
    }
}