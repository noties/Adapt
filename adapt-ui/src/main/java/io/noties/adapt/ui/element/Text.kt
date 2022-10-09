package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.Layout
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
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

/**
 * Text size, supplied value is in SP and will be automatically converted to pixels
 * @see TextView.setTextSize
 */
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
 * @see TextView.setTextColor
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textColor(
    colorStateList: ColorStateList
): ViewElement<V, LP> = onView {
    setTextColor(colorStateList)
}

/**
 * Gravity
 * @see TextView.setGravity
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textGravity(
    gravity: Gravity
): ViewElement<V, LP> = onView {
    this.gravity = gravity.gravityValue
}

/**
 * Typeface
 * @see TextView.setTypeface
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textFont(
    font: Typeface? = null,
    fontStyle: Int = Typeface.NORMAL
): ViewElement<V, LP> = onView {
    setTypeface(font, fontStyle)
}

/**
 * All caps
 * @see TextView.setAllCaps
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textAllCaps(
    allCaps: Boolean = true
): ViewElement<V, LP> =
    onView {
        isAllCaps = allCaps
    }

/**
 * Hides a TextView if it has null or empty text
 */
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

/**
 * Hint
 * @see TextView.setHint
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textHint(
    hint: CharSequence?
): ViewElement<V, LP> = onView {
    this.hint = hint
}

/**
 * Ellipsize
 * @see TextView.setEllipsize
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textEllipsize(
    truncateAt: TextUtils.TruncateAt
): ViewElement<V, LP> = onView {
    this.ellipsize = truncateAt
}

/**
 * Maximum lines
 * @see TextView.setMaxLines
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textMaxLines(
    maxLines: Int
): ViewElement<V, LP> = onView {
    this.maxLines = maxLines
}

/**
 * Single line
 * @see TextView.setSingleLine
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textSingleLine(
    singleLine: Boolean = true
): ViewElement<V, LP> = onView {
    this.isSingleLine = singleLine
}

/**
 * HyphenationFrequency
 */
@RequiresApi(Build.VERSION_CODES.M)
@JvmInline
value class HyphenationFrequency(val flag: Int) {
    companion object {
        val none: HyphenationFrequency get() = HyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE)
        val normal: HyphenationFrequency get() = HyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL)
        val full: HyphenationFrequency get() = HyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textHyphenationFrequency(
    hyphenationFrequency: HyphenationFrequency
): ViewElement<V, LP> = onView {
    this.hyphenationFrequency = hyphenationFrequency.flag
}

/**
 * BreakStrategy
 */
@RequiresApi(Build.VERSION_CODES.M)
@JvmInline
value class BreakStrategy(val flag: Int) {
    companion object {
        val simple: BreakStrategy get() = BreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
        val balanced: BreakStrategy get() = BreakStrategy(Layout.BREAK_STRATEGY_BALANCED)
        val highQuality: BreakStrategy get() = BreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textBreakStrategy(
    breakStrategy: BreakStrategy
): ViewElement<V, LP> = onView {
    this.breakStrategy = breakStrategy.flag
}

/**
 * Text changed [TextWatcher.afterTextChanged]
 */
fun <V : TextView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.textOnTextChanged(
    action: (CharSequence?) -> Unit
): ViewElement<V, LP> = onView {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
            Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            action(s)
        }
    })
}