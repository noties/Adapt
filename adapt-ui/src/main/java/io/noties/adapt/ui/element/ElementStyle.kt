package io.noties.adapt.ui.element

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.app.style.ViewStyles

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.style(
    style: ElementStyle<V, LP>
) = this.also {
    style.block.invoke(ViewFactoryConstants.Impl, it)
}

fun <V: View, LP: LayoutParams> ViewElement<V, LP>.style(
    builder: ViewStyles.() -> ElementStyle<V, LP>
) = style(builder(ViewStyles))

class ElementStyle<in V : View, in LP : LayoutParams> private constructor(
    val block: ViewFactoryConstants.(ViewElement<@UnsafeVariance V, @UnsafeVariance LP>) -> Unit
) {
    companion object {
        operator fun <V : View, LP : LayoutParams> invoke(
            block: ViewFactoryConstants.(ViewElement<V, LP>) -> Unit
        ) = ElementStyle(block)

        fun generic(
            block: ViewFactoryConstants.(ViewElement<View, LayoutParams>) -> Unit
        ) = ElementStyle(block)

        fun <V : View> view(
            block: ViewFactoryConstants.(ViewElement<V, LayoutParams>) -> Unit
        ) = ElementStyle(block)

        fun <LP : LayoutParams> layout(
            block: ViewFactoryConstants.(ViewElement<View, LP>) -> Unit
        ) = ElementStyle(block)

        fun <V : View, LP : LayoutParams> viewLayout(
            block: ViewFactoryConstants.(ViewElement<V, LP>) -> Unit
        ) = invoke(block)
    }
}