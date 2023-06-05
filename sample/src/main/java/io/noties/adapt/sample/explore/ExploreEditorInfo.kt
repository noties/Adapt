package io.noties.adapt.sample.explore

import android.os.Build
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement

object ExploreEditorInfo {

    fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textInputType(
        inputType: ExploreEditorInfo.InputType
    ) = onView { it.inputType = inputType.value }

    fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textImeOptions(
        imeOptions: ExploreEditorInfo.ImeOptions
    ) = onView { it.imeOptions = imeOptions.value }

    open class InputType(val value: Int) {
        companion object {
            val none: InputType get() = InputType(EditorInfo.TYPE_NULL)
            val text: Text get() = Text()
            val number: Number get() = Number()
            val phone: InputType get() = InputType(EditorInfo.TYPE_CLASS_PHONE)
            val dateTime: DateTime get() = DateTime()
        }

        class Text(
            value: Int = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_NORMAL
        ) : InputType(value) {
            // Capping, one of:
            // - cap characters
            // - cap words
            // - cap sentences
            val capCharacters: Text get() = Text(value or EditorInfo.TYPE_TEXT_FLAG_CAP_CHARACTERS)
            val capWords: Text get() = Text(value or EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS)
            val capSentences: Text get() = Text(value or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES)

            // VARIATION:
            //  - normal (default)
            //  - uri
            //  - email
            //  - email subject
            //  - short message
            //  - long message
            //  - person name
            //  - postal address
            //  - password
            val uri: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_URI)
            val email: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            val emailSubject: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT)
            val shortMessage: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE)
            val longMessage: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE)
            val personName: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME)
            val postalAddress: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
            val password: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            val visiblePassword: Text get() = Text(clearVariation or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

            val autoCorrect: Text get() = Text(value or EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT)
            val autoComplete: Text get() = Text(value or EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE)
            val noSuggestions: Text get() = Text(value or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

            val multiline: Text get() = Text(value or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE)

            private val clearVariation: Int get() = value and EditorInfo.TYPE_MASK_VARIATION.inv()
        }

        class Number(
            value: Int = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_NORMAL
        ) : InputType(value) {
            val signed: Number get() = Number(value or EditorInfo.TYPE_NUMBER_FLAG_SIGNED)
            val decimal: Number get() = Number(value or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL)
            val password: Number get() = Number(value or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
        }

        class DateTime(
            value: Int = EditorInfo.TYPE_CLASS_DATETIME or EditorInfo.TYPE_DATETIME_VARIATION_NORMAL
        ) : InputType(value) {
            val date: DateTime get() = DateTime(value or EditorInfo.TYPE_DATETIME_VARIATION_DATE)
            val time: DateTime get() = DateTime(value or EditorInfo.TYPE_DATETIME_VARIATION_TIME)
        }
    }

    interface ImeOptionsBase {
        val value: Int

        val actionUnspecified: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_UNSPECIFIED)
        val actionNone: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_NONE)
        val actionGo: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_GO)
        val actionSearch: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_SEARCH)
        val actionSend: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_SEND)
        val actionNext: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_NEXT)
        val actionDone: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_DONE)
        val actionPrevious: ImeOptions get() = ImeOptions(value or EditorInfo.IME_ACTION_PREVIOUS)

        @get:RequiresApi(Build.VERSION_CODES.O)
        val noPersonalizedLearning: ImeOptions
            get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING)

        val noFullScreen: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_FULLSCREEN)
        val noExactUi: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_EXTRACT_UI)
        val noEnterAction: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_ENTER_ACTION)

        val navigatePrevious: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS)
        val navigateNext: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NAVIGATE_NEXT)

        val forceAscii: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_FORCE_ASCII)
    }

    class ImeOptions(override val value: Int) : ImeOptionsBase {
        companion object : ImeOptionsBase {
            override val value: Int = EditorInfo.IME_NULL
        }
    }

    fun inputType(inputType: InputType) {

    }

    fun imeOptions(imeOptions: ImeOptions) {

    }

    fun hey() {
        inputType(InputType.text.capCharacters.uri.noSuggestions)
        imeOptions(ImeOptions.actionDone.forceAscii.noExactUi)
    }
}