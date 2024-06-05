package io.noties.adapt.ui.app.style

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.element.ElementStyle

object ViewStyles {
    fun style(
        block: ViewFactoryConstants.(ViewElement<View, LayoutParams>) -> Unit
    ) = ElementStyle.generic(block)

    fun <V : View> styleView(
        block: ViewFactoryConstants.(ViewElement<V, LayoutParams>) -> Unit
    ) = ElementStyle.view(block)

    fun <LP : LayoutParams> styleLayout(
        block: ViewFactoryConstants.(ViewElement<View, LP>) -> Unit
    ) = ElementStyle.layout(block)

    fun <V : View, LP : LayoutParams> styleViewLayout(
        block: ViewFactoryConstants.(ViewElement<V, LP>) -> Unit
    ) = ElementStyle.viewLayout(block)
}