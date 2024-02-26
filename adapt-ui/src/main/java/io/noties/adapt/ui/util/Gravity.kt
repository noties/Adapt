package io.noties.adapt.ui.util

import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.CENTER_VERTICAL
import android.view.Gravity.END
import android.view.Gravity.HORIZONTAL_GRAVITY_MASK
import android.view.Gravity.LEFT
import android.view.Gravity.RIGHT
import android.view.Gravity.START
import android.view.Gravity.TOP
import android.view.Gravity.VERTICAL_GRAVITY_MASK
import androidx.annotation.GravityInt

/**
 * Utility to specify [android.view.Gravity] in a type-safe manner
 * without manual xor operation. `Gravity.CENTER OR Gravity.TOP` becomes
 * `Gravity.center.top`.
 * Direct child of Gravity allows further customization of opposite axis,
 * so `Gravity.top` (y) has `.center`, `.leading` and `.trailing` (x).
 * And `Gravity.leading` (x) has `.center`, `.top` and `.bottom` (y).
 */
open class Gravity(@GravityInt val value: Int) {

    companion object {

        val leading = HorizontalGravity(START)
        val top = VerticalGravity(TOP)
        val trailing = HorizontalGravity(END)
        val bottom = VerticalGravity(BOTTOM)

        val center = CenterGravity()

        class VerticalGravity(@GravityInt value: Int) : Gravity(value) {
            val leading = Gravity(value or START)
            val center = Gravity(value or CENTER_HORIZONTAL)
            val trailing = Gravity(value or END)
        }

        class HorizontalGravity(@GravityInt value: Int) : Gravity(value) {
            val top = Gravity(value or TOP)
            val center = Gravity(value or CENTER_VERTICAL)
            val bottom = Gravity(value or BOTTOM)
        }

        class CenterGravity : Gravity(CENTER) {
            val horizontal = Gravity(CENTER_HORIZONTAL)
            val vertical = Gravity(CENTER_VERTICAL)

            val top = Gravity(TOP or CENTER_HORIZONTAL)
            val bottom = Gravity(BOTTOM or CENTER_HORIZONTAL)

            val leading = Gravity(START or CENTER_VERTICAL)
            val trailing = Gravity(END or CENTER_VERTICAL)
        }
    }

    override fun toString(): String {
        return "Gravity($value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Gravity

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }

    fun hasLeading() = checkHorizontal(LEFT)
    fun hasTop() = checkVertical(TOP)
    fun hasTrailing() = checkHorizontal(RIGHT)
    fun hasBottom() = checkVertical(BOTTOM)
    fun hasCenter() = checkHorizontal(CENTER_HORIZONTAL) || checkVertical(CENTER_VERTICAL)

    private fun checkHorizontal(expected: Int): Boolean {
        return expected == (value and HORIZONTAL_GRAVITY_MASK)
    }

    private fun checkVertical(expected: Int): Boolean {
        return expected == (value and VERTICAL_GRAVITY_MASK)
    }
}

typealias GravityBuilder = Gravity.Companion.() -> Gravity