package io.noties.adapt.ui.app

import android.content.Context

interface ContextHolder {
    val context: Context get() = App.context
}