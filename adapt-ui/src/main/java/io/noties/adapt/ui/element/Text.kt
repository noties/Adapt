package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.Layout
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.TextWatcherHideIfEmpty
import io.noties.adapt.ui.util.dip
import kotlin.math.roundToInt

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Text(
    text: CharSequence? = null
): ViewElement<TextView, LP> = Element(ElementViewFactory.Text) { tv ->
    text?.also { tv.text = it }
}

/**
 * Text size, supplied value is in SP and will be automatically converted to pixels
 * @see TextView.setTextSize
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textSize(
    size: Int,
): ViewElement<V, LP> = onView {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
}

/**
 * @see textColor(android.content.res.ColorStateList)
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textColor(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    setTextColor(color)
}

/**
 * @see io.noties.adapt.ui.util.ColorStateListBuilder
 * @see TextView.setTextColor
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textColor(
    colorStateList: ColorStateList
): ViewElement<V, LP> = onView {
    setTextColor(colorStateList)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textGradient(
    gradient: Gradient
): ViewElement<V, LP> = onView {
    val view = this

    val shaderBounds = Rect()
    val rect = Rect()

    fun deliver() {
        rect.set(
            view.paddingLeft,
            view.paddingTop,
            view.width - view.paddingRight,
            view.height - view.paddingBottom
        )

        if (shaderBounds != rect && !rect.isEmpty) {
            shaderBounds.set(rect)
            view.paint.shader = gradient.createShader(shaderBounds)
            view.invalidate()
        }
    }

    view.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> deliver() }

    // deliver right away if dimensions are already present
    if (view.width > 0 && view.height > 0) {
        deliver()
    }
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textShadow(
    radius: Int,
    @ColorInt color: Int? = null,
    dx: Int? = null,
    dy: Int? = null
): ViewElement<V, LP> = onView {
    setShadowLayer(
        radius.dip.toFloat(),
        dx?.dip?.toFloat() ?: 0F,
        dy?.dip?.toFloat() ?: 0F,
        color ?: this.currentTextColor
    )
}

/**
 * Gravity
 * @see TextView.setGravity
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textGravity(
    gravity: Gravity
): ViewElement<V, LP> = onView {
    this.gravity = gravity.value
}

/**
 * Typeface
 * @see TextView.setTypeface
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textFont(
    font: Typeface? = null,
    fontStyle: Int = Typeface.NORMAL
): ViewElement<V, LP> = onView {
    setTypeface(font, fontStyle)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textBold(
): ViewElement<V, LP> = onView {
    val tp = typeface
    val style = tp?.style?.let { it or Typeface.BOLD } ?: Typeface.BOLD
    setTypeface(tp, style)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textItalic(
): ViewElement<V, LP> = onView {
    val tp = typeface
    val style = tp?.style?.let { it or Typeface.ITALIC } ?: Typeface.ITALIC
    setTypeface(tp, style)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textUnderline(
    underline: Boolean = true
): ViewElement<V, LP> = onView {
    paint.isUnderlineText = underline
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textStrikeThrough(
    strikeThrough: Boolean = true
): ViewElement<V, LP> = onView {
    paint.isStrikeThruText = strikeThrough
}

/**
 * All caps
 * @see TextView.setAllCaps
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textAllCaps(
    allCaps: Boolean = true
): ViewElement<V, LP> =
    onView {
        isAllCaps = allCaps
    }

/**
 * Hides a TextView if it has null or empty text
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHideIfEmpty(
    hideIfEmpty: Boolean = true
): ViewElement<V, LP> =
    onView {
        TextWatcherHideIfEmpty.remove(this)
        if (hideIfEmpty) {
            TextWatcherHideIfEmpty.init(this)
        } else {
            // if false, then make view visible, it could be hidden before
            visibility = View.VISIBLE
        }
    }

/**
 * @see TextView.setText
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.text(
    text: CharSequence?
): ViewElement<V, LP> = onView {
    this.text = text
}

/**
 * @see TextView.setText
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.text(
    @StringRes textResId: Int
): ViewElement<V, LP> = onView {
    setText(textResId)
}

/**
 * Hint
 * @see TextView.setHint
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHint(
    hint: CharSequence?
): ViewElement<V, LP> = onView {
    this.hint = hint
}

/**
 * Ellipsize
 * @see TextView.setEllipsize
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textEllipsize(
    truncateAt: TextUtils.TruncateAt
): ViewElement<V, LP> = onView {
    this.ellipsize = truncateAt
}

/**
 * Maximum lines
 * @see TextView.setMaxLines
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textMaxLines(
    maxLines: Int
): ViewElement<V, LP> = onView {
    this.maxLines = maxLines
}

/**
 * Single line
 * @see TextView.setSingleLine
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textSingleLine(
    singleLine: Boolean = true
): ViewElement<V, LP> = onView {
    this.isSingleLine = singleLine
}

/**
 * HyphenationFrequency
 */
@RequiresApi(Build.VERSION_CODES.M)
@JvmInline
value class HyphenationFrequency(val value: Int) {
    companion object {
        val none: HyphenationFrequency get() = HyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE)
        val normal: HyphenationFrequency get() = HyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL)
        val full: HyphenationFrequency get() = HyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
    }
}

/**
 * @see TextView.setHyphenationFrequency
 */
@RequiresApi(Build.VERSION_CODES.M)
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHyphenationFrequency(
    hyphenationFrequency: HyphenationFrequency
): ViewElement<V, LP> = onView {
    this.hyphenationFrequency = hyphenationFrequency.value
}

/**
 * BreakStrategy
 * @see TextView.setBreakStrategy
 */
@RequiresApi(Build.VERSION_CODES.M)
@JvmInline
value class BreakStrategy(val value: Int) {
    companion object {
        val simple: BreakStrategy get() = BreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
        val balanced: BreakStrategy get() = BreakStrategy(Layout.BREAK_STRATEGY_BALANCED)
        val highQuality: BreakStrategy get() = BreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
    }
}

/**
 * @see TextView.setBreakStrategy
 */
@RequiresApi(Build.VERSION_CODES.M)
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textBreakStrategy(
    breakStrategy: BreakStrategy
): ViewElement<V, LP> = onView {
    this.breakStrategy = breakStrategy.value
}

/**
 * Supplied values are in SP (so, 12 == 12.sp)
 * @see TextView.setAutoSizeTextTypeUniformWithPresetSizes
 */
@RequiresApi(Build.VERSION_CODES.O)
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textAutoSize(
    textSizes: IntArray
): ViewElement<V, LP> = onView {
    setAutoSizeTextTypeUniformWithPresetSizes(textSizes, TypedValue.COMPLEX_UNIT_SP)
}

/**
 * Supplied values are in SP. Maximum value by default uses current [TextView.getTextSize]
 * @see TextView.setAutoSizeTextTypeUniformWithConfiguration
 */
@RequiresApi(Build.VERSION_CODES.O)
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textAutoSize(
    minimumTextSize: Int,
    maximumTextSize: Int? = null,
    stepGranularity: Int? = 1
): ViewElement<V, LP> = onView {
    setAutoSizeTextTypeUniformWithConfiguration(
        minimumTextSize,
        maximumTextSize ?: (textSize / resources.displayMetrics.scaledDensity).roundToInt(),
        stepGranularity ?: 1,
        TypedValue.COMPLEX_UNIT_SP
    )
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textSelectable(
    selectable: Boolean = true
): ViewElement<V, LP> = onView {
    setTextIsSelectable(selectable)
}

/**
 * Text changed [TextWatcher.afterTextChanged]
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textOnTextChanged(
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