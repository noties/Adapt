package io.noties.adapt.ui.util

import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.Gravity.TOP
import androidx.annotation.GravityInt

/**
 * Utility to specify [android.view.Gravity] in a type-safe manner
 * without manual xor operation. `Gravity.CENTER OR Gravity.TOP` becomes
 * `Gravity.center.top`.
 * Direct child of Gravity allows further customization of opposite axis,
 * so `Gravity.top` (y) has `.center`, `.leading` and `.trailing` (x).
 * And `Gravity.leading` (x) has `.center`, `.top` and `.bottom` (y).
 */
interface Gravity {

    @get:GravityInt
    val value: Int

    companion object {
        val center: __Meta.Center get() = __Meta.Center
        val leading: __Meta.Leading get() = __Meta.Leading
        val top: __Meta.Top get() = __Meta.Top
        val trailing: __Meta.Trailing get() = __Meta.Trailing
        val bottom: __Meta.Bottom get() = __Meta.Bottom

        fun raw(@GravityInt gravity: Int): Gravity = __Meta.Raw("raw", gravity)
    }

    @Suppress("ClassName")
    object __Meta {

        interface HasName {
            val name: String
        }

        interface HasCenter : Gravity, HasName {
            val center: Gravity get() = Raw("$name.center", value or CENTER)
        }

        interface HasLeading : Gravity, HasName {
            val leading: Gravity get() = Raw("$name.leading", value or START)
        }

        interface HasTop : Gravity, HasName {
            val top: Gravity get() = Raw("$name.top", value or TOP)
        }

        interface HasTrailing : Gravity, HasName {
            val trailing: Gravity get() = Raw("$name.trailing", value or END)
        }

        interface HasBottom : Gravity, HasName {
            val bottom: Gravity get() = Raw("$name.bottom", value or BOTTOM)
        }

        object Leading : Gravity, HasTop, HasBottom, HasCenter {
            override val value: Int
                get() = START
            override val name: String
                get() = ".leading"

            override fun toString(): String = __Meta.toString(this)
        }

        object Top : Gravity, HasLeading, HasTrailing, HasCenter {
            override val value: Int
                get() = TOP
            override val name: String
                get() = ".top"

            override fun toString(): String = __Meta.toString(this)
        }

        object Trailing : Gravity, HasTop, HasBottom, HasCenter {
            override val value: Int
                get() = END
            override val name: String
                get() = ".trailing"

            override fun toString(): String = __Meta.toString(this)
        }

        object Bottom : Gravity, HasName, HasLeading, HasTrailing, HasCenter {
            override val value: Int
                get() = BOTTOM
            override val name: String
                get() = ".bottom"

            override fun toString(): String = __Meta.toString(this)
        }

        // It seems we do not need to specify CENTER_VERTICAL or CENTER_HORIZONTAL
        //  as xor with CENTER yield proper results:
        //  CENTER||START:8388627 CENTER_VERTICAL||START:8388627
        //  CENTER||TOP:49 CENTER_HORIZONTAL||TOP:49
        //  CENTER||END:8388629 CENTER_VERTICAL||END:8388629
        //  CENTER||BOTTOM:81 CENTER_HORIZONTAL||BOTTOM:81
        object Center : Gravity, HasName, HasLeading, HasTop, HasTrailing, HasBottom {
            override val value: Int
                get() = CENTER
            override val name: String
                get() = ".center"

            override fun toString(): String = __Meta.toString(this)

            val horizontal: Gravity
                get() = Raw(
                    "$name.horizontal",
                    android.view.Gravity.CENTER_HORIZONTAL
                )
            val vertical: Gravity
                get() = Raw(
                    "$name.vertical",
                    android.view.Gravity.CENTER_VERTICAL
                )
        }

        internal fun <T> toString(t: T): String where T : HasName, T : Gravity {
            return "Gravity(${t.name}, ${t.value})"
        }

        internal class Raw(
            override val name: String,
            override val value: Int
        ) : Gravity, HasName {

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                other as Raw
                if (value != other.value) return false
                return true
            }

            override fun hashCode(): Int {
                return value
            }

            override fun toString(): String = __Meta.toString(this)
        }
    }
}