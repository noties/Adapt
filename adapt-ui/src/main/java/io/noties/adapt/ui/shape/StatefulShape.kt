package io.noties.adapt.ui.shape

import android.graphics.drawable.StateListDrawable
import android.util.StateSet

class StatefulShape {

    companion object {
        fun drawable(block: StatefulShape.() -> Unit): StateListDrawable {
            val instance = StatefulShape()
            block(instance)
            return instance.drawable()
        }
    }

    private val entries = mutableListOf<Pair<IntArray, Shape>>()
    private var defaultEntry: Shape? = null

    fun drawable(): StateListDrawable {
        val drawable = StateListDrawable()
        entries.forEach {
            drawable.addState(it.first, it.second.newDrawable())
        }
        // must be applied last... otherwise it will be used for all states
        defaultEntry?.also {
            drawable.addState(StateSet.WILD_CARD, it.newDrawable())
        }
        return drawable
    }

    fun set(state: Int, shape: Shape): StatefulShape =
        this.set(intArrayOf(state), shape)

    fun set(state: IntArray, shape: Shape): StatefulShape =
        this.also {
            if (StateSet.isWildCard(state)) {
                defaultEntry = shape
            } else {
                entries.add(state to shape)
            }
        }

    fun setPressed(shape: Shape): StatefulShape =
        set(android.R.attr.state_pressed, shape)

    fun setEnabled(shape: Shape): StatefulShape =
        set(android.R.attr.state_enabled, shape)

    fun setFocused(shape: Shape): StatefulShape =
        set(android.R.attr.state_focused, shape)

    fun setActivated(shape: Shape): StatefulShape =
        set(android.R.attr.state_activated, shape)

    fun setSelected(shape: Shape): StatefulShape =
        set(android.R.attr.state_selected, shape)

    fun setDefault(shape: Shape): StatefulShape =
        set(StateSet.WILD_CARD, shape)

    // postpone for now, `default` would not be very pretty, plus it exposes getter, which is not good
//    var pressed: Shape by StateProperty(android.R.attr.state_pressed)
//
//
//    // I would have made getter private, but unfortunately it is not possible with Kotlin at the moment
//    private class StateProperty(val state: Int) : ReadWriteProperty<StatefulShape, Shape> {
//        // strictly speaking there should not be getter
//        override fun getValue(thisRef: StatefulShape, property: KProperty<*>): Shape {
//            return thisRef.entries.firstOrNull {
//                it.first.size == 1 && it.first[0] == state
//            }?.second ?: error()
//        }
//
//        override fun setValue(thisRef: StatefulShape, property: KProperty<*>, value: Shape) {
//            thisRef.set(state, value)
//        }
//    }
}