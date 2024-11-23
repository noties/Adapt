package io.noties.adapt.sample.util

import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtil {
    fun hide(view: View) {
        val manager = view.context.getSystemService(InputMethodManager::class.java) ?: return
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}