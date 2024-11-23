package io.noties.adapt.sample.explore

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import android.view.View
import android.widget.Checkable
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Oval
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeFactory
import io.noties.debug.Debug
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("StopShip")
class ExploreViewState {

    interface VSBuilder {
        // unique name to not have collisions with any other _builder_ (or any other functionality)
        val viewStateBuilderRawValues: Set<Int>

        val pressed get() = ViewState(viewStateBuilderRawValues + android.R.attr.state_pressed)
        val focused get() = ViewState(viewStateBuilderRawValues + android.R.attr.state_focused)
        val selected get() = ViewState(viewStateBuilderRawValues + android.R.attr.state_selected)
        val enabled get() = ViewState(viewStateBuilderRawValues + android.R.attr.state_enabled)
        val activated get() = ViewState(viewStateBuilderRawValues + android.R.attr.state_activated)
        val checked get() = ViewState(viewStateBuilderRawValues + android.R.attr.state_checked)

        // no need to use rawValues, default is always empty and cannot be modified
        val default get() = ViewState(emptySet())

        fun raw(@AttrRes vararg attrs: Int) = ViewState(viewStateBuilderRawValues + attrs.toSet())
    }

    /**
     * Actually `DrawableState` is awful and criptic and not obvious.
     * In the end it is drawable that can react to tthis state, but it is mostly
     * referenced from view context, and view can be set with these styles
     */
    @JvmInline
    value class ViewState(@AttrRes val rawValues: Set<Int>) : VSBuilder {
        companion object : VSBuilder {

            fun attrName(
                @AttrRes attr: Int,
                resources: Resources = Resources.getSystem()
            ): String = try {
                resources.getResourceName(attr)
            } catch (t: Throwable) {
                "$attr"
            }

            // always empty initial
            override val viewStateBuilderRawValues: Set<Int> = emptySet()
        }

        // here we can helper functions to check for certain presence, like:
        //  which would provide helpers in some contexts

        val isPressed: Boolean get() = contains(android.R.attr.state_pressed)
        val isFocused: Boolean get() = contains(android.R.attr.state_focused)
        val isSelected: Boolean get() = contains(android.R.attr.state_selected)
        val isEnabled: Boolean get() = contains(android.R.attr.state_enabled)
        val isActivated: Boolean get() = contains(android.R.attr.state_activated)
        val isChecked: Boolean get() = contains(android.R.attr.state_checked)

        /**
         * ```kotlin
         * val state: ViewState = view.viewState
         * if (state.contains(R.attr.my_attr, R.attr.some_other_attr)) {}
         * if (state.contains { pressed.activated }) {}
         * ```
         */
        fun contains(@AttrRes vararg rawValues: Int): Boolean {
            // search for first NOT contained in this.rawValues
            //  if it is present (not null) then there are values that are not present
            //  thus, whole is not contained in the this.rawValues
            return rawValues.firstOrNull {
                !this.rawValues.contains(it)
            } != null
        }

        // optimization? to not allocate array when called vararg version
        fun contains(@AttrRes rawValue: Int): Boolean {
            return rawValues.contains(rawValue)
        }
//
//        fun contains(block: VSBuilder.() -> Unit): Boolean {
//            val builder = ViewStateBuilder.create()
//            block(builder)
//            return contains(builder.viewStateBuilderRawValues)
//        }

        fun contains(collection: Collection<Int>): Boolean {
            return collection.firstOrNull {
                !rawValues.contains(it)
            } != null
        }

        fun contains(viewState: ViewState): Boolean {
            return rawValues.containsAll(viewState.rawValues)
        }

        override val viewStateBuilderRawValues: Set<Int>
            get() = rawValues

        override fun toString(): String {
            val states = rawValues.joinToString { attrName(it) }
            return "ViewState($states)"
        }
    }

