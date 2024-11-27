package io.noties.adapt.ui.state

import android.content.res.ColorStateList
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import androidx.annotation.AttrRes
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class StateListBuilder<T> {
    companion object {
        fun color(color: ColorStateListBuilder): ColorStateList {
            return ColorStateListFactory.build(color).colorStateList
        }

        fun shape(shape: ShapeStateListBuilder): StateListDrawable {
            return ShapeStateListFactory.build(shape).stateListDrawable
        }

        fun drawable(drawable: DrawableStateListBuilder): StateListDrawable {
            return DrawableStateListFactory.build(drawable).stateListDrawable
        }
    }

    protected abstract val instance: T

    private val attrs = mutableSetOf<Int>()
    val entries = mutableMapOf</*@AttrRes*/Set<Int>, T>()

    var pressed: T by MutableProp(this, android.R.attr.state_pressed)
    var T.pressed: T by MutableProp(this, android.R.attr.state_pressed)

    var focused: T by MutableProp(this, android.R.attr.state_focused)
    var T.focused: T by MutableProp(this, android.R.attr.state_focused)

    var selected: T by MutableProp(this, android.R.attr.state_selected)
    var T.selected: T by MutableProp(this, android.R.attr.state_selected)

    var enabled: T by MutableProp(this, android.R.attr.state_enabled)
    var T.enabled: T by MutableProp(this, android.R.attr.state_enabled)

    var activated: T by MutableProp(this, android.R.attr.state_activated)
    var T.activated: T by MutableProp(this, android.R.attr.state_activated)

    var checked: T by MutableProp(this, android.R.attr.state_checked)
    var T.checked: T by MutableProp(this, android.R.attr.state_checked)

    // no need for T.default, as it is useless and is no-op
    var default: T
        get() = instance
        set(value) {
            entries[emptySet()] = value
        }

    fun raw(@AttrRes vararg attrs: Int): T = instance
        .also {
            attrs.forEach { this.attrs.add(it) }
        }

    fun T.raw(@AttrRes vararg attrs: Int): T = this@StateListBuilder.raw(*attrs)

    protected fun split(): List<Pair<IntArray, T>> {
        val result = this.entries
            .toList()
            .map {
                val key = if (it.first.isEmpty()) {
                    StateSet.WILD_CARD
                } else {
                    it.first.toIntArray()
                }
                key to it.second
            }
            .associate { it.first to it.second }
            .map { (k, v) ->
                k to v
            }
            // wild card must be applied last
            .sortedByDescending {
                it.first.size
            }

        return result
    }

    private class MutableProp<T>(
        val builder: StateListBuilder<T>,
        @AttrRes val attr: Int
    ) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            builder.attrs.add(attr)
            return builder.instance
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            // both get and set contribute to the attrs
            builder.attrs.add(attr)
            val set = builder.attrs.toSet()
            builder.entries[set] = value
            builder.attrs.clear()
        }
    }
}