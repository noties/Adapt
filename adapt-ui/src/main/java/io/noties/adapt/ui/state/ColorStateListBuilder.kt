package io.noties.adapt.ui.state

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import io.noties.adapt.ui.app.color.Colors

typealias ColorStateListBuilder = ColorStateListFactory.() -> Unit

class ColorStateListFactory : StateListBuilder</*@ColorInt*/ Int>(), Colors {
    companion object {
        fun build(block: ColorStateListFactory.() -> Unit): ColorStateListFactory {
            val builder = ColorStateListFactory()
            block(builder)
            return builder
        }
    }

    override val instance: Int = 0

    val colorStateList: ColorStateList
        get() = run {
            val split = split()
            val states = Array<IntArray>(split.size) { intArrayOf() }
            val values = IntArray(split.size)

            split
                .withIndex()
                .forEach { (i, v) ->
                    states[i] = v.first
                    values[i] = v.second
                }

            ColorStateList(
                states,
                values
            )
        }

    val stateListDrawable: StateListDrawable
        get() = run {
            val drawable = StateListDrawable()
            split()
                .forEach { (k, v) ->
                    drawable.addState(k, ColorDrawable(v))
                }
            drawable
        }
}