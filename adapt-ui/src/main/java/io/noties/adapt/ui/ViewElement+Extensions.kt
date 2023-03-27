package io.noties.adapt.ui

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.util.Gravity
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
    property.set(it)
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
 * @see View.setId
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.id(
    id: Int
): ViewElement<V, LP> = onView {
    it.id = id
}

/**
 * Tag
 * @see View.setTag
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.tag(
    tag: Any?,
    key: Int? = null
): ViewElement<V, LP> = onView {
    if (key != null) {
        it.setTag(key, tag)
    } else {
        it.tag = tag
    }
}

/**
 * Background
 * @see View.setBackgroundColor
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    it.setBackgroundColor(color)
}

/**
 * @see View.setBackground
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(
    drawable: Drawable?
): ViewElement<V, LP> = onView {
    it.background = drawable
}

/**
 * @see View.setBackground
 * @see Shape.newDrawable
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(
    shape: Shape
): ViewElement<V, LP> = background(shape.newDrawable())

/**
 * @see background(Int)
 * @see background(Drawable?)
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.backgroundDefaultSelectable(): ViewElement<V, LP> =
    onView {
        it.background = resolveDefaultSelectableDrawable(it.context)
    }

/**
 * Foreground
 * @see View.setForeground
 * @see View.setForegroundGravity
 */
@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foreground(
    drawable: Drawable?,
    gravity: Gravity? = null
): ViewElement<V, LP> = onView { view ->
    view.foreground = drawable
    gravity?.also { view.foregroundGravity = it.value }
}

/**
 * @see View.setForeground
 * @see View.setForegroundGravity
 * @see Shape.newDrawable
 */
@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foreground(
    shape: Shape,
    gravity: Gravity? = null
): ViewElement<V, LP> = foreground(shape.newDrawable(), gravity)

/**
 * @see View.setForeground
 */
@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foregroundDefaultSelectable(): ViewElement<V, LP> =
    onView {
        it.foreground = resolveDefaultSelectableDrawable(it.context)
    }

