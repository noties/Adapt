package io.noties.adapt.ui.shape

import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import io.noties.adapt.ui.util.DrawableState

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

    fun set(set: Set<DrawableState>, block: ShapeFactoryBuilder) =
        set(set, block(ShapeFactory.NoOp))

    fun set(state: DrawableState, shape: Shape) = set(setOf(state), shape)
    fun set(state: DrawableState, block: ShapeFactoryBuilder) = set(state, block(ShapeFactory.NoOp))

    fun setPressed(shape: Shape) = set(setOf(DrawableState.pressed), shape)
    fun setPressed(block: ShapeFactoryBuilder) = setPressed(block(ShapeFactory.NoOp))

    fun setEnabled(shape: Shape) = set(setOf(DrawableState.enabled), shape)
    fun setEnabled(block: ShapeFactoryBuilder) = setEnabled(block(ShapeFactory.NoOp))

    fun setFocused(shape: Shape) = set(setOf(DrawableState.focused), shape)
    fun setFocused(block: ShapeFactoryBuilder) = setFocused(block(ShapeFactory.NoOp))

    fun setActivated(shape: Shape) = set(setOf(DrawableState.activated), shape)
    fun setActivated(block: ShapeFactoryBuilder) = setActivated(block(ShapeFactory.NoOp))

    fun setSelected(shape: Shape) = set(setOf(DrawableState.selected), shape)
    fun setSelected(block: ShapeFactoryBuilder) = setSelected(block(ShapeFactory.NoOp))

    fun setChecked(shape: Shape) = set(setOf(DrawableState.checked), shape)
    fun setChecked(block: ShapeFactoryBuilder) = setChecked(block(ShapeFactory.NoOp))

    fun setDefault(shape: Shape) = set(setOf(), shape)
    fun setDefault(block: ShapeFactoryBuilder) = setDefault(block(ShapeFactory.NoOp))

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