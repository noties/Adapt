package io.noties.adapt.sample.explore

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import io.noties.adapt.sample.explore.ExploreOnFocusChangeListener.onFocusChanged
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.onClick

object ExploreAutofill {

    @RequiresApi(Build.VERSION_CODES.O)
    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.autofillEnabled(
        enabled: Boolean
    ) = onView {
        // by default apply to children also, if it is a view group
        applyAutofill(it, enabled, it is ViewGroup)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.autofillEnabled(
        enabled: Boolean,
        applyToChildren: Boolean = enabled
    ) = onView {
        applyAutofill(it, enabled, applyToChildren)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmInline
    value class AutofillHint(val rawValue: String) {
        companion object {
            val email = AutofillHint(View.AUTOFILL_HINT_EMAIL_ADDRESS)
            val name = AutofillHint(View.AUTOFILL_HINT_NAME)
            val username = AutofillHint(View.AUTOFILL_HINT_USERNAME)
            val password = AutofillHint(View.AUTOFILL_HINT_PASSWORD)
            val phone = AutofillHint(View.AUTOFILL_HINT_PHONE)
            val address = AutofillHint(View.AUTOFILL_HINT_POSTAL_ADDRESS)
            val postalCode = AutofillHint(View.AUTOFILL_HINT_POSTAL_CODE)
            val creditCardNumber = AutofillHint(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER)
            val creditCardSecurityCode = AutofillHint(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.autofillHint(
        hint: AutofillHint?
    ) = onView {
        val hints = hint?.rawValue?.let { value -> arrayOf(value) } ?: emptyArray()
        it.setAutofillHints(*hints)
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.autofillRequestOnFocus() =
//        onFocusChanged { view, hasFocus ->
//            if (hasFocus) {
//                val manager = view.context.getSystemService(AutofillManager::class.java)
//                manager?.requestAutofill(view)
//            }
//        }

    // TODO: rename to process autofil on focus and cancel autofill when has no focus
    // there is little sense to do it for other views
    @RequiresApi(Build.VERSION_CODES.O)
    fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.autofillRequestOnFocusWhenEmpty() =
        onFocusChanged { view, hasFocus ->
            val manager = view.context.getSystemService(AutofillManager::class.java)
            if (hasFocus && view.length() == 0) {
                manager?.requestAutofill(view)
                manager?.notifyViewEntered(view)
            } else {
                manager?.notifyViewExited(view)
            }
        }

    // compatibility aware onClick, which just click on not supported versions
    // hm, doesn't sounds like a very wide-spread case, remove it
    fun <V: View, LP: LayoutParams> ViewElement<V, LP>.autofillCommitOnClick(onClick: () -> Unit) = this
        .also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.onClick {
                    onClick()

                    val manager = it.view.context.getSystemService(AutofillManager::class.java)
                    manager?.commit()
                }
            } else {
                it.onClick(onClick)
            }
        }

    // TODO: if a view-group is marked as important... why even? why view-group can be important?
    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyAutofill(view: View, enabled: Boolean, applyToChildren: Boolean) {
        val value = if (enabled) {
            if (applyToChildren) View.IMPORTANT_FOR_AUTOFILL_YES else View.IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS
        } else {
            if (applyToChildren) View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS else View.IMPORTANT_FOR_AUTOFILL_NO
        }
        view.importantForAutofill = value
    }
}