/**
 * Padding
 * @see View.setPaddingRelative
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.padding(
    all: Int
): ViewElement<V, LP> = padding(all, all, all, all)

/**
 * @see View.setPaddingRelative
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.padding(
    horizontal: Int? = null,
    vertical: Int? = null
): ViewElement<V, LP> = padding(horizontal, vertical, horizontal, vertical)

/**
 * @see View.setPaddingRelative
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.padding(
    leading: Int? = null,
    top: Int? = null,
    trailing: Int? = null,
    bottom: Int? = null
): ViewElement<V, LP> = onView {
    it.setPaddingRelative(
        leading?.dip ?: it.paddingStart,
        top?.dip ?: it.paddingTop,
        trailing?.dip ?: it.paddingEnd,
        bottom?.dip ?: it.paddingBottom
    )
}

/**
 * Enabled
 * @see View.setEnabled
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.enabled(
    enabled: Boolean = true
): ViewElement<V, LP> = onView {
    it.isEnabled = enabled
}

/**
 * Activated
 * @see View.setActivated
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.activated(
    activated: Boolean = true
): ViewElement<V, LP> = onView {
    it.isActivated = activated
}

/**
 * @see View.setSelected
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.selected(
    selected: Boolean = true
): ViewElement<V, LP> = onView {
    it.isSelected = selected
}

/**
 * Visible
 * @see View.setVisibility
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.visible(
    visible: Boolean
): ViewElement<V, LP> = onView {
    it.visibility = if (visible) VISIBLE else GONE
}

/**
 * Alpha
 * @see View.setAlpha
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.alpha(
    @FloatRange(from = 0.0, to = 1.0) alpha: Float
): ViewElement<V, LP> = onView {
    it.alpha = alpha
}

/**
 * Scale
 * @see View.setScaleX
 * @see View.setScaleY
 * @see scale
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.scale(
    xy: Float
): ViewElement<V, LP> = scale(xy, xy)

/**
 * Scale
 * @see View.setScaleX
 * @see View.setScaleY
 * @see scale
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.scale(
    x: Float? = null,
    y: Float? = null
): ViewElement<V, LP> = onView { view ->
    x?.let { view.scaleX = it }
    y?.let { view.scaleY = it }
}

/**
 * Rotation
 * @see View.setRotation
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.rotate(
    rotation: Float
): ViewElement<V, LP> = onView {
    it.rotation = rotation
}

/**
 * Elevation
 * @see View.setElevation
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.elevation(
    elevation: Int
): ViewElement<V, LP> = onView {
    it.elevation = elevation.dip.toFloat()
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
): ViewElement<V, LP> = onView { view ->
    x?.dip?.also { view.translationX = it.toFloat() }
    y?.dip?.also { view.translationY = it.toFloat() }
    z?.dip?.also { view.translationZ = it.toFloat() }
}

/**
 * OnClick
 * @see View.setOnClickListener
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onClick(
    action: (() -> Unit)?
): ViewElement<V, LP> = onClick(true, action = action)

/**
 * OnClick
 * @see View.setOnClickListener
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onClick(
    debounce: Boolean = true,
    debounceMillis: Long = 450L,
    action: (() -> Unit)?
): ViewElement<V, LP> = onView {
    if (action == null) {
        it.setOnClickListener(null)
    } else {
        if (debounce) {
            var lastClickMillis = 0L
            it.setOnClickListener {
                val now = SystemClock.uptimeMillis()
                if (now - lastClickMillis > debounceMillis) {
                    lastClickMillis = now
                    action()
                }
            }
        } else {
            it.setOnClickListener { action() }
        }
    }
}

/**
 * OnLongClick
 * @see View.OnLongClickListener
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onLongClick(
    action: (() -> Unit)?
): ViewElement<V, LP> = onView {
    if (action == null) {
        it.setOnLongClickListener(null)
    } else {
        it.setOnLongClickListener {
            action()
            true
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewScrollChanged(
    action: ((V, x: Int, y: Int) -> Unit)?
): ViewElement<V, LP> = onView {
    if (action == null) {
        it.setOnScrollChangeListener(null)
    } else {
        it.setOnScrollChangeListener { v, scrollX, scrollY, _, _ ->
            @Suppress("UNCHECKED_CAST")
            action(v as V, scrollX, scrollY)
        }
    }
}

/**
 * OverScrollMode
 * @see View.setOverScrollMode
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.overScrollMode(
    overScrollMode: Int
): ViewElement<V, LP> = onView {
    it.overScrollMode = overScrollMode
}

/**
 * ScrollBarStyle
 * @see View.setScrollBarStyle
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.scrollBarStyle(
    scrollBarStyle: Int
): ViewElement<V, LP> = onView {
    it.scrollBarStyle = scrollBarStyle
}

/**
 * Minimum width and height
 * @see View.setMinimumWidth
 * @see View.setMinimumHeight
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.minimumSize(
    width: Int? = null,
    height: Int? = null
): ViewElement<V, LP> = onView { view ->
    // there is no point of using `unused` here, as null would suffice
    //  (cannot set null)
    width?.dip?.also { view.minimumWidth = it }
    height?.dip?.also { view.minimumHeight = it }
}

/**
 * Focusable
 * @see View.setFocusable
 * @see View.setFocusableInTouchMode
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.focusable(
    focusable: Boolean = true,
    focusableInTouchMode: Boolean = focusable
): ViewElement<V, LP> = onView {
    it.isFocusable = focusable
    it.isFocusableInTouchMode = focusableInTouchMode
}

/**
 * Allows clipping a [View] by outline-provider. For example,
 * when a background drawable is set, by default it is used to
 * create an outline. Most of the shapes ([Shape]) do initialize
 * an outline, so they would work too. The exception is to use _simple_
 * shapes (cannot use path) - see [View.setClipToOutline] for more info
 * @see View.setClipToOutline
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.clipToOutline(
    clipToOutline: Boolean = true
): ViewElement<V, LP> = onView {
    it.clipToOutline = clipToOutline
}

/**
 * An utility function to trigger called in on-pre drawing state,
 * when view is measured and is going to be drawn on canvas.
 * Useful when view dimensions (width and height) should be available (after measure,
 * but before being drawn on screen)
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewPreDraw(
    block: (V) -> Unit
): ViewElement<V, LP> = onView { view ->
    val vto = view.viewTreeObserver.takeIf { it.isAlive } ?: return@onView
    vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {

            block(view)

            view.viewTreeObserver
                .takeIf { it.isAlive }
                ?.removeOnPreDrawListener(this)

            // do not block drawing
            return true
        }
    })
}

/**
 * `block` callback receives 3 arguments:
 * + `view` - view that has callback registered
 * + `attached` - boolean indicating the state, true - view becomes attached, false - detached
 * @see View.addOnAttachStateChangeListener
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewAttachedStateChanged(
    block: (view: V, attached: Boolean) -> Unit
): ViewElement<V, LP> = onView { view ->
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            notify(v, true)
        }

        override fun onViewDetachedFromWindow(v: View) {
            notify(v, false)
        }

        @Suppress("UNCHECKED_CAST")
        private fun notify(v: View, attached: Boolean) {
            block(v as V, attached)
        }
    })
}

/**
 * Checks if specified SDK version is available (a device runs greater or equal SDK version
 * than specified)
 */
@ChecksSdkIntAtLeast(parameter = 1, lambda = 2)
inline fun <V : View, LP : LayoutParams> ViewElement<V, LP>.ifAvailable(
    version: Int,
    block: (ViewElement<V, LP>) -> Unit
): ViewElement<V, LP> {
    if (Build.VERSION.SDK_INT >= version) {
        block(this)
    }
    return this
}

@ChecksSdkIntAtLeast(parameter = 1, lambda = 2)
inline fun <V : View, LP : LayoutParams> ViewElement<V, LP>.ifAvailable(
    version: Int,
    block: (ViewElement<V, LP>) -> Unit,
    `else`: (ViewElement<V, LP>) -> Unit
): ViewElement<V, LP> {
    if (Build.VERSION.SDK_INT >= version) {
        block(this)
    } else {
        `else`(this)
    }
    return this
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onElementView(
    block: (ViewElement<V, LP>) -> Unit
): ViewElement<V, LP> {
    val element = this
    return onView {
        block(element)
    }
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewLayout(
    callback: (V, width: Int, height: Int) -> Unit
): ViewElement<V, LP> = onView {
    it.addOnLayoutChangeListener { v, left, top, right, bottom, _, _, _, _ ->
        @Suppress("UNCHECKED_CAST")
        callback(v as V, right - left, bottom - top)
    }
}

