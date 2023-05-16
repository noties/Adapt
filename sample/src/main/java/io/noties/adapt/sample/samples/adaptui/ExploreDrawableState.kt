package io.noties.adapt.sample.samples.adaptui

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.annotation.AttrRes
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.copy
import java.util.Arrays
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@JvmInline
value class DrawableState(@AttrRes val value: Int) {
    companion object {
        val pressed = DrawableState(android.R.attr.state_pressed)
        val focused = DrawableState(android.R.attr.state_focused)
        val selected = DrawableState(android.R.attr.state_selected)
        val enabled = DrawableState(android.R.attr.state_enabled)
        val activated = DrawableState(android.R.attr.state_activated)
        val checked = DrawableState(android.R.attr.state_checked)
    }

    operator fun plus(other: DrawableState): Set<DrawableState> {
        return setOf(this, other)
    }

    // https://stackoverflow.com/questions/15543186/how-do-i-create-colorstatelist-programmatically/58121371#58121371
    // It is possible to specify state by negating value.. for unfocused would be -attr.focused
    //  it it possible that this is a discarded impl, right now we do not receive negative values
}

class DrawableStateSet(@AttrRes val state: IntArray) {
    val pressed by Prop(DrawableState.pressed)
    val focused by Prop(DrawableState.focused)
    val selected by Prop(DrawableState.selected)
    val enabled by Prop(DrawableState.enabled)
    val activated by Prop(DrawableState.activated)
    val checked by Prop(DrawableState.checked)

    fun contains(set: Set<DrawableState>) = contains(this.state, set)
    fun contains(state: DrawableState) = contains(this.state, state)
    fun contains(@AttrRes state: IntArray) = contains(this.state, state)
    fun contains(@AttrRes attr: Int) = contains(this.state, attr)

    override fun toString(): String = toString(Resources.getSystem())

    fun toString(resources: Resources): String = state
        .joinToString(", ") {
            resourceName(resources, it)
        }
        .let {
            "DrawableStateSet($it)"
        }

    companion object {
        fun resourceName(resources: Resources = Resources.getSystem(), @AttrRes attr: Int): String =
            try {
                resources.getResourceName(attr)
            } catch (t: Throwable) {
                "$attr"
            }

        fun contains(@AttrRes state: IntArray, drawableState: DrawableState): Boolean {
            return state.contains(drawableState.value)
        }

        fun contains(@AttrRes state: IntArray, @AttrRes attr: Int): Boolean {
            return state.contains(attr)
        }

        fun contains(@AttrRes state: IntArray, set: Set<DrawableState>): Boolean {
            return set.all { state.contains(it.value) }
        }

        fun contains(@AttrRes state: IntArray, @AttrRes stateToMatch: IntArray): Boolean {
            return stateToMatch.all { state.contains(it) }
        }

        internal class Prop(val state: DrawableState) :
            ReadOnlyProperty<DrawableStateSet, Boolean> {

            // no synchronization
            private var cached: Boolean? = null

            override fun getValue(thisRef: DrawableStateSet, property: KProperty<*>): Boolean {
                return cached ?: kotlin.run {
                    thisRef.state.contains(state.value).also {
                        cached = it
                    }
                }
            }
        }
    }
}

/**
 * `pressed` is not reported without a stateful drawable for the `android.R.attr.state_pressed`, as
 * a workaround a [StatefulShape], [StateListDrawable] can be used with pressed state defined. Or a stateful drawable
 * with this state defined, for example, `backgroundDefaultSelectable()` or `foregroundDefaultSelectable()`.
 * There is an utility [ReportStateDrawable] drawable that allows report states without drawing anything
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onDrawableStateChange(
    action: (V, DrawableStateSet) -> Unit
) = onView {
    it.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
        var previous = it.drawableState
        override fun onPreDraw(): Boolean {
            val current = it.drawableState
            if (!Arrays.equals(previous, current)) {
                previous = current
                action(it, DrawableStateSet(current))
            }
            return true
        }
    })
}

/**
 * Special [Drawable] that reports being stateful and accepts incoming states. By default possibly can lead
 * to unneeded invalidations, consider specifying states that the view is interested to receive
 */
class ReportStateDrawable(val set: Set<DrawableState>) : Drawable() {

    private var previousState = intArrayOf()

    /**
     * Accepts only a single state
     */
    constructor(state: DrawableState) : this(setOf(state))

    /**
     * Accepts all the states
     */
    constructor() : this(setOf())

    override fun draw(canvas: Canvas) = Unit
    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

    override fun isStateful(): Boolean = true

    // we override the `setState`, so if this function is called -> the state had changed
    override fun onStateChange(state: IntArray): Boolean {
        // we could match before (was pressed), but not it is not, but we are not tracking it
        val result = set.isEmpty() || kotlin.run {
            // check current THEN previous (if not current, but previous, then there is also a change)
            DrawableStateSet.contains(state, set) || DrawableStateSet.contains(previousState, set)
        }
        previousState = state.copyOf()

        if (result) {
            invalidateSelf()
        }
        return result
    }

