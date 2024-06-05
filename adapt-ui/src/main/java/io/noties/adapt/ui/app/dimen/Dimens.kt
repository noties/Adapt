package io.noties.adapt.ui.app.dimen

import androidx.annotation.DimenRes
import io.noties.adapt.ui.app.ContextHolder
import kotlin.math.roundToInt

object Dimens : ContextHolder {
    fun res(@DimenRes resId: Int): Int = context.resources
        .let {
            val density = it.displayMetrics.density
            val value = it.getDimension(resId)
            (value / density).roundToInt()
        }
}

typealias DimensBuilder = Dimens.() -> /*@Dimension*/ Int