    var View.viewState: ViewState
        get() = ViewState(drawableState.toSet())
        set(value) {
            if (value.isPressed) this.isPressed = true
            if (value.isFocused) this.requestFocus()
            if (value.isSelected) this.isSelected = true
            if (value.isEnabled) this.isEnabled = true
            if (value.isActivated) this.isActivated = true
            if (value.isChecked) (this as? Checkable)?.isChecked = true
            invalidate()
        }

    fun ImageView.hey() {
        this.viewState = ViewState.activated
    }

    interface ViewStateBuilder {
        // unique name to not have collisions with any other _builder_ (or any other functionality)
        val viewStateBuilderRawValues: MutableSet<Int>

        val pressed get() = add(android.R.attr.state_pressed)
        val focused get() = add(android.R.attr.state_focused)
        val selected get() = add(android.R.attr.state_selected)
        val enabled get() = add(android.R.attr.state_enabled)
        val activated get() = add(android.R.attr.state_activated)
        val checked get() = add(android.R.attr.state_checked)

        // aka `wild card`
        // if preceded with any other looses meaning and disappears
        //  default -> StateSet.WILD_CARD
        //  default.pressed -> [state_pressed]
        val default get() = raw()

        fun raw(@AttrRes vararg attrs: Int) = this.also {
            viewStateBuilderRawValues.addAll(attrs.toSet())
        }

        fun add(@AttrRes rawValue: Int) = this.also {
            viewStateBuilderRawValues.add(rawValue)
        }

        // like _shared_ in iOS, instance used by default
//        companion object : ViewStateBuilder {
//            override val viewStateBuilderRawValues: MutableSet<Int> = mutableSetOf()
//        }

        companion object {
            fun create(): ViewStateBuilder = object : ViewStateBuilder {
                override val viewStateBuilderRawValues: MutableSet<Int> = mutableSetOf()
            }
        }
    }

    abstract class ViewStateListBuilder<T> : ViewStateBuilder {
        // ViewState must have proper hashCode, so set of attrs in any order is returning the same result
        val entries: MutableMap<ViewState, T> = mutableMapOf()

        override val viewStateBuilderRawValues: MutableSet<Int> = mutableSetOf()

        infix fun ViewStateBuilder.to(value: T) {
            // copy values
            entries[ViewState(viewStateBuilderRawValues.toSet())] = value
            viewStateBuilderRawValues.clear()
        }

        fun split(): Pair<Array<IntArray>, Array<T>> {
//            return states to values
            val (states, values) = this.entries
                .toList()
                // wild card must be applied last
                .sortedByDescending {
                    it.first.rawValues.size
                }
                .map {
                    val key = if (it.first.rawValues.isEmpty()) {
                        StateSet.WILD_CARD
                    } else {
                        it.first.rawValues.toIntArray()
                    }
                    key to it.second
                }
                .associate { it.first to it.second }
                .let {
                    it.keys to it.values
                }

            return states.toTypedArray() to createTypedArray(values)
        }

        protected abstract fun createTypedArray(values: Collection<T>): Array<T>
    }

    abstract class StateBuilder<T> {
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

        fun T.raw(@AttrRes vararg attrs: Int): T = this@StateBuilder.raw(*attrs)

        protected fun split(): Pair<Array<IntArray>, Collection<T>> {
//            return states to values
            val (states, values) = this.entries
                .toList()
                // wild card must be applied last
                .sortedByDescending {
                    it.first.size
                }
                .map {
                    val key = if (it.first.isEmpty()) {
                        StateSet.WILD_CARD
                    } else {
                        it.first.toIntArray()
                    }
                    key to it.second
                }
                .associate { it.first to it.second }
                .let {
                    it.keys to it.values
                }

            return states.toTypedArray() to values
        }

