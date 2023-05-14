package io.noties.adapt.sample.samples.adaptui

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.annotation.AttrRes
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import java.util.Arrays

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
    /**
     * `!DrawableState.pressed` => not pressed
     */
    // TODO: this actually does not seem to be working now, we do not receive negative attributesø
//    operator fun not(): DrawableState = DrawableState(-value)
}

class DrawableStateSet(@AttrRes val state: IntArray) {
    val pressed: Boolean by lazy(LazyThreadSafetyMode.NONE) { state.contains(DrawableState.pressed.value) }
    val focused: Boolean by lazy(LazyThreadSafetyMode.NONE) { state.contains(DrawableState.focused.value) }
    val selected: Boolean by lazy(LazyThreadSafetyMode.NONE) { state.contains(DrawableState.selected.value) }
    val enabled: Boolean by lazy(LazyThreadSafetyMode.NONE) { state.contains(DrawableState.enabled.value) }
    val activated: Boolean by lazy(LazyThreadSafetyMode.NONE) { state.contains(DrawableState.activated.value) }
    val checked: Boolean by lazy(LazyThreadSafetyMode.NONE) { state.contains(DrawableState.checked.value) }

    fun matches(set: Set<DrawableState>): Boolean {
        return matches(state, set)
    }

    fun matches(@AttrRes state: IntArray): Boolean {
        return matches(this.state, state)
    }

    fun contains(state: DrawableState) = contains(state.value)

    fun contains(@AttrRes attr: Int): Boolean = state.contains(attr)

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

        fun matches(@AttrRes state: IntArray, set: Set<DrawableState>): Boolean {
            return set.all { state.contains(it.value) }
        }

        fun matches(@AttrRes state: IntArray, @AttrRes stateToMatch: IntArray): Boolean {
            return state.all { stateToMatch.contains(it) }
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

    constructor(state: DrawableState) : this(setOf(state))
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
            DrawableStateSet.matches(state, set) || DrawableStateSet.matches(previousState, set)
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