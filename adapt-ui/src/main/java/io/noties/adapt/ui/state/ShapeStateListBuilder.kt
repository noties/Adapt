package io.noties.adapt.ui.state

import android.graphics.drawable.StateListDrawable
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeFactory

typealias ShapeStateListBuilder = ShapeStateListFactory.() -> Unit

class ShapeStateListFactory : StateListBuilder<Shape>(), ShapeFactory {
    companion object {
        fun build(block: ShapeStateListFactory.() -> Unit): ShapeStateListFactory {
            val builder = ShapeStateListFactory()
            block(builder)
            return builder
        }
    }

    override val instance: Shape = RectangleShape()
    override fun add(shape: Shape) = Unit

    val stateListDrawable: StateListDrawable
        get() = run {
            val drawable = StateListDrawable()
            split()
                .forEach { (k, v) ->
                    drawable.addState(k, v.newDrawable())
                }
            drawable
        }
}