package io.noties.adapt.ui.state

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable

typealias DrawableStateListBuilder = DrawableStateListFactory.() -> Unit

class DrawableStateListFactory : StateListBuilder<Drawable>() {
    companion object {
        fun build(block: DrawableStateListFactory.() -> Unit): DrawableStateListFactory {
            val builder = DrawableStateListFactory()
            block(builder)
            return builder
        }
    }

    override val instance: Drawable = ColorDrawable(0)

    val stateListDrawable: StateListDrawable
        get() = run {
            val drawable = StateListDrawable()
            split()
                .forEach { (k, v) ->
                    drawable.addState(k, v)
                }
            drawable
        }
}