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
    it.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
}

/**
 * @see textColor(android.content.res.ColorStateList)
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textColor(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    it.setTextColor(color)
}

/**
 * @see io.noties.adapt.ui.util.ColorStateListBuilder
 * @see TextView.setTextColor
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textColor(
    colorStateList: ColorStateList
): ViewElement<V, LP> = onView {
    it.setTextColor(colorStateList)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textGradient(
    gradient: Gradient
): ViewElement<V, LP> = onView { view ->

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
    it.setShadowLayer(
        radius.dip.toFloat(),
        dx?.dip?.toFloat() ?: 0F,
        dy?.dip?.toFloat() ?: 0F,
        color ?: it.currentTextColor
    )
}

/**
 * Gravity
 * @see TextView.setGravity
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textGravity(
    gravity: Gravity
): ViewElement<V, LP> = onView {
    it.gravity = gravity.value
}

/**
 * Typeface
 * @see TextView.setTypeface
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textFont(
    font: Typeface? = null,
    fontStyle: Int = Typeface.NORMAL
): ViewElement<V, LP> = onView {
    it.setTypeface(font, fontStyle)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textBold(
): ViewElement<V, LP> = onView { view ->
    val tp = view.typeface
    val style = tp?.style?.let { it or Typeface.BOLD } ?: Typeface.BOLD
    view.setTypeface(tp, style)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textItalic(
): ViewElement<V, LP> = onView { view ->
    val tp = view.typeface
    val style = tp?.style?.let { it or Typeface.ITALIC } ?: Typeface.ITALIC
    view.setTypeface(tp, style)
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textUnderline(
    underline: Boolean = true
): ViewElement<V, LP> = onView {
    it.paint.isUnderlineText = underline
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textStrikeThrough(
    strikeThrough: Boolean = true
): ViewElement<V, LP> = onView {
    it.paint.isStrikeThruText = strikeThrough
}

/**
 * All caps
 * @see TextView.setAllCaps
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textAllCaps(
    allCaps: Boolean = true
): ViewElement<V, LP> = onView {
    it.isAllCaps = allCaps
}

/**
 * Hides a TextView if it has null or empty text
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHideIfEmpty(
    hideIfEmpty: Boolean = true
): ViewElement<V, LP> =
    onView {
        TextWatcherHideIfEmpty.remove(it)
        if (hideIfEmpty) {
            TextWatcherHideIfEmpty.init(it)
        } else {
            // if false, then make view visible, it could be hidden before
            it.visibility = View.VISIBLE
        }
    }

/**
 * @see TextView.setText
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.text(
    text: CharSequence?
): ViewElement<V, LP> = onView {
    it.text = text
}

/**
 * @see TextView.setText
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.text(
    @StringRes textResId: Int
): ViewElement<V, LP> = onView {
    it.setText(textResId)
}

/**
 * Hint
 * @see TextView.setHint
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHint(
    hint: CharSequence?
): ViewElement<V, LP> = onView {
    it.hint = hint
}

/**
 * Hint text color
 * @see TextView.setHintTextColor
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHintColor(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    it.setHintTextColor(color)
}

/**
 * Hint text color
 * @see TextView.setHintTextColor
 * @see io.noties.adapt.ui.util.ColorStateListBuilder
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textHintColor(
    colorStateList: ColorStateList
): ViewElement<V, LP> = onView {
    it.setHintTextColor(colorStateList)
}

/**
 * Ellipsize
 * @see TextView.setEllipsize
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textEllipsize(
    truncateAt: TextUtils.TruncateAt
): ViewElement<V, LP> = onView {
    it.ellipsize = truncateAt
}

/**
 * Maximum lines
 * @see TextView.setMaxLines
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textMaxLines(
    maxLines: Int
): ViewElement<V, LP> = onView {
    it.maxLines = maxLines
}

/**
 * Single line
 * @see TextView.setSingleLine
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textSingleLine(
    singleLine: Boolean = true
): ViewElement<V, LP> = onView {
    it.isSingleLine = singleLine
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
    it.hyphenationFrequency = hyphenationFrequency.value
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
    it.breakStrategy = breakStrategy.value
}

/**
 * Supplied values are in SP (so, 12 == 12.sp)
 * @see TextView.setAutoSizeTextTypeUniformWithPresetSizes
 */
@RequiresApi(Build.VERSION_CODES.O)
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textAutoSize(
    textSizes: IntArray
): ViewElement<V, LP> = onView {
    it.setAutoSizeTextTypeUniformWithPresetSizes(textSizes, TypedValue.COMPLEX_UNIT_SP)
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
    it.setAutoSizeTextTypeUniformWithConfiguration(
        minimumTextSize,
        maximumTextSize ?: (it.textSize / it.resources.displayMetrics.scaledDensity).roundToInt(),
        stepGranularity ?: 1,
        TypedValue.COMPLEX_UNIT_SP
    )
}

fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textSelectable(
    selectable: Boolean = true
): ViewElement<V, LP> = onView {
    it.setTextIsSelectable(selectable)
}

/**
 * Text changed [TextWatcher.afterTextChanged]
 */
fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textOnTextChanged(
    action: (CharSequence?) -> Unit
): ViewElement<V, LP> = onView {
    it.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
            Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            action(s)
        }
    })
}