package io.noties.adapt.ui.element

import android.widget.EditText
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.util.InputType

/**
 * @see EditText
 * @see InputType
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.TextInput(
    inputType: InputType? = null
): ViewElement<EditText, LP> = Element(ElementViewFactory.TextInput) { et ->
    inputType?.also { et.inputType = it.value }
}