        private class MutableProp<T>(
            val builder: StateBuilder<T>,
            @AttrRes val attr: Int
        ) : ReadWriteProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                builder.attrs.add(attr)
                return builder.instance
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                val set = builder.attrs.toSet()
                builder.entries[set] = value
                builder.attrs.clear()
            }
        }
    }

    class StateBuilderShape : StateBuilder<Shape>(), ShapeFactory {
        override fun add(shape: Shape) = Unit
        override val instance: Shape = Rectangle()
    }

    fun heyhey() {
        fun block(block: StateBuilderShape.() -> Unit) {
            val builder = StateBuilderShape()
            block(builder)
            Debug.i("entries:${builder.entries}")
        }

        block {
            pressed.pressed = Rectangle()
            pressed = Circle()
            pressed.activated = Oval()

        }
    }

    class ViewStateListBuilderColor : ViewStateListBuilder<Int>() {

        val colorStateList: ColorStateList
            get() = run {
                val (states, colors) = split()
                return ColorStateList(states, colors.toIntArray())
            }

        override fun createTypedArray(values: Collection<Int>): Array<Int> {
            return values.toTypedArray()
        }
    }

    open class ViewStateListBuilderShape : ViewStateListBuilder<Shape>() {

        val stateListDrawable: StateListDrawable
            get() = run {
                val drawable = StateListDrawable()
                val (state, values) = split()
                state
                    .withIndex()
                    .forEach { (i, v) ->
                        drawable.addState(v, values[i].newDrawable())
                    }
                drawable
            }

        override fun createTypedArray(values: Collection<Shape>): Array<Shape> {
            return values.toTypedArray()
        }
    }


    class ViewStateListBuilderDrawable : ViewStateListBuilder<Drawable>() {

        val stateListDrawable: StateListDrawable
            get() = run {
                val drawable = StateListDrawable()
                val (state, values) = split()
                state
                    .withIndex()
                    .forEach { (i, v) ->
                        drawable.addState(v, values[i])
                    }
                drawable
            }

        override fun createTypedArray(values: Collection<Drawable>): Array<Drawable> {
            return values.toTypedArray()
        }
    }

    fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textColor(
        block: ViewStateListBuilderColor.() -> Unit
    ) = this.let {
        val builder = ViewStateListBuilderColor()
        block(builder)
        it.textColor(builder.colorStateList)
    }

    class ViewBackgroundBuilder : ViewStateListBuilderShape(), ShapeFactory {
        override fun add(shape: Shape) {
            TODO("Not yet implemented")
        }

        infix fun ViewStateBuilder.x(shape: Shape) {

        }
    }

    // backgroundWithState
    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.backgroundStatefull(
        block: ViewBackgroundBuilder.() -> Unit
    ) = this.also {
        val builder = ViewBackgroundBuilder()
        block(builder)
        it.background(builder.stateListDrawable)
    }

    fun hey(context: Context) {
        ViewFactory.createView(context) {
            View()
                .backgroundStatefull {
                    // ah... to is just creates pair and fuck it
                    // THIS IS AWESOME, but `to` is fucking awful, there is no guarantee that
                    //  it would add expected type and not just create a pair and
                    //  ignore it (as there is not check-result)
                    // a different infix function name...
                    //      pressed.activated at Rectangle {}
                    //      pressed.activated on Rectangle {}
                    //      pressed.activated with Rectangle {}
                    //      pressed.activated is Rectangle {}
                    //      pressed.activated = Rectangle {}
                    //      pressed.activated assign Rectangle {}
                    //      pressed.activated sets Rectangle {}
                    //      pressed.activated eq Rectangle {}
                    //      pressed.activated pairsWith Rectangle {}
                    //      pressed.activated as Rectangle {}
                    //      pressed.activated in Rectangle {}
                    //      pressed.activated con Rectangle {}
                    //      pressed.activated x Rectangle {}

                    // if only one would be required, it could be used,
                    //  accept Pair<Set<Int>, Shape>, but for multiple it does not work
                    //  as `to` can be potentially just create pair, and as kotlin
                    //  does not enforce usage, can go into abyss just like that
                    pressed.activated to Rectangle { }

                    pressed.activated x Circle { }

//                    pressed.activated = Circle { }
                }
        }
    }
}