package io.noties.adapt.ui.state

import androidx.annotation.AttrRes

interface ViewStateFactory {
    companion object {
        fun create(): ViewStateFactory = object : ViewStateFactoryRoot {}
    }

    // unique name to not have collisions with any other _builder_ (or any other functionality)
    val viewStateFactoryRawValues: Set<Int>

    val pressed get() = ViewState(viewStateFactoryRawValues + android.R.attr.state_pressed)
    val focused get() = ViewState(viewStateFactoryRawValues + android.R.attr.state_focused)
    val selected get() = ViewState(viewStateFactoryRawValues + android.R.attr.state_selected)
    val enabled get() = ViewState(viewStateFactoryRawValues + android.R.attr.state_enabled)
    val activated get() = ViewState(viewStateFactoryRawValues + android.R.attr.state_activated)
    val checked get() = ViewState(viewStateFactoryRawValues + android.R.attr.state_checked)

    // no need to use rawValues, default is always empty and cannot be modified
    val default get() = ViewState(emptySet())

    fun raw(@AttrRes vararg attrs: Int) = ViewState(viewStateFactoryRawValues + attrs.toSet())
}

interface ViewStateFactoryRoot : ViewStateFactory {
    override val viewStateFactoryRawValues: Set<Int>
        get() = emptySet()
}