package io.noties.adapt.ui.element

import android.view.View
import android.widget.LinearLayout
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName")
fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.Spacer(weight: Float = 1F): ViewElement<View, LP> {
    // not only return, but we also need to add it to internal collection
    return ViewElement<View, LP> {
        View(it)
    }.also {
        it.layoutBlocks.add {
            this.weight = weight
        }
    }.also(elements::add)
}