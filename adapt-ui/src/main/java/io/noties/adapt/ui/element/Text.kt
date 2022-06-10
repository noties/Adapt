package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.GravityInt
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.util.TextWatcherHideIfEmpty

@Suppress("FunctionName")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Text(
    text: CharSequence? = null
): ViewElement<TextView, LP> {
    // not only return, but we also need to add it to internal collection
    return ViewElement<TextView, LP> {
        TextView(it).also { tv -> tv.text = text }
    }.also(elements::add)
}

data class TextStyle(
    val size: Int,
    val color: ColorStateList,
    @GravityInt val gravity: Int,
    val font: Typeface?,
    val fontStyle: Int = Typeface.NORMAL
) {
    companion object {

        fun setSize(textView: TextView, size: Int) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        }

        fun setColor(textView: TextView, colorStateList: ColorStateList) {
            textView.setTextColor(colorStateList)
        }

        fun setGravity(textView: TextView, @GravityInt gravity: Int) {
            textView.gravity = gravity
        }

        fun setFont(textView: TextView, font: Typeface?, style: Int) {
            textView.setTypeface(font, style)
        }
    }

    fun applyTo(textView: TextView) {
        setSize(textView, size)
        setColor(textView, color)
        setGravity(textView, gravity)
        setFont(textView, font, fontStyle)
    }
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textSize(
    size: Int,
): ViewElement<V, LP> = onView {
    TextStyle.setSize(this, size)
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textColor(
    @ColorInt color: Int
): ViewElement<V, LP> = textColor(ColorStateList.valueOf(color))

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textColor(
    colorStateList: ColorStateList
): ViewElement<V, LP> = onView {
    TextStyle.setColor(this, colorStateList)
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

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textAllCaps(
    allCaps: Boolean = true
): ViewElement<V, LP> =
    onView {
        isAllCaps = allCaps
    }

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textHideIfEmpty(
    hideIfEmpty: Boolean = true
): ViewElement<V, LP> =
    onView {
        TextWatcherHideIfEmpty.remove(this)
        if (hideIfEmpty) {
            TextWatcherHideIfEmpty.init(this)
        }
    }

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textStyle(
    style: TextStyle
): ViewElement<V, LP> = onView {
    style.applyTo(this)
}