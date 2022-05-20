package io.noties.adapt.ui.element

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.View(): ViewElement<View, LP> {
    // not only return, but we also need to add it to internal collection
    return ViewElement<View, LP> {
        View(it)
    }.also(elements::add)
}