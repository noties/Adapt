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
    fun companion() {
        val inputs = listOf(
            ImeOptions.none to IME_NULL,
            ImeOptions.actionUnspecified to IME_ACTION_UNSPECIFIED,
            ImeOptions.actionNone to IME_ACTION_NONE,
            ImeOptions.actionGo to IME_ACTION_GO,
            ImeOptions.actionSearch to IME_ACTION_SEARCH,
            ImeOptions.actionSend to IME_ACTION_SEND,
            ImeOptions.actionNext to IME_ACTION_NEXT,
            ImeOptions.actionDone to IME_ACTION_DONE,
            ImeOptions.actionPrevious to IME_ACTION_PREVIOUS,
            ImeOptions.noPersonalizedLearning to IME_FLAG_NO_PERSONALIZED_LEARNING,
            ImeOptions.noFullScreen to IME_FLAG_NO_FULLSCREEN,
            ImeOptions.noExactUi to IME_FLAG_NO_EXTRACT_UI,
            ImeOptions.noEnterAction to IME_FLAG_NO_ENTER_ACTION,
            ImeOptions.navigatePrevious to IME_FLAG_NAVIGATE_PREVIOUS,
            ImeOptions.navigateNext to IME_FLAG_NAVIGATE_NEXT,
            ImeOptions.forceAscii to IME_FLAG_FORCE_ASCII,
        )

        for ((ime, expected) in inputs) {
            assertEquals(ime.toString(), expected, ime.value)
        }
    }

    @Test
    fun instance() {
        val inputs = listOf(
            ImeOptions.actionDone.actionGo.actionNone.actionUnspecified.actionSearch to IME_ACTION_SEARCH,
            ImeOptions.forceAscii.actionSend.actionGo.noExactUi to (IME_FLAG_FORCE_ASCII or IME_ACTION_GO or IME_FLAG_NO_EXTRACT_UI),
            ImeOptions.none.none to IME_NULL,
            ImeOptions.actionUnspecified.actionUnspecified to IME_ACTION_UNSPECIFIED,
            ImeOptions.actionNone.actionNone to IME_ACTION_NONE,
            ImeOptions.actionGo.actionGo to IME_ACTION_GO,
            ImeOptions.actionSearch.actionSearch to IME_ACTION_SEARCH,
            ImeOptions.actionSend.actionSend to IME_ACTION_SEND,
            ImeOptions.actionNext.actionNext to IME_ACTION_NEXT,
            ImeOptions.actionDone.actionDone to IME_ACTION_DONE,
            ImeOptions.actionPrevious.actionPrevious to IME_ACTION_PREVIOUS,
            ImeOptions.noPersonalizedLearning.noPersonalizedLearning to IME_FLAG_NO_PERSONALIZED_LEARNING,
            ImeOptions.noFullScreen.noFullScreen to IME_FLAG_NO_FULLSCREEN,
            ImeOptions.noExactUi.noExactUi to IME_FLAG_NO_EXTRACT_UI,
            ImeOptions.noEnterAction.noEnterAction to IME_FLAG_NO_ENTER_ACTION,
            ImeOptions.navigatePrevious.navigatePrevious to IME_FLAG_NAVIGATE_PREVIOUS,
            ImeOptions.navigateNext.navigateNext to IME_FLAG_NAVIGATE_NEXT,
            ImeOptions.forceAscii.forceAscii to IME_FLAG_FORCE_ASCII,
        )

        for ((ime, expected) in inputs) {
            assertEquals(ime.toString(), expected, ime.value)
        }
    }
}