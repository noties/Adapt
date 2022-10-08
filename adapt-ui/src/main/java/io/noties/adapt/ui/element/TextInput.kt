package io.noties.adapt.ui.element

import android.widget.EditText
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * EditText
 * @see android.view.inputmethod.EditorInfo for `inputType` arguments
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.TextInput(
    inputType: Int? = null
): ViewElement<EditText, LP> =
    ViewElement<EditText, LP> { context ->
        EditText(context).also { et ->
            inputType?.also {
                et.inputType = it
            }
        }
    }.also(elements::add)