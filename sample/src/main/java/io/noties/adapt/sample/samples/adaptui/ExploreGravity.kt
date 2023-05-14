package io.noties.adapt.sample.samples.adaptui

import androidx.annotation.GravityInt
import io.noties.debug.Debug
import android.view.Gravity as _Gravity

object ExploreGravity {

    open class Gravity(@GravityInt val value: Int) {

        companion object {

            val leading = HorizontalGravity(_Gravity.START)
            val top = VerticalGravity(_Gravity.TOP)
            val trailing = HorizontalGravity(_Gravity.END)
            val bottom = VerticalGravity(_Gravity.BOTTOM)

            val center = CenterGravity()

            class VerticalGravity(@GravityInt value: Int) : Gravity(value) {
                val leading = Gravity(value or _Gravity.START)
                val center = Gravity(value or _Gravity.CENTER_HORIZONTAL)
                val trailing = Gravity(value or _Gravity.END)
            }

            class HorizontalGravity(@GravityInt value: Int) : Gravity(value) {
                val top = Gravity(value or _Gravity.TOP)
                val center = Gravity(value or _Gravity.CENTER_VERTICAL)
                val bottom = Gravity(value or _Gravity.BOTTOM)
            }

            class CenterGravity : Gravity(_Gravity.CENTER) {
                val horizontal = Gravity(_Gravity.CENTER_HORIZONTAL)
                val vertical = Gravity(_Gravity.CENTER_VERTICAL)

                val top = Gravity(_Gravity.TOP or _Gravity.CENTER_HORIZONTAL)
                val bottom = Gravity(_Gravity.BOTTOM or _Gravity.CENTER_HORIZONTAL)

                val leading = Gravity(_Gravity.START or _Gravity.CENTER_VERTICAL)
                val trailing = Gravity(_Gravity.END or _Gravity.CENTER_VERTICAL)
            }
        }

        override fun toString(): String {
            return "Gravity($value)"
        }

    }

    fun hey() {
        val entries = listOf(
            "leading" to Gravity.leading,
            "top" to Gravity.top,
            "trailing" to Gravity.trailing,
            "bottom" to Gravity.bottom,
            "leading.top" to Gravity.leading.top,
            "leading.center" to Gravity.leading.center,
            "leading.bottom" to Gravity.leading.bottom,
            "top.leading" to Gravity.top.leading,
            "top.center" to Gravity.top.center,
            "top.trailing" to Gravity.top.trailing,
            "trailing.top" to Gravity.trailing.top,
            "trailing.center" to Gravity.trailing.center,
            "trailing.bottom" to Gravity.trailing.bottom,
            "bottom.leading" to Gravity.bottom.leading,
            "bottom.center" to Gravity.bottom.center,
            "bottom.trailing" to Gravity.bottom.trailing,
            "center" to Gravity.center,
            "center.leading" to Gravity.center.leading,
            "center.top" to Gravity.center.top,
            "center.trailing" to Gravity.center.trailing,
            "center.bottom" to Gravity.center.bottom,
            "center.vertical" to Gravity.center.vertical,
            "center.horizontal" to Gravity.center.horizontal,
            "raw(center)" to Gravity(_Gravity.CENTER),
            "raw(leading|center_vertical)" to Gravity(_Gravity.START or _Gravity.CENTER_VERTICAL)
        )

        for (entry in entries) {
            Debug.i("'${entry.first}':${entry.second}")
        }
    }
}