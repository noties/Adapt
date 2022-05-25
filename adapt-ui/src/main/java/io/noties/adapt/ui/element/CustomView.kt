package io.noties.adapt.ui.element

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName", "unused")
fun <V : View, LP : LayoutParams> ViewFactory<LP>.CustomView(
    provider: (Context) -> V,
    block: V.() -> Unit = {}
): ViewElement<V, LP> {
    return ViewElement<V, LP>(provider)
        .onView(block)
        .also(elements::add)
}