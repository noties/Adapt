package io.noties.adapt.ui.shape

import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import io.noties.adapt.ui.state.DrawableState

class StatefulShape {

    companion object {
        fun drawable(block: StatefulShape.() -> Unit): StateListDrawable {
            val instance = StatefulShape()
            block(instance)
            return instance.drawable()
        }
    }

    fun drawable(): StateListDrawable {
        val drawable = StateListDrawable()
        entries
            .toList()
            // wild_card must be applied last... otherwise it will be used for all states
            .sortedByDescending { it.first.size }
            .forEach { (key, value) ->
                val states = if (key.isEmpty()) {
                    StateSet.WILD_CARD
                } else {
                    key.map { it.value }.toIntArray()
                }
                drawable.addState(states, value.newDrawable())
            }
        return drawable
    }

    fun set(set: Set<DrawableState>, shape: Shape) = this.also {
        entries[set] = shape
    }

    fun set(set: Set<DrawableState>, block: () -> Shape) = set(set, block())

    fun set(state: DrawableState, shape: Shape) = set(setOf(state), shape)
    fun set(state: DrawableState, block: () -> Shape) = set(state, block())

    fun setPressed(shape: Shape) = set(setOf(DrawableState.pressed), shape)
    fun setPressed(block: () -> Shape) = setPressed(block())

    fun setEnabled(shape: Shape) = set(setOf(DrawableState.enabled), shape)
    fun setEnabled(block: () -> Shape) = setEnabled(block())

    fun setFocused(shape: Shape) = set(setOf(DrawableState.focused), shape)
    fun setFocused(block: () -> Shape) = setFocused(block())

    fun setActivated(shape: Shape) = set(setOf(DrawableState.activated), shape)
    fun setActivated(block: () -> Shape) = setActivated(block())

    fun setSelected(shape: Shape) = set(setOf(DrawableState.selected), shape)
    fun setSelected(block: () -> Shape) = setSelected(block())

    fun setChecked(shape: Shape) = set(setOf(DrawableState.checked), shape)
    fun setChecked(block: () -> Shape) = setChecked(block())

    fun setDefault(shape: Shape) = set(setOf(), shape)
    fun setDefault(block: () -> Shape) = setDefault(block())

    @Deprecated(
        "Use dedicated DrawableState",
        replaceWith = ReplaceWith("set(DrawableState(state), shape)")
    )
    fun set(state: Int, shape: Shape) = set(DrawableState(state), shape)

    @Deprecated("Use dedicated DrawableState")
    fun set(state: IntArray, shape: Shape) = this.also {
        val states = if (StateSet.isWildCard(state)) {
            emptySet()
        } else {
            state.map { DrawableState(it) }.toSet()
        }
        set(states, shape)
    }

    internal val entries = mutableMapOf<Set<DrawableState>, Shape>()
}