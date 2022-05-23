package io.noties.adapt.ui

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup.GONE
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.VISIBLE
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.GravityInt
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.shape.Shape
import kotlin.reflect.KMutableProperty0

/**
 * Reference
 */
@JvmName("referenceNotNull")
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.reference(
    property: KMutableProperty0<V>
): ViewElement<V, LP> = onView {
    property.set(this)
}

@JvmName("referenceNullable")
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.reference(
    property: KMutableProperty0<V?>
): ViewElement<V, LP> = onView {
    property.set(this)
}

/**
 * Id
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.id(
    id: Int
): ViewElement<V, LP> = onView {
    setId(id)
}

/**
 * Background
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    setBackgroundColor(color)
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(
    drawable: Drawable?
): ViewElement<V, LP> = onView {
    background = drawable
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.backgroundDefaultSelectable(): ViewElement<V, LP> =
    onView {
        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        val array = context.obtainStyledAttributes(attrs)
        try {
            val drawable = array.getDrawable(0)
            background = drawable
        } finally {
            array.recycle()
        }
    }

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(shape: Shape): ViewElement<V, LP> =
    background(shape.drawable())

/**
 * Foreground
 */
@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foreground(
    drawable: Drawable?,
    @GravityInt gravity: Int? = null
): ViewElement<V, LP> = onView {
    foreground = drawable
    gravity?.also { foregroundGravity = it }
}

@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foreground(
    shape: Shape,
    @GravityInt gravity: Int? = null
): ViewElement<V, LP> = foreground(shape.drawable(), gravity)

/**
 * Padding
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.padding(
    all: Int
): ViewElement<V, LP> = padding(all, all, all, all)

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.padding(
    horizontal: Int? = null,
    vertical: Int? = null
): ViewElement<V, LP> = padding(horizontal, vertical, horizontal, vertical)

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.padding(
    leading: Int? = null,
    top: Int? = null,
    trailing: Int? = null,
    bottom: Int? = null
): ViewElement<V, LP> = onView {
    setPaddingRelative(
        leading?.dip ?: paddingStart,
        top?.dip ?: paddingTop,
        trailing?.dip ?: paddingEnd,
        bottom?.dip ?: paddingBottom
    )
}

/**
 * Enabled
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.enabled(
    enabled: Boolean
): ViewElement<V, LP> = onView {
    isEnabled = enabled
}

/**
 * Activated
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.activated(
    activated: Boolean
): ViewElement<V, LP> = onView {
    isActivated = activated
}

/**
 * Visible
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.visible(
    visible: Boolean
): ViewElement<V, LP> = onView {
    visibility = if (visible) VISIBLE else GONE
}

/**
 * Alpha
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.alpha(
    @FloatRange(from = 0.0, to = 1.0) alpha: Float
): ViewElement<V, LP> = onView {
    this.alpha = alpha
}

/**
 * OnClick
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onClick(
    action: () -> Unit
): ViewElement<V, LP> = onView {
    setOnClickListener { action() }
}

/**
 * Elevation
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.elevation(
    elevation: Int
): ViewElement<V, LP> = onView {
    setElevation(elevation.dip.toFloat())
}

/**
 * Translation
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.translation(
    x: Int? = null,
    y: Int? = null,
    z: Int? = null
): ViewElement<V, LP> = onView {
    x?.dip?.also { translationX = it.toFloat() }
    y?.dip?.also { translationY = it.toFloat() }
    z?.dip?.also { translationZ = it.toFloat() }
}
