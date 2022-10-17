package io.noties.adapt.ui.element

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * @see View
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.View(): ViewElement<View, LP> =
    Element(ElementViewFactory.View)