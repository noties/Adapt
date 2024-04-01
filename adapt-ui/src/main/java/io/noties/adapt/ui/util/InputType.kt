package io.noties.adapt.ui.util

import android.view.inputmethod.EditorInfo

open class InputType(val rawValue: Int) {

    @Deprecated("Use `rawValue`", ReplaceWith("rawValue"))
    val value: Int get() = rawValue

    companion object {
        val none: InputType get() = InputType(EditorInfo.TYPE_NULL)
        val text: Text get() = Text()
        val number: Number get() = Number()
        val phone: InputType get() = InputType(EditorInfo.TYPE_CLASS_PHONE)
        val dateTime: DateTime get() = DateTime()

        fun raw(value: Int): InputType = InputType(value)
    }

    class Text(
        rawValue: Int = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_NORMAL
    ) : InputType(rawValue) {
        // Capping, one of:
        // - cap characters
        // - cap words
        // - cap sentences
        val capCharacters: Text get() = Text(rawValue or EditorInfo.TYPE_TEXT_FLAG_CAP_CHARACTERS)
        val capWords: Text get() = Text(rawValue or EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS)
        val capSentences: Text get() = Text(rawValue or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES)

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

        val autoCorrect: Text get() = Text(rawValue or EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT)
        val autoComplete: Text get() = Text(rawValue or EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE)
        val noSuggestions: Text get() = Text(rawValue or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        val multiline: Text get() = Text(rawValue or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE)
    }

    class Number(
        rawValue: Int = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_NORMAL
    ) : InputType(rawValue) {
        val signed: Number get() = Number(rawValue or EditorInfo.TYPE_NUMBER_FLAG_SIGNED)
        val decimal: Number get() = Number(rawValue or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL)
        val password: Number get() = Number(rawValue or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    class DateTime(
        rawValue: Int = EditorInfo.TYPE_CLASS_DATETIME or EditorInfo.TYPE_DATETIME_VARIATION_NORMAL
    ) : InputType(rawValue) {
        val date: DateTime get() = DateTime(clearVariation or EditorInfo.TYPE_DATETIME_VARIATION_DATE)
        val time: DateTime get() = DateTime(clearVariation or EditorInfo.TYPE_DATETIME_VARIATION_TIME)
    }

    internal val clearVariation: Int get() = rawValue and EditorInfo.TYPE_MASK_VARIATION.inv()

    override fun toString(): String {
        return "${javaClass.simpleName}(value=$rawValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InputType) return false

        if (rawValue != other.rawValue) return false

        return true
    }

    override fun hashCode(): Int {
        return rawValue
    }
}

typealias InputTypeBuilder = InputType.Companion.() -> InputType