    override fun getIntrinsicWidth(): Int = 1
    override fun getIntrinsicHeight(): Int = 1
}

class StatefulShape private constructor() {
    companion object {
        fun create(block: StatefulShapeBuilder.() -> Unit): Drawable {
            val builder = StatefulShapeBuilder()
            block(builder)
            TODO()
        }
    }

    class StatefulShapeBuilder {
        operator fun set(state: DrawableState, shape: Shape) = set(setOf(state), shape)
        operator fun set(@AttrRes attr: Int, shape: Shape) = set(DrawableState(attr), shape)
        operator fun set(@AttrRes attrs: IntArray, shape: Shape) =
            set(attrs.map { DrawableState(it) }.toSet(), shape)

        operator fun set(set: Set<DrawableState>, shape: Shape) = this.also {
            // if wildcard, then set default
            val notWildCards = set.filter { it.value != 0 }
            if (set.size != notWildCards.size) {
                // we have wildcards specified
                defaultShape = shape

                // if there are more states, just use them
                if (notWildCards.isNotEmpty()) {
                    sets[notWildCards.toSet()] = shape
                }
            } else {
                sets[set] = shape
            }
        }

        fun setPressed(shape: Shape) = this.also { it[DrawableState.pressed] = shape }
        fun setFocused(shape: Shape) = this.also { it[DrawableState.focused] = shape }
        fun setSelected(shape: Shape) = this.also { it[DrawableState.selected] = shape }
        fun setEnabled(shape: Shape) = this.also { it[DrawableState.enabled] = shape }
        fun setActivated(shape: Shape) = this.also { it[DrawableState.activated] = shape }
        fun setChecked(shape: Shape) = this.also { it[DrawableState.checked] = shape }
        fun setDefault(shape: Shape) = this.also { it[StateSet.WILD_CARD] = shape }

        private val sets = mutableMapOf<Set<DrawableState>, Shape>()
        private var defaultShape: Shape? = null

        fun build(): StateListDrawable {
            val drawable = StateListDrawable()
            // NB! copy the shape only when drawable is created, otherwise we would lose identity
            val shapes = sets.values.toSet().associateWith { it.copy().newDrawable() }
            sets.forEach { entry ->
                drawable.addState(
                    entry.key.map { it.value }.toIntArray(),
                    shapes[entry.value]
                )
            }
            defaultShape?.also { drawable.addState(StateSet.WILD_CARD, shapes[it]) }
            return drawable
        }
    }
}

fun Shape.newDrawable2() = ShapeDrawable2(this, Unit)

open class ShapeDrawable2<R : Any>(
    val shape: Shape,
    val ref: R
) : Drawable() {

    companion object {

//            operator fun invoke(
//                shape: Shape
//            ): ShapeDrawableNoRef = ShapeDrawable(shape, Unit)

//        operator fun <S : Shape, R : Any> invoke(ref: R, block: (R) -> S): ShapeDrawable<R> {
//            return ShapeDrawable(block(ref), ref)
//        }
    }

    private var stateful: Stateful<R>? = null

    override fun draw(canvas: Canvas) {
        shape.draw(canvas, bounds)
    }

    override fun getIntrinsicWidth(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.width?.resolve(0) ?: super.getIntrinsicWidth()
    }

    override fun getIntrinsicHeight(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.height?.resolve(0) ?: super.getIntrinsicHeight()
    }

    override fun getAlpha(): Int {
        return shape.alpha?.let { (it * 255).roundToInt() } ?: 255
    }

    override fun setAlpha(alpha: Int) {
        shape.alpha(alpha / 255F)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun getOutline(outline: Outline) {
        shape.outline(outline, bounds)
    }

    fun invalidate(block: (R) -> Unit) {
        block(ref)
        invalidateSelf()
    }

    // TODO: add a single state fun
    fun stateful(
        states: Set<DrawableState> = emptySet(),
        onStateChange: ShapeDrawable2<R>.(DrawableStateSet) -> Unit = {}
    ) = this.also {
        it.stateful = Stateful(states, onStateChange)
    }

    override fun isStateful(): Boolean {
        return stateful != null
    }

    override fun onStateChange(state: IntArray): Boolean {
        val stateful = this.stateful ?: return false
        val result = stateful.states.isEmpty() || kotlin.run {
            DrawableStateSet.contains(state, stateful.states) || DrawableStateSet.contains(
                previousState,
                stateful.states
            )
        }
        previousState = state.copyOf()
        if (result) {
            stateful.onStateChange.invoke(this, DrawableStateSet(state))
            invalidateSelf()
        }
        return result
    }

    private var previousState: IntArray = intArrayOf()

    private class Stateful<R : Any>(
        val states: Set<DrawableState>,
        val onStateChange: ShapeDrawable2<R>.(DrawableStateSet) -> Unit = {}
    )
}