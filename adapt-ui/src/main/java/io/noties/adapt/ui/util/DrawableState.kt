package io.noties.adapt.ui.util

import android.content.res.Resources
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import java.util.Arrays
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * `pressed` is not reported without a stateful drawable for the `android.R.attr.state_pressed`, as
 * a workaround a [io.noties.adapt.ui.shape.StatefulShape], [StateListDrawable] can be used with
 * pressed state defined. Or a stateful drawable
 * with this state defined, for example, `backgroundDefaultSelectable()` or `foregroundDefaultSelectable()`.
 * Additionally, a regular shape can be turned into stateful drawable with `shape.newDrawable().stateful()`
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onDrawableStateChange(
    action: (view: V, stateSet: DrawableStateSet) -> Unit
) = onView {
    it.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        var previous = it.drawableState
        override fun onPreDraw(): Boolean {
            if (!it.viewTreeObserver.isAlive) {
                it.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }

            val current = it.drawableState
            if (!Arrays.equals(previous, current)) {
                previous = current
                action(it, DrawableStateSet(current))
            }

            return true
        }
    })
}

@JvmInline
value class DrawableState(@AttrRes val value: Int) {
    companion object {
        val pressed = DrawableState(android.R.attr.state_pressed)
        val focused = DrawableState(android.R.attr.state_focused)
        val selected = DrawableState(android.R.attr.state_selected)
        val enabled = DrawableState(android.R.attr.state_enabled)
        val activated = DrawableState(android.R.attr.state_activated)
        val checked = DrawableState(android.R.attr.state_checked)

        fun attrResourceName(
            @AttrRes attr: Int,
            resources: Resources = Resources.getSystem()
        ): String = try {
            resources.getResourceName(attr)
        } catch (t: Throwable) {
            "$attr"
        }
    }

    operator fun plus(other: DrawableState): Set<DrawableState> {
        return setOf(this, other)
    }

    // https://stackoverflow.com/questions/15543186/how-do-i-create-colorstatelist-programmatically/58121371#58121371
    // It is possible to specify state by negating value.. for unfocused would be -attr.focused
    //  it it possible that this is a discarded impl, right now we do not receive negative values

    override fun toString(): String {
        val name = attrResourceName(value)
        return "DrawableState($name)"
    }
}

class DrawableStateSet(@AttrRes val state: IntArray) {
    val pressed by Prop(DrawableState.pressed)
    val focused by Prop(DrawableState.focused)
    val selected by Prop(DrawableState.selected)
    val enabled by Prop(DrawableState.enabled)
    val activated by Prop(DrawableState.activated)
    val checked by Prop(DrawableState.checked)

    fun containsAll(set: Set<DrawableState>) = containsAll(this.state, set)
    fun containsAny(set: Set<DrawableState>) = containsAny(this.state, set)
    fun contains(state: DrawableState) = contains(this.state, state)

    override fun toString(): String {
        val attrs = state.joinToString(", ") {
            DrawableState.attrResourceName(it)
        }
        return "DrawableStateSet([$attrs])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawableStateSet) return false

        if (!state.contentEquals(other.state)) return false

        return true
    }

    override fun hashCode(): Int {
        return state.contentHashCode()
    }

    companion object {

        fun contains(@AttrRes state: IntArray, drawableState: DrawableState): Boolean {
            return state.contains(drawableState.value)
        }

        fun containsAll(@AttrRes state: IntArray, set: Set<DrawableState>): Boolean {
            return set.all { state.contains(it.value) }
        }

        fun containsAny(@AttrRes state: IntArray, set: Set<DrawableState>): Boolean {
            return set.any { contains(state, it) }
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