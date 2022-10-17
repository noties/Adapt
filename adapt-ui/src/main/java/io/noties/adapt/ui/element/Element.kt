package io.noties.adapt.ui.element

import android.content.Context
import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * Generic element to wrap any [View] that does not specify any children.
 * For example `Element(::RecyclerView)` or `Element { context -> RecyclerView(context) }
 */
@Suppress("FunctionName")
fun <V : View, LP : LayoutParams> ViewFactory<LP>.Element(
    provider: (Context) -> V
): ViewElement<V, LP> = Element(provider) {}

@Suppress("FunctionName")
fun <V : View, LP : LayoutParams> ViewFactory<LP>.Element(
    provider: (Context) -> V,
    configurator: (V) -> Unit
): ViewElement<V, LP> = ViewElement<V, LP> {
    provider(it).also(configurator)
}.also(elements::add)