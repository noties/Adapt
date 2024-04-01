package io.noties.adapt.ui.util

import android.annotation.SuppressLint
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
open class Gravity(@GravityInt val rawValue: Int) {

    @Deprecated("Use `rawValue`", ReplaceWith("rawValue"))
    val value: Int get() = rawValue

    companion object {

        val leading = HorizontalGravity(START)
        val top = VerticalGravity(TOP)
        val trailing = HorizontalGravity(END)
        val bottom = VerticalGravity(BOTTOM)

        val center = CenterGravity()

        class VerticalGravity(@GravityInt rawValue: Int) : Gravity(rawValue) {
            val leading = Gravity(rawValue or START)
            val center = Gravity(rawValue or CENTER_HORIZONTAL)
            val trailing = Gravity(rawValue or END)
        }

        class HorizontalGravity(@GravityInt rawValue: Int) : Gravity(rawValue) {
            val top = Gravity(rawValue or TOP)
            val center = Gravity(rawValue or CENTER_VERTICAL)
            val bottom = Gravity(rawValue or BOTTOM)
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
        return "Gravity($rawValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Gravity

        if (rawValue != other.rawValue) return false

        return true
    }

    override fun hashCode(): Int {
        return rawValue
    }

    @SuppressLint("RtlHardcoded")
    fun hasLeading() = checkHorizontal(LEFT)
    fun hasTop() = checkVertical(TOP)
    @SuppressLint("RtlHardcoded")
    fun hasTrailing() = checkHorizontal(RIGHT)
    fun hasBottom() = checkVertical(BOTTOM)
    fun hasCenter() = checkHorizontal(CENTER_HORIZONTAL) || checkVertical(CENTER_VERTICAL)

    private fun checkHorizontal(expected: Int): Boolean {
        return expected == (rawValue and HORIZONTAL_GRAVITY_MASK)
    }

    private fun checkVertical(expected: Int): Boolean {
        return expected == (rawValue and VERTICAL_GRAVITY_MASK)
    }
}

typealias GravityBuilder = Gravity.Companion.() -> Gravity