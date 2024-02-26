package io.noties.adapt.ui.app.style

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.element.ElementStyle
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object ViewStyles


fun style(
    block: ViewFactoryConstants.(ViewElement<View, LayoutParams>) -> Unit
) = ViewStylesProperty(block)

fun <V : View> styleView(
    block: ViewFactoryConstants.(ViewElement<V, LayoutParams>) -> Unit
) = ViewStylesProperty(block)

fun <LP : LayoutParams> styleLayout(
    block: ViewFactoryConstants.(ViewElement<View, LP>) -> Unit
) = ViewStylesProperty(block)

fun <V : View, LP : LayoutParams> styleViewLayout(
    block: ViewFactoryConstants.(ViewElement<V, LP>) -> Unit
) = ViewStylesProperty(block)


class ViewStylesProperty<V : View, LP : LayoutParams>(
    private val block: ViewFactoryConstants.(ViewElement<V, LP>) -> Unit
) : ReadOnlyProperty<ViewStyles, ElementStyle<V, LP>> {
    override fun getValue(thisRef: ViewStyles, property: KProperty<*>): ElementStyle<V, LP> {
        return ElementStyle(block)
    }
}