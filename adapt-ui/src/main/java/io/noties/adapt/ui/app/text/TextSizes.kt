package io.noties.adapt.ui.app.text

import androidx.annotation.DimenRes
import io.noties.adapt.ui.app.ContextHolder
import kotlin.math.roundToInt

interface TextSizes : ContextHolder {
    companion object : TextSizes

    /**
     * Obtain scaled-pixel dimension resource *and* scale it up
     * to points which adapt-ui uses, so 4 pixels on xxhdpi device would equal 1 point (4.px / 4.scaledDensity = 1)
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