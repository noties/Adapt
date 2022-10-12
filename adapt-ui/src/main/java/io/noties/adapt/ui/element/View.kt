package io.noties.adapt.ui.element

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * @see View
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.View(): ViewElement<View, LP> {
    // not only return, but we also need to add it to internal collection
    return ViewElement<View, LP> {
        ElementViewFactory.View(it)
    }.also(elements::add)
}