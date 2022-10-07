package io.noties.adapt.ui.util

import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.CENTER_VERTICAL
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.Gravity.TOP

@Suppress("ClassName")
interface Gravity {
    val gravityValue: Int

    companion object {
        val center: __Meta.Center get() = __Meta.Center
        val leading: __Meta.Leading get() = __Meta.Leading
        val top: __Meta.Top get() = __Meta.Top
        val trailing: __Meta.Trailing get() = __Meta.Trailing
        val bottom: __Meta.Bottom get() = __Meta.Bottom
    }

    object __Meta {

        interface HasLeading : Gravity {
            val leading: Gravity get() = Impl(gravityValue or START)
        }

        interface HasTop : Gravity {
            val top: Gravity get() = Impl(gravityValue or TOP)
        }

        interface HasTrailing : Gravity {
            val trailing: Gravity get() = Impl(gravityValue or END)
        }

        interface HasBottom : Gravity {
            val bottom: Gravity get() = Impl(gravityValue or BOTTOM)
        }

        object Leading : Gravity, HasTop, HasBottom {
            override val gravityValue: Int
                get() = START
        }

        object Top : Gravity, HasLeading, HasTrailing {
            override val gravityValue: Int
                get() = TOP
        }

        object Trailing : Gravity, HasTop, HasBottom {
            override val gravityValue: Int
                get() = END
        }

        object Bottom : Gravity, HasLeading, HasTrailing {
            override val gravityValue: Int
                get() = BOTTOM
        }

        object Center : Gravity {
            override val gravityValue: Int
                get() = CENTER

            val vertical: Gravity get() = Impl(CENTER_VERTICAL)
            val horizontal: Gravity get() = Impl(CENTER_HORIZONTAL)
        }

        internal class Impl(override val gravityValue: Int) : Gravity
    }
}