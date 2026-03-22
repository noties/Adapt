package io.noties.adapt.ui.app.string

import androidx.annotation.StringRes
import io.noties.adapt.ui.app.App

interface Strings {
    companion object : Strings

    fun res(@StringRes id: Int): String = App.context.getString(id)
}

typealias StringsBuilder = Strings.() -> String