package io.noties.adapt.ui.util

import android.os.Build
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi

abstract class ImeOptionsBase(val value: Int) {

    val none: ImeOptions get() = ImeOptions(EditorInfo.IME_NULL)

    val actionUnspecified: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_UNSPECIFIED)
    val actionNone: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_NONE)
    val actionGo: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_GO)
    val actionSearch: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_SEARCH)
    val actionSend: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_SEND)
    val actionNext: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_NEXT)
    val actionDone: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_DONE)
    val actionPrevious: ImeOptions get() = ImeOptions(clearActionValue or EditorInfo.IME_ACTION_PREVIOUS)

    @get:RequiresApi(Build.VERSION_CODES.O)
    val noPersonalizedLearning: ImeOptions
        get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING)

    val noFullScreen: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_FULLSCREEN)
    val noExactUi: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_EXTRACT_UI)
    val noEnterAction: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NO_ENTER_ACTION)

    val navigatePrevious: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS)
    val navigateNext: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_NAVIGATE_NEXT)

    val forceAscii: ImeOptions get() = ImeOptions(value or EditorInfo.IME_FLAG_FORCE_ASCII)

    private val clearActionValue: Int get() = value and EditorInfo.IME_MASK_ACTION.inv()

    override fun toString(): String {
        return "ImeOptions(value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImeOptions) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }
}

class ImeOptions(value: Int) : ImeOptionsBase(value) {
    companion object : ImeOptionsBase(EditorInfo.IME_NULL)
}