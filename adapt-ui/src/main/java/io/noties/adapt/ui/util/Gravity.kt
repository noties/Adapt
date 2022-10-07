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
    val gravityValue: Int

    companion object {
        val center: __Meta.Center get() = __Meta.Center
        val leading: __Meta.Leading get() = __Meta.Leading
        val top: __Meta.Top get() = __Meta.Top
        val trailing: __Meta.Trailing get() = __Meta.Trailing
        val bottom: __Meta.Bottom get() = __Meta.Bottom

        fun raw(@GravityInt gravity: Int): Gravity = __Meta.Raw(gravity)
    }

    @Suppress("ClassName")
    object __Meta {

        interface HasCenterHorizontal : Gravity {
            val center: Gravity get() = Raw(gravityValue or CENTER)
        }

        interface HasCenterVertical : Gravity {
            val center: Gravity get() = Raw(gravityValue or CENTER)
        }

        interface HasLeading : Gravity {
            val leading: Gravity get() = Raw(gravityValue or START)
        }

        interface HasTop : Gravity {
            val top: Gravity get() = Raw(gravityValue or TOP)
        }

        interface HasTrailing : Gravity {
            val trailing: Gravity get() = Raw(gravityValue or END)
        }

        interface HasBottom : Gravity {
            val bottom: Gravity get() = Raw(gravityValue or BOTTOM)
        }

        object Leading : Gravity, HasTop, HasBottom, HasCenterVertical {
            override val gravityValue: Int
                get() = START
        }

        object Top : Gravity, HasLeading, HasTrailing, HasCenterHorizontal {
            override val gravityValue: Int
                get() = TOP
        }

        object Trailing : Gravity, HasTop, HasBottom, HasCenterVertical {
            override val gravityValue: Int
                get() = END
        }

        object Bottom : Gravity, HasLeading, HasTrailing, HasCenterHorizontal {
            override val gravityValue: Int
                get() = BOTTOM
        }

        // It seems we do not need to specify CENTER_VERTICAL or CENTER_HORIZONTAL
        //  as xor with CENTER yield proper results:
        //  CENTER||START:8388627 CENTER_VERTICAL||START:8388627
        //  CENTER||TOP:49 CENTER_HORIZONTAL||TOP:49
        //  CENTER||END:8388629 CENTER_VERTICAL||END:8388629
        //  CENTER||BOTTOM:81 CENTER_HORIZONTAL||BOTTOM:81
        object Center : Gravity, HasLeading, HasTop, HasTrailing, HasBottom {
            override val gravityValue: Int
                get() = CENTER
        }

        internal class Raw(override val gravityValue: Int) : Gravity
    }
}