package io.noties.adapt.ui.app.text

import androidx.annotation.DimenRes
import io.noties.adapt.ui.app.ContextHolder
import kotlin.math.roundToInt

interface TextSizes : ContextHolder {
    companion object : TextSizes

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
}

typealias TextSizesBuilder = TextSizes.() -> Int