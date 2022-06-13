package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams

class ViewFactory<out LP : LayoutParams>(val context: Context) {

    @Suppress("PropertyName", "unused")
    val FILL: Int
        get() {
            return LayoutParams.MATCH_PARENT
        }

    @Suppress("PropertyName", "unused")
    val WRAP: Int
        get() {
            return LayoutParams.WRAP_CONTENT
        }

    var elements: MutableList<ViewElement<out View, *>> = mutableListOf()

    // empty companion object to be used in extensions
    companion object
}

// `*` would match all
// `ViewGroup.LayoutParams` would match ONLY `ViewGroup.LayoutParams`, not type children
typealias AnyViewFactory = ViewFactory<*>