package io.noties.adapt.ui.element

import android.view.ViewGroup
import android.widget.LinearLayout
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren

// TODO: deal with this
@Suppress("FunctionName", "unused")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Button(
    action: () -> Unit,
    children: ViewFactory<LinearLayout.LayoutParams>.() -> Unit
): ViewElement<LinearLayout, LP> {
    // TODO: can we use first element?
    return ViewElement<LinearLayout, LP> {
        LinearLayout(it).also { ll ->
            ll.orientation = LinearLayout.HORIZONTAL
//            ll.gravity = gravity
            ViewFactory.addChildren(ll, children)
        }
    }.also(elements::add)
}