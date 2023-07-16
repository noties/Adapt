package io.noties.adapt.sample.ui

import android.animation.LayoutTransition
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import io.noties.adapt.sample.R

class SearchBar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var onTextChangedListener: (String?) -> Unit = {}

    private var focus: View
    private var input: Input
    private var clear: View
    private var cancel: View

    init {
        orientation = HORIZONTAL

        inflate(context, R.layout.widget_search_bar, this)

        focus = findViewById(R.id.focus)
        input = findViewById(R.id.input)
        clear = findViewById(R.id.clear)
        cancel = findViewById(R.id.cancel)

        LayoutTransition()
            .also {
                layoutTransition = it

                val inputGroup: ViewGroup = findViewById(R.id.input_group)
                inputGroup.layoutTransition = it
            }

        // subscribe to text change notification
        input.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                onTextChanged(s.toString())
            }
        })

        fun looseFocus() {
            Keyboard.hide(input)
            focus.requestFocus()
        }

        // focus listener to show/hide keyboard (so, when there is focus keyboard is shown)
        input.setOnFocusChangeListener { v, hasFocus ->
            cancel.isVisible = hasFocus || input.text.isNotEmpty()
            if (hasFocus) {
                // ensure keyboard is showing
                Keyboard.show(v)
            } else {
                looseFocus()
            }
        }

        input.onBackPressedListener = ::looseFocus
        input.setOnEditorActionListener { _, _, event ->
            if (event == null || KeyEvent.ACTION_UP == event.action) {
                looseFocus()
            }
            true
        }

        clear.setOnClickListener {
            // clear text
            input.setText("")

            // ensure keyboard is shown
            input.requestFocus()
            Keyboard.show(input)
        }

        cancel.setOnClickListener {
            input.setText("")
            looseFocus()
        }
    }

    fun onTextChanged(text: String) {
        val isEmpty = text.isEmpty()

        clear.isVisible = !isEmpty
        cancel.isVisible = !isEmpty || input.hasFocus()

        onTextChangedListener(if (isEmpty) null else text)
    }


    class Input(context: Context, attrs: AttributeSet) : EditText(context, attrs) {
        var onBackPressedListener: () -> Unit = {}

        override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
            val listener = onBackPressedListener
            val isBack = KeyEvent.KEYCODE_BACK == keyCode
            // event can be null, then consider this it as UP
            val isUp = (KeyEvent.ACTION_UP == event?.action)
            if (isBack && isUp) {
                listener()
                return true
            }
            return super.onKeyPreIme(keyCode, event)
        }
    }

    abstract class TextWatcherAdapter : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable) = Unit
    }

    private var View.isVisible: Boolean
        get() = View.VISIBLE == visibility
        set(value) {
            visibility = if (value) View.VISIBLE else View.GONE
        }

    object Keyboard {

        fun show(view: View) {
            imm(view)?.also {
                it.showSoftInput(view, 0)
            }
        }

        fun hide(view: View) {
            imm(view)?.also {
                it.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        private fun imm(view: View): InputMethodManager? =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    }
}