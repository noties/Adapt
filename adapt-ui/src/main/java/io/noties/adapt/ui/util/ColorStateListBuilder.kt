package io.noties.adapt.ui.util

import android.content.res.ColorStateList
import android.util.StateSet
import androidx.annotation.ColorInt
import io.noties.adapt.ui.state.DrawableState

class ColorStateListBuilder {

    companion object {
        fun create(block: ColorStateListBuilder.() -> Unit): ColorStateList {
            val builder = ColorStateListBuilder()
            block(builder)
            return builder.build()
        }
    }

    fun build(): ColorStateList {
        val (states, colors) = this.entries
            .toList()
            // wild card must be applied last
            .sortedByDescending { it.first.size }
            .map {
                val key = if (it.first.isEmpty()) {
                    StateSet.WILD_CARD
                } else {
                    it.first.map { ds -> ds.value }.toIntArray()
                }
                key to it.second
            }
            .associate { it.first to it.second }
            .let {
                it.keys to it.values
            }

        return ColorStateList(states.toTypedArray(), colors.toIntArray())
    }

    fun set(set: Set<DrawableState>, @ColorInt color: Int) = this.also {
        it.entries[set] = color
    }

    fun set(state: DrawableState, @ColorInt color: Int) = set(setOf(state), color)

    fun setPressed(@ColorInt color: Int) = set(DrawableState.pressed, color)
    fun setEnabled(@ColorInt color: Int) = set(DrawableState.enabled, color)
    fun setFocused(@ColorInt color: Int) = set(DrawableState.focused, color)
    fun setActivated(@ColorInt color: Int) = set(DrawableState.activated, color)
    fun setSelected(@ColorInt color: Int) = set(DrawableState.selected, color)
    fun setChecked(@ColorInt color: Int) = set(DrawableState.checked, color)

    fun setDefault(@ColorInt color: Int) = set(emptySet(), color)

    @Deprecated(
        "use DrawableState version",
        replaceWith = ReplaceWith("set(DrawableState(state), color)")
    )
    fun set(state: Int, @ColorInt color: Int) = set(DrawableState(state), color)

    @Deprecated("use DrawableState version")
    fun set(states: IntArray, @ColorInt color: Int) = this.also {
        val s = if (StateSet.isWildCard(states)) {
            emptySet()
        } else {
            states.map { attr -> DrawableState(attr) }.toSet()
        }
        it.set(s, color)
    }

    internal val entries = mutableMapOf<Set<DrawableState>, Int>()
}