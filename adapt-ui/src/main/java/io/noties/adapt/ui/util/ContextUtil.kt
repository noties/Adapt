package io.noties.adapt.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

val Context.activity: Activity? get() {
    var current: Context? = this
    while (current != null) {
        if (current is Activity) return current
        if (current is ContextWrapper) {
            current = current.baseContext
        }
    }
    return null
}