package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.util.Gravity
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

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textSize(
    size: Int,
): ViewElement<V, LP> = onView {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
}

/**
 * @see textColor(android.content.res.ColorStateList)
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textColor(
    @ColorInt color: Int
): ViewElement<V, LP> = textColor(ColorStateList.valueOf(color))

/**
 * @see io.noties.adapt.ui.util.ColorStateListBuilder
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textColor(
    colorStateList: ColorStateList
): ViewElement<V, LP> = onView {
    setTextColor(colorStateList)
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textGravity(
    gravity: Gravity
): ViewElement<V, LP> = onView {
    this.gravity = gravity.gravityValue
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textFont(
    font: Typeface? = null,
    fontStyle: Int = Typeface.NORMAL
): ViewElement<V, LP> = onView {
    setTypeface(font, fontStyle)
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

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.text(
    text: CharSequence?
): ViewElement<V, LP> = onView {
    this.text = text
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.text(
    @StringRes textResId: Int
): ViewElement<V, LP> = onView {
    setText(textResId)
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textHint(
    hint: CharSequence?
): ViewElement<V, LP> = onView {
    this.hint = hint
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textEllipsize(
    truncateAt: TextUtils.TruncateAt
): ViewElement<V, LP> = onView {
    this.ellipsize = truncateAt
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textMaxLines(
    maxLines: Int
): ViewElement<V, LP> = onView {
    this.maxLines = maxLines
}

fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textSingleLine(
    singleLine: Boolean = true
): ViewElement<V, LP> = onView {
    this.isSingleLine = singleLine
}