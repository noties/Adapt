package io.noties.adapt.ui.util

import android.view.inputmethod.EditorInfo

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
        val date: DateTime get() = DateTime(clearVariation or EditorInfo.TYPE_DATETIME_VARIATION_DATE)
        val time: DateTime get() = DateTime(clearVariation or EditorInfo.TYPE_DATETIME_VARIATION_TIME)
    }

    internal val clearVariation: Int get() = value and EditorInfo.TYPE_MASK_VARIATION.inv()

    override fun toString(): String {
        return "${javaClass.simpleName}(value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InputType) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }
}