package io.noties.adapt.ui.state

import android.content.res.Resources
import androidx.annotation.AttrRes
import io.noties.adapt.ui.app.App

/**
 * Somehow in Android it is called `drawable state`, which gives it little visibility
 * and limits its usage to drawables only
 */
@JvmInline
value class ViewState(val rawValues: Set<Int>) : ViewStateFactory {
    constructor(attrs: IntArray) : this(attrs.toSet())

    companion object : ViewStateFactoryRoot {
        fun create(block: ViewStateFactory.() -> ViewState): ViewState {
            val builder = ViewStateFactory.create()
            return block(builder)
        }

        fun attrName(
            @AttrRes attr: Int,
            resources: Resources = App.context.resources
        ): String = try {
            resources.getResourceName(attr)
        } catch (t: Throwable) {
            "$attr"
        }
    }

    val isPressed: Boolean get() = contains(android.R.attr.state_pressed)
    val isFocused: Boolean get() = contains(android.R.attr.state_focused)
    val isSelected: Boolean get() = contains(android.R.attr.state_selected)
    val isEnabled: Boolean get() = contains(android.R.attr.state_enabled)
    val isActivated: Boolean get() = contains(android.R.attr.state_activated)
    val isChecked: Boolean get() = contains(android.R.attr.state_checked)

    override val viewStateFactoryRawValues: Set<Int>
        get() = rawValues

    override fun toString(): String {
        val states = rawValues.joinToString { attrName(it) }
        return "ViewState($states)"
    }

    /**
     * ```kotlin
     * val state: ViewState = view.viewState
     * if (state.contains(R.attr.my_attr, R.attr.some_other_attr)) {}
     * if (state.contains { pressed.activated }) {}
     * ```
     */
    fun contains(@AttrRes vararg rawValues: Int): Boolean {
        return this.rawValues.containsAll(rawValues.toList())
    }

    // optimization? to not allocate array when called vararg version
    fun contains(@AttrRes rawValue: Int): Boolean {
        return rawValues.contains(rawValue)
    }

    fun contains(block: ViewStateFactory.() -> Unit): Boolean {
        val builder = ViewStateFactory.create()
        block(builder)
        return contains(builder.viewStateFactoryRawValues)
    }

    fun contains(collection: Collection<Int>): Boolean {
        return rawValues.containsAll(collection)
    }

    fun contains(viewState: ViewState): Boolean {
        return rawValues.containsAll(viewState.rawValues)
    }
}

typealias ViewStateBuilder = ViewStateFactory.() -> ViewState