package io.noties.adapt.ui.element

import android.view.View
import android.widget.LinearLayout
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * A special element that can be used inside [LinearLayout] (VStack and HStack)
 * that has default layout weight of 1
 */
@Suppress("FunctionName")
fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.Spacer(
    weight: Float = 1F
): ViewElement<View, LP> = Element(ElementViewFactory.Spacer) {
    it.layoutParams = LinearLayout.LayoutParams(0, 0, weight)
}