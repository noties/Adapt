package io.noties.adapt.ui

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.GONE
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.VISIBLE
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.GravityInt
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.resolveDefaultSelectableDrawable
import kotlin.reflect.KMutableProperty0

/**
 * Reference
 */
@JvmName("referenceView")
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.reference(
    property: KMutableProperty0<in V>
): ViewElement<V, LP> = onView {
    property.set(this)
}

/**
 * Stores the element itself in specified mutable property.
 * Designed usage:
 * ```kotlin
 * newViewElement()
 *   .reference(ref::element)
 *
 * // which is the same as
 * newViewElement()
 *   .also { ref.element = it }
 * ```
 *
 * When element property represents a parent of a view, it would need to
 * have a type with `out` parameter, for example:
 *
 * ```kotlin
 * class Ref {
 *   lateinit var element: ViewElement<out View, *>
 * }
 * val ref = Ref()
 *
 * Text() // ViewElement<TextView, *>
 *   .reference(ref::element) // this works
 * ```
 */
@JvmName("referenceElement")
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.reference(
    property: KMutableProperty0<in ViewElement<V, LP>>
): ViewElement<V, LP> = this.also { property.set(it) }

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

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(shape: Shape): ViewElement<V, LP> =
    background(shape.drawable())

/**
 * @see background(Int)
 * @see background(Drawable?)
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.backgroundDefaultSelectable(): ViewElement<V, LP> =
    onView {
        background = resolveDefaultSelectableDrawable(context)
    }

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

@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foregroundDefaultSelectable(): ViewElement<V, LP> =
    onView {
        foreground = resolveDefaultSelectableDrawable(context)
    }

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
 * @see View.setEnabled
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.enabled(
    enabled: Boolean
): ViewElement<V, LP> = onView {
    isEnabled = enabled
}

/**
 * Activated
 * @see View.setActivated
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.activated(
    activated: Boolean
): ViewElement<V, LP> = onView {
    isActivated = activated
}

/**
 * Visible
 * @see View.setVisibility
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.visible(
    visible: Boolean
): ViewElement<V, LP> = onView {
    visibility = if (visible) VISIBLE else GONE
}

/**
 * Alpha
 * @see View.setAlpha
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.alpha(
    @FloatRange(from = 0.0, to = 1.0) alpha: Float
): ViewElement<V, LP> = onView {
    this.alpha = alpha
}

/**
 * OnClick
 * @see View.setOnClickListener
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onClick(
    action: (() -> Unit)?
): ViewElement<V, LP> = onView {
    if (action == null) {
        setOnClickListener(null)
    } else {
        setOnClickListener { action() }
    }
}

/**
 * Elevation
 * @see View.setElevation
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.elevation(
    elevation: Int
): ViewElement<V, LP> = onView {
    setElevation(elevation.dip.toFloat())
}

/**
 * Translation
 * @see View.setTranslationX
 * @see View.setTranslationY
 * @see View.setTranslationZ
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

/**
 * ClipChildren
 * @see ViewGroup.setClipChildren
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.clipChildren(
    clipChildren: Boolean
): ViewElement<V, LP> = onView {
    this.clipChildren = clipChildren
}

/**
 * ClipToPadding
 * @see ViewGroup.setClipToPadding
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.clipToPadding(
    clipToPadding: Boolean
): ViewElement<V, LP> = onView {
    this.clipToPadding = clipToPadding
}


/**
 * NoClip
 * @see clipToPadding
 * @see clipChildren
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.noClip(): ViewElement<V, LP> =
    this.clipChildren(false)
        .clipToPadding(false)

/**
 * OverScrollMode
 * @see View.setOverScrollMode
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.overScrollMode(
    overScrollMode: Int
): ViewElement<V, LP> = onView {
    this.overScrollMode = overScrollMode
}

/**
 * ScrollBarStyle
 * @see View.setScrollBarStyle
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.scrollBarStyle(
    scrollBarStyle: Int
): ViewElement<V, LP> = onView {
    this.scrollBarStyle = scrollBarStyle
}

/**
 * Minimum width and height
 * @see View.setMinimumWidth
 * @see View.setMinimumHeight
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.minimumSize(
    width: Int? = null,
    height: Int? = null
): ViewElement<V, LP> = onView {
    // there is no point of using `unused` here, as null would suffice
    //  (cannot set null)
    width?.dip?.also { minimumWidth = it }
    height?.dip?.also { minimumHeight = it }
}