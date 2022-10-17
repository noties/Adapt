package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup

typealias LayoutParams = ViewGroup.LayoutParams

class ViewFactory<out LP : LayoutParams>(val context: Context) {

    @Suppress("PropertyName")
    val FILL: Int
        get() {
            return LayoutParams.MATCH_PARENT
        }

    @Suppress("PropertyName")
    val WRAP: Int
        get() {
            return LayoutParams.WRAP_CONTENT
        }

    var elements: MutableList<ViewElement<out View, *>> = mutableListOf()

    // empty companion object to be used in extensions
    companion object
}