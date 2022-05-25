package io.noties.adapt.ui.element

import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.GravityInt
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Text(
    text: String? = null
): ViewElement<TextView, LP> {
    // not only return, but we also need to add it to internal collection
    return ViewElement<TextView, LP> {
        TextView(it).also { tv -> tv.text = text }
    }.also(elements::add)
}

data class TextStyle(
    val size: Int,
    @ColorInt val color: Int,
    @GravityInt val gravity: Int,
    val font: Typeface?,
    val fontStyle: Int = Typeface.NORMAL
) {
    companion object {

        fun setSize(textView: TextView, size: Int) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        }

        fun setColor(textView: TextView, @ColorInt color: Int) {
            textView.setTextColor(color)
        }

        fun setGravity(textView: TextView, @GravityInt gravity: Int) {
            textView.gravity = gravity
        }

        fun setFont(textView: TextView, font: Typeface?, style: Int) {
            textView.setTypeface(font, style)
        }
    }
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textSize(
    size: Int,
): ViewElement<V, LP> = onView {
    TextStyle.setSize(this, size)
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textColor(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    TextStyle.setColor(this, color)
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textGravity(
    @GravityInt gravity: Int
): ViewElement<V, LP> = onView {
    TextStyle.setGravity(this, gravity)
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textFont(
    font: Typeface? = null,
    fontStyle: Int = Typeface.NORMAL
): ViewElement<V, LP> = onView {
    TextStyle.setFont(this, font, fontStyle)
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textAllCaps(): ViewElement<V, LP> =
    onView {
        isAllCaps = true
    }

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textStyle(
    style: TextStyle
): ViewElement<V, LP> = onView {
    TextStyle.setSize(this, style.size)
    TextStyle.setColor(this, style.color)
    TextStyle.setGravity(this, style.gravity)
    TextStyle.setFont(this, style.font, style.fontStyle)
}