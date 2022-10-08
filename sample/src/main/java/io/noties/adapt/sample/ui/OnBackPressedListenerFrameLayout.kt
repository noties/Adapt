package io.noties.adapt.sample.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout

class OnBackPressedListenerFrameLayout(
    context: Context,
    attributeSet: AttributeSet
) : FrameLayout(context, attributeSet) {

    fun onSoftBackPressed(): Boolean {
        val focus = findFocus()
        if (focus != null && focus != this) {
            requestFocus()
            return true
        }
        return false
    }

    private val activity: Activity?
        get() {
            var ctx = context
            while (ctx != null) {
                if (ctx is Activity) {
                    return ctx
                }
                ctx = if (ctx is ContextWrapper) {
                    ctx.baseContext
                } else {
                    null
                }
            }
            return null
        }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (gainFocus) {
            hideKeyboard(this)
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        val isBack = KeyEvent.KEYCODE_BACK == event?.keyCode
        if (isBack) {
            val focus = activity?.currentFocus
            if (focus != null) {
                requestFocus()
                return true
            }
        }
        return super.dispatchKeyEventPreIme(event)
    }

    private fun hideKeyboard(focus: View) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        imm.hideSoftInputFromWindow(focus.windowToken, 0)
    }
}