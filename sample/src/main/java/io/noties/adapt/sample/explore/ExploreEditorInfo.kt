package io.noties.adapt.sample.explore

import android.view.inputmethod.EditorInfo

object ExploreEditorInfo {

    open class InputType(val value: Int) {
        companion object {
            val none = InputType(EditorInfo.TYPE_NULL)
            val text = Text()
            val number = Number()
            val phone = InputType(EditorInfo.TYPE_CLASS_PHONE)
            val dateTime = DateTime()
        }

        class Text(
            value: Int = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_NORMAL
        ) : InputType(value) {
            // Capping, one of:
            // - cap characters
            // - cap words
            // - cap sentences
            val capCharacters = Text(value or EditorInfo.TYPE_TEXT_FLAG_CAP_CHARACTERS)
            val capWords = Text(value or EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS)
            val capSentences = Text(value or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES)

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
            val uri = Text(value or EditorInfo.TYPE_TEXT_VARIATION_URI)
            val email = Text(value or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            val emailSubject = Text(value or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT)
            val shortMessage = Text(value or EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE)
            val longMessage = Text(value or EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE)
            val personName = Text(value or EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME)
            val postalAddress = Text(value or EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
            val password = Text(value or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            val visiblePassword = Text(value or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

            val autoCorrect = Text(value or EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT)
            val autoComplete = Text(value or EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE)
            val noSuggestions = Text(value or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

            val multiline = Text(value or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE)
        }

        class Number(
            value: Int = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_NORMAL
        ) : InputType(value) {
            val signed = Number(value or EditorInfo.TYPE_NUMBER_FLAG_SIGNED)
            val decimal = Number(value or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL)
            val password = Number(value or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
        }

        class DateTime(
            value: Int = EditorInfo.TYPE_CLASS_DATETIME or EditorInfo.TYPE_DATETIME_VARIATION_NORMAL
        ) : InputType(value) {
            val date = DateTime(value or EditorInfo.TYPE_DATETIME_VARIATION_DATE)
            val time = DateTime(value or EditorInfo.TYPE_DATETIME_VARIATION_TIME)
        }
    }

    fun inputType(inputType: InputType) {

    }

    fun hey() {
        inputType(InputType.text.capCharacters.uri.noSuggestions)
    }
}