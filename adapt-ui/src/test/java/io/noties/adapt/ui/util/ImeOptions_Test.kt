package io.noties.adapt.ui.util

import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.view.inputmethod.EditorInfo.IME_ACTION_NONE
import android.view.inputmethod.EditorInfo.IME_ACTION_PREVIOUS
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.view.inputmethod.EditorInfo.IME_ACTION_SEND
import android.view.inputmethod.EditorInfo.IME_ACTION_UNSPECIFIED
import android.view.inputmethod.EditorInfo.IME_FLAG_FORCE_ASCII
import android.view.inputmethod.EditorInfo.IME_FLAG_NAVIGATE_NEXT
import android.view.inputmethod.EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_ENTER_ACTION
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_FULLSCREEN
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
import android.view.inputmethod.EditorInfo.IME_NULL
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ImeOptions_Test {

    @Test
    fun nonTerminating() {
        val inputs = listOf(
            ImeOptions.none to IME_NULL,
            ImeOptions.noPersonalizedLearning to IME_FLAG_NO_PERSONALIZED_LEARNING,
            ImeOptions.noFullScreen to IME_FLAG_NO_FULLSCREEN,
            ImeOptions.noExtractUi to IME_FLAG_NO_EXTRACT_UI,
            ImeOptions.noEnterAction to IME_FLAG_NO_ENTER_ACTION,
            ImeOptions.navigatePrevious to IME_FLAG_NAVIGATE_PREVIOUS,
            ImeOptions.navigateNext to IME_FLAG_NAVIGATE_NEXT,
            ImeOptions.forceAscii to IME_FLAG_FORCE_ASCII,
            ImeOptions.noPersonalizedLearning.noFullScreen to (IME_FLAG_NO_PERSONALIZED_LEARNING or IME_FLAG_NO_FULLSCREEN),
            ImeOptions.noExtractUi.noEnterAction to (IME_FLAG_NO_EXTRACT_UI or IME_FLAG_NO_ENTER_ACTION),
            ImeOptions.navigatePrevious.navigateNext to (IME_FLAG_NAVIGATE_PREVIOUS or IME_FLAG_NAVIGATE_NEXT),
            ImeOptions.forceAscii.noPersonalizedLearning to (IME_FLAG_FORCE_ASCII or IME_FLAG_NO_PERSONALIZED_LEARNING)
        )

        var i = 0

        for ((ime, expected) in inputs) {
            val s = "${i++} expected:${toImeOptionsString(expected)} ime:$ime"

            val (rawValue, editorAction) = ime
            assertEquals(s, rawValue, ime.rawValue)
            assertEquals(s, editorAction, ime.editorAction)

            assertEquals(s, expected, ime.rawValue)
            assertNull(s, ime.editorAction)
        }
    }

    @Test
    fun terminating() {
        val inputs = listOf(
            // action-none and action-unspecified does not register editor-action-listener
            ImeOptions.actionNone to IME_ACTION_NONE,
            ImeOptions.actionUnspecified to IME_ACTION_UNSPECIFIED
        )

        for ((ime, expected) in inputs) {
            val s = ime.toString()

            assertEquals(s, expected, ime.rawValue)
            assertNull(s, ime.editorAction)

            val (rawValue, editorAction) = ime
            assertEquals(s, expected, rawValue)
            assertNull(s, editorAction)
        }
    }

    @Test
    fun terminating_with_action() {
        val inputs = listOf(
            ImeOptions.actionGo() to IME_ACTION_GO,
            ImeOptions.actionGo {} to IME_ACTION_GO,
            ImeOptions.actionSearch() to IME_ACTION_SEARCH,
            ImeOptions.actionSearch {} to IME_ACTION_SEARCH,
            ImeOptions.actionSend() to IME_ACTION_SEND,
            ImeOptions.actionSend {} to IME_ACTION_SEND,
            ImeOptions.actionNext() to IME_ACTION_NEXT,
            ImeOptions.actionNext {} to IME_ACTION_NEXT,
            ImeOptions.actionDone() to IME_ACTION_DONE,
            ImeOptions.actionDone {} to IME_ACTION_DONE,
            ImeOptions.actionPrevious() to IME_ACTION_PREVIOUS,
            ImeOptions.actionPrevious {} to IME_ACTION_PREVIOUS,
        )

        for ((ime, expected) in inputs) {
            val s = "expected:${toImeOptionsString(expected)} ime:$ime"

            assertEquals(s, expected, ime.rawValue)
            assertNotNull(s, ime.editorAction)

            val (rawValue, editorAction) = ime
            assertEquals(s, expected, rawValue)
            assertNotNull(s, editorAction)
        }
    }

    @Test
    fun mix() {
        val inputs = listOf(
            ImeOptions.forceAscii.noPersonalizedLearning.actionGo() to (IME_FLAG_FORCE_ASCII or IME_FLAG_NO_PERSONALIZED_LEARNING or IME_ACTION_GO),
            ImeOptions.noExtractUi.noFullScreen.navigateNext.actionNext() to (IME_FLAG_NO_EXTRACT_UI or IME_FLAG_NO_FULLSCREEN or IME_FLAG_NAVIGATE_NEXT or IME_ACTION_NEXT)
        )

        for ((ime, expected) in inputs) {
            val s = "expected:${toImeOptionsString(expected)} ime:$ime"

            // mask with action
            assertEquals(s, expected, ime.rawValue)
            assertNotNull(s, ime.editorAction)

            val (rawValue, editorAction) = ime
            assertEquals(s, expected, rawValue)
            assertNotNull(s, editorAction)
        }
    }
}