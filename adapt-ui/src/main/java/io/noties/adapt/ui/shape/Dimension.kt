package io.noties.adapt.ui.shape

import io.noties.adapt.ui.util.dip
import kotlin.math.roundToInt

sealed class Dimension {
    abstract fun resolve(dimension: Int): Int

    data class Exact(val value: Int) : Dimension() {
        override fun resolve(dimension: Int): Int = value.dip
        override fun toString(): String {
            return "Dimension.Exact(value=$value)"
        }
    }

    data class Relative(val percent: Float) : Dimension() {
        override fun resolve(dimension: Int): Int = (dimension * percent).roundToInt()
        override fun toString(): String {
            return "Dimension.Relative(percent=$percent)"
        }
    }
}