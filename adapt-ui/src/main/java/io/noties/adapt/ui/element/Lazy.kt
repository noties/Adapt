package io.noties.adapt.ui.element

import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.widget.LazyView

// the same LP are used -> from parent
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Lazy(
    children: ViewFactory<LP>.() -> Unit
) = Element {
    @Suppress("UNCHECKED_CAST")
    (LazyView(
        it,
        children as ViewFactory<LayoutParams>.() -> Unit
    ))
}

fun <LP : LayoutParams> ViewElement<LazyView, LP>.lazyInject(
): ViewElement<LazyView, LP> = onView {
    it.inject()
}