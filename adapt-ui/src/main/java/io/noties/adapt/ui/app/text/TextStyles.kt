package io.noties.adapt.ui.app.text

import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.element.ElementStyle
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object TextStyles


fun textStyle(
    block: ViewFactoryConstants.(ViewElement<TextView, LayoutParams>) -> Unit
) = TextStylesProperty(block)


class TextStylesProperty(
    private val block: ViewFactoryConstants.(ViewElement<TextView, LayoutParams>) -> Unit
): ReadOnlyProperty<TextStyles, ElementStyle<TextView, LayoutParams>> {
    override fun getValue(
        thisRef: TextStyles,
        property: KProperty<*>
    ): ElementStyle<TextView, LayoutParams> {
        return ElementStyle(block)
    }
}

