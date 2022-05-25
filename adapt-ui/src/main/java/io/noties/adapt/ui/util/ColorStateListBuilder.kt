package io.noties.adapt.ui.util

import android.content.res.ColorStateList
import androidx.annotation.ColorInt

class ColorStateListBuilder {

    companion object {
        fun create(block: ColorStateListBuilder.() -> Unit): ColorStateList {
            val builder = ColorStateListBuilder()
            block(builder)
            return builder.build()
        }
    }

    fun build(): ColorStateList {
        val (states, colors) = this.states
            .associate {
                it.states to it.color
            }
            .let {
                it.keys to it.values
            }
        return ColorStateList(states.toTypedArray(), colors.toIntArray())
    }

    fun addState(state: Int, @ColorInt color: Int) = addState(intArrayOf(state), color)

    fun addState(states: IntArray, @ColorInt color: Int) = this.also {
        this.states.add(Entry(states, color))
    }

    fun setPressed(@ColorInt color: Int) = addState(android.R.attr.state_pressed, color)

    fun setEnabled(@ColorInt color: Int) = addState(android.R.attr.state_enabled, color)

    fun setFocused(@ColorInt color: Int) = addState(android.R.attr.state_focused, color)

    fun setActivated(@ColorInt color: Int) = addState(android.R.attr.state_activated, color)

    fun setDefault(@ColorInt color: Int) = addState(intArrayOf(), color)


    private class Entry(val states: IntArray, @ColorInt val color: Int)

    private val states = mutableListOf<Entry>()
}