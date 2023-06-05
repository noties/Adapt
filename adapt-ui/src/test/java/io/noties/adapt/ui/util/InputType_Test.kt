package io.noties.adapt.ui.util

import android.view.inputmethod.EditorInfo.TYPE_CLASS_DATETIME
import android.view.inputmethod.EditorInfo.TYPE_CLASS_NUMBER
import android.view.inputmethod.EditorInfo.TYPE_CLASS_PHONE
import android.view.inputmethod.EditorInfo.TYPE_CLASS_TEXT
import android.view.inputmethod.EditorInfo.TYPE_DATETIME_VARIATION_DATE
import android.view.inputmethod.EditorInfo.TYPE_DATETIME_VARIATION_NORMAL
import android.view.inputmethod.EditorInfo.TYPE_DATETIME_VARIATION_TIME
import android.view.inputmethod.EditorInfo.TYPE_NULL
import android.view.inputmethod.EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
import android.view.inputmethod.EditorInfo.TYPE_NUMBER_FLAG_SIGNED
import android.view.inputmethod.EditorInfo.TYPE_NUMBER_VARIATION_NORMAL
import android.view.inputmethod.EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
import android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
import android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT
import android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_CAP_CHARACTERS
import android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES
import android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS
import android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
import android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_NORMAL
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_URI
import android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class InputType_Test {

    @Test
    fun none() {
        assertType(TYPE_NULL, InputType.none)
    }

    @Test
    fun text() {
        val base = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL
        val inputs = listOf(
            InputType.text to base,
            InputType.text.capCharacters to (base or TYPE_TEXT_FLAG_CAP_CHARACTERS),
            InputType.text.capWords to (base or TYPE_TEXT_FLAG_CAP_WORDS),
            InputType.text.capSentences to (base or TYPE_TEXT_FLAG_CAP_SENTENCES),
            InputType.text.uri to (base or TYPE_TEXT_VARIATION_URI),
            InputType.text.email to (base or TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
            InputType.text.emailSubject to (base or TYPE_TEXT_VARIATION_EMAIL_SUBJECT),
            InputType.text.shortMessage to (base or TYPE_TEXT_VARIATION_SHORT_MESSAGE),
            InputType.text.longMessage to (base or TYPE_TEXT_VARIATION_LONG_MESSAGE),
            InputType.text.personName to (base or TYPE_TEXT_VARIATION_PERSON_NAME),
            InputType.text.postalAddress to (base or TYPE_TEXT_VARIATION_POSTAL_ADDRESS),
            InputType.text.password to (base or TYPE_TEXT_VARIATION_PASSWORD),
            InputType.text.visiblePassword to (base or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD),
            InputType.text.autoComplete to (base or TYPE_TEXT_FLAG_AUTO_COMPLETE),
            InputType.text.autoCorrect to (base or TYPE_TEXT_FLAG_AUTO_CORRECT),
            InputType.text.noSuggestions to (base or TYPE_TEXT_FLAG_NO_SUGGESTIONS),
            InputType.text.multiline to (base or TYPE_TEXT_FLAG_MULTI_LINE),
            InputType.text.capCharacters.uri.autoComplete.multiline to (base or TYPE_TEXT_FLAG_CAP_CHARACTERS or TYPE_TEXT_VARIATION_URI or TYPE_TEXT_FLAG_AUTO_COMPLETE or TYPE_TEXT_FLAG_MULTI_LINE)
        )

        for ((type, expected) in inputs) {
            assertType(expected, type)
        }
    }

    @Test
    fun `text - variation cleared`() {
        val base = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL
        val inputs = listOf(
            (base or TYPE_TEXT_VARIATION_URI) to InputType.text.uri,
            (base or TYPE_TEXT_VARIATION_URI) to InputType.text.uri.uri.uri.uri.uri.uri,
            (base or TYPE_TEXT_VARIATION_URI) to InputType.text.email.uri,
            (base or TYPE_TEXT_VARIATION_URI) to InputType.text.email.emailSubject.shortMessage.longMessage.personName.postalAddress.password.visiblePassword.uri,
            (base or TYPE_TEXT_VARIATION_URI or TYPE_TEXT_FLAG_AUTO_CORRECT) to InputType.text.autoCorrect.email.emailSubject.shortMessage.longMessage.personName.postalAddress.password.visiblePassword.uri,
        )
        for ((expected, type) in inputs) {
            assertType(expected, type)
        }
    }

    @Test
    fun number() {
        val base = TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_NORMAL
        val inputs = listOf(
            InputType.number to base,
            InputType.number.signed to (base or TYPE_NUMBER_FLAG_SIGNED),
            InputType.number.decimal to (base or TYPE_NUMBER_FLAG_DECIMAL),
            InputType.number.password to (base or TYPE_NUMBER_VARIATION_PASSWORD),
            InputType.number.signed.password to (base or TYPE_NUMBER_VARIATION_PASSWORD or TYPE_NUMBER_FLAG_SIGNED),
            InputType.number.password.decimal to (base or TYPE_NUMBER_VARIATION_PASSWORD or TYPE_NUMBER_FLAG_DECIMAL),
        )
        for ((type, expected) in inputs) {
            assertType(expected, type)
        }
    }

    @Test
    fun phone() {
        assertType(TYPE_CLASS_PHONE, InputType.phone)
    }

    @Test
    fun dateTime() {
        val base = TYPE_CLASS_DATETIME or TYPE_DATETIME_VARIATION_NORMAL
        val inputs = listOf(
            InputType.dateTime to base,
            InputType.dateTime.date to (base or TYPE_DATETIME_VARIATION_DATE),
            InputType.dateTime.time to (base or TYPE_DATETIME_VARIATION_TIME),
            InputType.dateTime.time.time.time to (base or TYPE_DATETIME_VARIATION_TIME),
            InputType.dateTime.time.time.time.date to (base or TYPE_DATETIME_VARIATION_DATE),
        )

        for ((type, expected) in inputs) {
            assertType(expected, type)
        }
    }

    private fun assertType(expected: Int, type: InputType) {
        assertEquals(type.toString(), expected, type.value)
    }
}