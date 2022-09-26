package io.noties.adapt.ui.shape

import io.noties.adapt.ui.util.dip
import kotlin.math.roundToInt

sealed class Dimension {
    abstract fun resolve(dimension: Int): Int

    class Exact(val value: Int) : Dimension() {
        override fun resolve(dimension: Int): Int = value.dip
    }

    class Relative(val percent: Float) : Dimension() {
        override fun resolve(dimension: Int): Int = (dimension * percent).roundToInt()
    }
}