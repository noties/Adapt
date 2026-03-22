package io.noties.adapt.ui.util

import android.os.Build
import android.view.KeyEvent
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
import android.view.inputmethod.EditorInfo.IME_MASK_ACTION
import android.view.inputmethod.EditorInfo.IME_NULL
import android.widget.TextView
import androidx.annotation.RequiresApi

open class ImeOptions(
    open val rawValue: Int,
    open val editorAction: TextView.OnEditorActionListener? = null
) {
    operator fun component1(): Int = rawValue
    operator fun component2(): TextView.OnEditorActionListener? = editorAction

    override fun toString(): String {
        val rawValue = toImeOptionsString(rawValue)
        val editorValue = editorAction
            ?.let { ", <editorAction>" } ?: ""
        return "ImeOptions($rawValue$editorValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImeOptions) return false

        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        return rawValue
    }

    companion object : ContinueImeOptions

    interface TerminatingImeOptions {
        val rawValue: Int

        val none: ImeOptions get() = ImeOptions(IME_NULL)

        // typealias cannot be local and requires an import, thus `(() -> Unit)?`

        fun actionGo(editorAction: (() -> Unit)? = null) = action(IME_ACTION_GO, editorAction)

        fun actionSearch(editorAction: (() -> Unit)? = null) =
            action(IME_ACTION_SEARCH, editorAction)

        fun actionSend(editorAction: (() -> Unit)? = null) = action(IME_ACTION_SEND, editorAction)

        fun actionNext(editorAction: (() -> Unit)? = null) = action(IME_ACTION_NEXT, editorAction)

        fun actionDone(editorAction: (() -> Unit)? = null) = action(IME_ACTION_DONE, editorAction)

        fun actionPrevious(editorAction: (() -> Unit)? = null) =
            action(IME_ACTION_PREVIOUS, editorAction)

        // `there is no available action` according to the documentation, no need
        //  to register an editor-listener
        val actionNone: ImeOptions get() = ImeOptions(rawValueWithAction(IME_ACTION_NONE))

        // does not indicate any specific action and lets IME come up with own
        //  should not register listener
        val actionUnspecified: ImeOptions
            get() = ImeOptions(
                rawValueWithAction(
                    IME_ACTION_UNSPECIFIED
                )
            )

        // Allows specifying raw Ime flags and action
        fun raw(rawValue: Int, editorAction: TextView.OnEditorActionListener? = null) =
            ImeOptions(rawValue, editorAction)

        // clear action bits from current `rawValue` and merge it with requested action
        private fun rawValueWithAction(action: Int): Int =
            (rawValue and IME_MASK_ACTION.inv()) or action

        // this way editor-action is also added when action is specified
        private fun action(action: Int, editorAction: (() -> Unit)?) = ImeOptions(
            // this value must be merged with represent the whole value
            rawValueWithAction(action),
            // action should only receive ACTION_ID (cleared of all other flags)
            //  this must be exact value compared with `==`
            ActionListener(
                action and IME_MASK_ACTION,
                editorAction
            )
        )
    }

    internal interface StatelessTerminatingImeOptions : TerminatingImeOptions {
        override val rawValue: Int get() = 0
    }

    internal interface ContinueImeOptions : StatelessTerminatingImeOptions {
        val noFullScreen get() = ContinueImeOptionsImpl(rawValue or IME_FLAG_NO_FULLSCREEN)
        val noExtractUi get() = ContinueImeOptionsImpl(rawValue or IME_FLAG_NO_EXTRACT_UI)
        val noEnterAction get() = ContinueImeOptionsImpl(rawValue or IME_FLAG_NO_ENTER_ACTION)
        val navigatePrevious get() = ContinueImeOptionsImpl(rawValue or IME_FLAG_NAVIGATE_PREVIOUS)
        val navigateNext get() = ContinueImeOptionsImpl(rawValue or IME_FLAG_NAVIGATE_NEXT)
        val forceAscii get() = ContinueImeOptionsImpl(rawValue or IME_FLAG_FORCE_ASCII)

        val noPersonalizedLearning
            @RequiresApi(Build.VERSION_CODES.O)
            get() = ContinueImeOptionsImpl(rawValue or IME_FLAG_NO_PERSONALIZED_LEARNING)
    }

    // the options that could be configured further until terminating option
    //  NB! must not specify editor-action (which can be specified only by terminating
    //  actions)
    class ContinueImeOptionsImpl(override val rawValue: Int) : ImeOptions(rawValue), ContinueImeOptions

    private class ActionListener(
        val actionId: Int,
        val action: (() -> Unit)?
    ) : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            // check the supplied action
            return if (actionId == this.actionId &&
                (event == null || event.action == KeyEvent.ACTION_UP)
            ) {
                action?.invoke()
                true
            } else {
                false
            }
        }
    }


}

typealias ImeOptionsBuilder = ImeOptions.Companion.() -> ImeOptions

private val IME_FLAG_NO_PERSONALIZED_LEARNING =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        android.view.inputmethod.EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
    } else {
        // taken from Android source code, fallback
        0x1000000
    }

internal fun toImeOptionsString(rawValue: Int): String {
    if (rawValue == IME_NULL) return "NULL"
    val action = (rawValue and IME_MASK_ACTION)
        .let {
            when (it) {
                IME_ACTION_UNSPECIFIED -> "ACTION_UNSPECIFIED"
                IME_ACTION_GO -> "ACTION_GO"
                IME_ACTION_SEARCH -> "ACTION_SEARCH"
                IME_ACTION_SEND -> "ACTION_SEND"
                IME_ACTION_NEXT -> "ACTION_NEXT"
                IME_ACTION_DONE -> "ACTION_DONE"
                IME_ACTION_PREVIOUS -> "ACTION_PREVIOUS"
                IME_ACTION_NONE -> "ACTION_NONE"
                else -> "$it"
            }
        }
    val flags = listOf(
        "FLAG_NO_FULLSCREEN" to IME_FLAG_NO_FULLSCREEN,
        "FLAG_NO_EXTRACT_UI" to IME_FLAG_NO_EXTRACT_UI,
        "FLAG_NO_ENTER_ACTION" to IME_FLAG_NO_ENTER_ACTION,
        "FLAG_NAVIGATE_PREVIOUS" to IME_FLAG_NAVIGATE_PREVIOUS,
        "FLAG_NAVIGATE_NEXT" to IME_FLAG_NAVIGATE_NEXT,
        "FLAG_FORCE_ASCII" to IME_FLAG_FORCE_ASCII,
        "FLAG_NO_PERSONALIZED_LEARNING" to IME_FLAG_NO_PERSONALIZED_LEARNING,
    )
        .filter {
            (rawValue or it.second) == it.second
        }
        .joinToString(", ") { it.first }
        .takeIf { it.isNotEmpty() }
        ?.let { "[$it]" }
    return listOfNotNull(action, flags)
        .joinToString(":")
}