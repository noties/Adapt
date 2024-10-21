package io.noties.adapt.sample.explore

import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeFactory

object ExploreColorStateListBuilderDSL {



    //////////////////////////////////////////////////

    interface DrawableStateBuilder {
        val rawValues: MutableSet<Int>

        val pressed get() = add(android.R.attr.state_pressed)
        val focused get() = add(android.R.attr.state_focused)
        val selected get() = add(android.R.attr.state_selected)
        val enabled get() = add(android.R.attr.state_enabled)
        val activated get() = add(android.R.attr.state_activated)
        val checked get() = add(android.R.attr.state_checked)

        fun raw(@AttrRes vararg attrs: Int) = this.also {
            rawValues.addAll(attrs.toSet())
        }

        fun add(@AttrRes rawValue: Int) = this.also {
            rawValues.add(rawValue)
        }
    }

    @JvmInline
    value class DrawableState(@AttrRes val rawValues: Set<Int>) {
        constructor(@AttrRes rawValue: Int) : this(setOf(rawValue))

        companion object {
//            fun build(block: DrawableStateBuilder.() -> DrawableStateBuilder): DrawableState {
//
//            }
        }
    }

    open class StateListBuilder<T> : DrawableStateBuilder {
        val entries = mutableMapOf<DrawableState, T>()

        infix fun DrawableState.to(value: T) {
            entries[this] = value
        }

        override val rawValues: MutableSet<Int>
            get() = TODO("Not yet implemented")
    }

    class StateListBuilderColor : StateListBuilder</*@ColorInt*/ Int>()
    class StateListBuilderDrawable : StateListBuilder<Drawable>()
    class StateListBuilderShape : StateListBuilder<Shape>(), ShapeFactory {
        // no need to add root, it must be automatically tracked
        override fun add(shape: Shape) = Unit
    }

    fun hey() {
        fun buildColor(block: StateListBuilderColor.() -> Unit) = Unit
        fun buildShape(block: StateListBuilderShape.() -> Unit) = Unit

        buildColor {
            pressed to 1
            pressed.activated to 2
            pressed.raw(0).enabled to 3
        }

        buildShape {
            pressed to Rectangle()
            pressed.enabled to Circle {
                fill { black }
            }
        }
    }

//    class Root: StateHolder {
//        override val set: MutableSet<Int>
//            get() = TODO("Not yet implemented")
//
//    }

//    class State(
//        override val set: MutableSet<Int> = mutableSetOf()
//    ): StateHolder {
////        companion object: StateHolder {
////            override val set: MutableSet<Int>
////                get() = TODO("Not yet implemented")
////        }
//    }

//    abstract class StateListBuilder<T> {
////        val set = mutableSetOf<T>()
//        val entries = mutableMapOf<Set<DrawableState>, T>()
//    }

//    class StateColorBuilder<T> : StateHolder {
//        val entries = mutableMapOf<Set<Int>, Int>()
//
//        infix fun DrawableState.to(value: T) {
//            entries[set.toSet()] = color
//            // clear after added!!
//            set.clear()
//        }
//    }

//    interface StateHolder {
//        val set: MutableSet<Int>
//
//        val enabled: StateHolder
//            get() = this.also {
//                set.add(android.R.attr.state_enabled)
//            }
//
//        val pressed: StateHolder
//            get() = this.also {
//                set.add(android.R.attr.state_pressed)
//            }
//
//        val activated: StateHolder
//            get() = this.also {
//                set.add(android.R.attr.state_activated)
//            }
//
//        fun raw(@AttrRes vararg attrs: Int) = this.also {
//            attrs.forEach { set.add(it) }
//        }
//    }

//    val enabled: State
//        get() = State.also {
//            set.add(android.R.attr.state_enabled)
//        }
//
//    val pressed: State
//        get() = State.also {
//            set.add(android.R.attr.state_pressed)
//        }
//
//    val activated: State
//        get() = State.also {
//            set.add(android.R.attr.state_activated)
//        }
//
//    fun raw(@AttrRes vararg attrs: Int) = State.also {
//        attrs.forEach { set.add(it) }
//    }

//
//    interface StateHolderBUILDERState {
//
//    }
}