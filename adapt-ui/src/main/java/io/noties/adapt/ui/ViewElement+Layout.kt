package io.noties.adapt.ui

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip

/**
 * Specified layout dimensions for a view. Can be one of _normal_ layout attributes:
 * + [ViewGroup.LayoutParams.MATCH_PARENT] or [ViewFactory.FILL]
 * + [ViewGroup.LayoutParams.WRAP_CONTENT] or [ViewFactory.WRAP]
 * or an exact value specified in **density independent pixels** (dp), so when
 * specified `2` it would be converted to proper pixel value according to device density
 */
fun <V : View, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.layout(
    width: Int,
    height: Int
): ViewElement<V, LP> = onLayout {
    // special values
    if (width == MATCH_PARENT || width == WRAP_CONTENT) {
        this.width = width
    } else {
        // else exact dp values, convert to px
        this.width = width.dip
    }
    if (height == MATCH_PARENT || height == WRAP_CONTENT) {
        this.height = height
    } else {
        this.height = height.dip
    }
}

/**
 * Sets width and height view dimensions to MATCH_PARENT
 */
fun <V : View, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.layoutFill() =
    this.layout(MATCH_PARENT, MATCH_PARENT)

/**
 * Sets width and height view dimensions to WRAP_CONTENT
 */
fun <V : View, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.layoutWrap() =
    this.layout(WRAP_CONTENT, WRAP_CONTENT)


/**
 * LinearLayout layout dimensions and `layout_weight` attribute.
 * Normally, when inside horizontal LinearLayout - width=0
 * when inside vertical LinearLayout - height=0
 * @see layout
 * @see layoutWeight
 */
fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layout(
    width: Int,
    height: Int,
    weight: Float? = null
): ViewElement<V, LLP> {
    return this.also {
        (it as ViewElement<V, out LayoutParams>).layout(width, height)
        weight?.also { w ->
            it.onLayout { this.weight = w }
        }
    }
}

/**
 * Applies `layout_weight` attribute for a view inside [LinearLayout]
 * Consider using [layout(width, height, weight?)] in order to specify
 * appropriate width/height. Normally, when inside horizontal LinearLayout - width=0
 * when inside vertical LinearLayout - height=0
 */
fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutWeight(
    weight: Float
): ViewElement<V, LLP> = onLayout {
    this.weight = weight
}

/**
 * Specifies `layout_gravity` for a view inside LinearLayout (`VStack` or `HStack`)
 */
@JvmName("linearLayoutGravity")
fun <V : View, LLP : LinearLayout.LayoutParams> ViewElement<V, LLP>.layoutGravity(
    gravity: Gravity
): ViewElement<V, LLP> = onLayout {
    this.gravity = gravity.value
}

/**
 * Specifies `layout_gravity` for a view inside FrameLayout (`ZStack`)
 */
@JvmName("frameLayoutGravity")
fun <V : View, FLP : FrameLayout.LayoutParams> ViewElement<V, FLP>.layoutGravity(
    gravity: Gravity
): ViewElement<V, FLP> = onLayout {
    this.gravity = gravity.value
}

/**
 * Specifies value for all layout margins: `start`, `top`, `end` and `bottom`.
 * Value is in `dip`, it is automatically converted to pixels according to device density
 */
fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.layoutMargin(
    all: Int
) = layoutMargin(all, all, all, all)

/**
 * Specifies values for vertical and horizontal layout margins. If value is `null`
 * it is not applied as a margin (ignored)
 */
fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.layoutMargin(
    horizontal: Int? = null,
    vertical: Int? = null
) = layoutMargin(horizontal, vertical, horizontal, vertical)

/**
 * Specifies values for all layout margins
 * + `leading` => `marginStart`
 * + `top` => `topMargin`
 * + `trailing` => `marginEnd`
 * + `bottom` => `bottomMargin`
 * If value is `null` it is ignored
 */
fun <V : View, MLP : ViewGroup.MarginLayoutParams> ViewElement<V, MLP>.layoutMargin(
    leading: Int? = null,
    top: Int? = null,
    trailing: Int? = null,
    bottom: Int? = null
) = onLayout {
    leading?.dip?.also { marginStart = it }
    top?.dip?.also { topMargin = it }
    trailing?.dip?.also { marginEnd = it }
    bottom?.dip?.also { bottomMargin = it }
}