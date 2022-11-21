package io.noties.adapt.ui.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import io.noties.adapt.ui.R

/**
 * @since $UNRELEASED;
 */
class TextWatcherHideIfEmpty internal constructor(private val textView: TextView) : TextWatcher {

    companion object {

        internal val id = R.id.adaptui_internal_text_watcher

        fun init(textView: TextView) {

            (textView.getTag(id) as? TextWatcher)?.also { textView.removeTextChangedListener(it) }

            val watcher = TextWatcherHideIfEmpty(textView)
            textView.addTextChangedListener(watcher)
            textView.setTag(id, watcher)
            // trigger initial state
            textView.text = textView.text
        }

        fun remove(textView: TextView) {
            (textView.getTag(id) as? TextWatcherHideIfEmpty)?.also {
                textView.removeTextChangedListener(it)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(s: Editable?) {
        val hide = s.isNullOrEmpty()
        textView.visibility = if (hide) View.GONE else View.VISIBLE
    }
}