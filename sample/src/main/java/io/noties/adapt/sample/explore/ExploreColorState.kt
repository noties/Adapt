package io.noties.adapt.sample.explore

import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.ui.app.color.Colors

object ExploreColorState {
    // we cannot use the same textColor, as builder must return single value,
    //  conditionally returning Int for a single color or ColorStateList does not seem possible right

    // empty, nothing to load
    // holder for possible states
    object ColorStates {
//        val enabledDisabled: Int

        fun build(block: ColorStateBuilder.() -> Unit) {

        }
    }

    fun hey() {
        ColorStates.build {

            with(Colors) {
                pressed to black
            }

            pressed to 0
            pressed.activated to 2
            pressed.activated.enabled.raw(0) to 4

            raw(0) to 3
            raw().pressed to 5

//            // can define scope, so all states inside are combined with pressed
//            pressed {
//                activated to 9
//                enabled to 19
//            }
//
        }
    }

    sealed class State: StateHolder

    /*data*/ object Pressed : State()
    /*data*/ object Enabled : State()
    /*data*/ object Activated : State()

    // not a state-holder, cannot be modified
    data class Raw(val attrs: IntArray) : State() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Raw) return false

            return attrs.contentEquals(other.attrs)
        }

        override fun hashCode(): Int {
            return attrs.contentHashCode()
        }
    }

    interface StateHolder {
//        val set: MutableSet<Int>

        val pressed get() = Pressed
        val enabled get() = Enabled
        val activated get() = Activated

        fun raw(@AttrRes vararg attrs: Int) = Raw(attrs)

//        fun pressed(block: StateHolder.() -> Unit) = TODO()
//        fun enabled(block: StateHolder.() -> Unit) = TODO()
//        fun activated(block: StateHolder.() -> Unit) = TODO()
    }


    class ColorStateBuilder : StateHolder {

        val entries = mutableMapOf<State, Int>()

        // cannot include raw in holder instance, as it should be the only call,
        //  no customization should be possible like `pressed.raw()` or `raw().pressed`
//        fun raw(attrs: IntArray) = Raw(attrs)

//        val pressed: State get() = Pressed

        infix fun State.to(@ColorInt color: Int) {
            entries[this] = color
        }


    }


}