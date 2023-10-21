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
import io.noties.adapt.ui.shape.ShapeFactory
import io.noties.adapt.ui.shape.ShapeFactoryBuilder
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.OnScrollChangedListenerRegistration
import io.noties.adapt.ui.util.addOnScrollChangedListener
import io.noties.adapt.ui.util.density
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.onAttachedOnce
import io.noties.adapt.ui.util.onDetachedOnce
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
 * @see View.setBackground
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.background(
    block: ShapeFactoryBuilder
): ViewElement<V, LP> = background(block(ShapeFactory.NoOp))

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
 * @see View.setForegroundGravity
 * @see Shape.newDrawable
 * @see ShapeFactory
 */
@RequiresApi(Build.VERSION_CODES.M)
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.foreground(
    gravity: Gravity? = null,
    block: ShapeFactoryBuilder
): ViewElement<V, LP> = foreground(block(ShapeFactory.NoOp), gravity)

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
    val density = it.density
    it.setPaddingRelative(
        leading?.dip(density) ?: it.paddingStart,
        top?.dip(density) ?: it.paddingTop,
        trailing?.dip(density) ?: it.paddingEnd,
        bottom?.dip(density) ?: it.paddingBottom
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
    it.elevation = elevation.dip(it.density).toFloat()
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
    val density = view.density
    x?.dip(density)?.also { view.translationX = it.toFloat() }
    y?.dip(density)?.also { view.translationY = it.toFloat() }
    z?.dip(density)?.also { view.translationZ = it.toFloat() }
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

/**
 * Inside the callback actual scroll positions can be checked directly by [View.getScrollX] and [View.getScrollY].
 * Also callback can unregister listener (stop receiving scroll events) by calling [OnScrollChangedListenerRegistration.unregisterOnScrollChangedListener]
 * @see [OnScrollChangedListenerRegistration]
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewScrollChanged(
    action: OnScrollChangedListenerRegistration.(V, deltaX: Int, deltaY: Int) -> Unit
): ViewElement<V, LP> = onView { view ->
    lateinit var registration: OnScrollChangedListenerRegistration
    registration = view.addOnScrollChangedListener { _, deltaX, deltaY ->
        action(registration, view, deltaX, deltaY)
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
 * Scroll bars
 * @see View.setHorizontalScrollBarEnabled
 * @see View.setVerticalScrollBarEnabled
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.scrollBarsEnabled(
    horizontal: Boolean? = null,
    vertical: Boolean? = null
): ViewElement<V, LP> = onView {
    horizontal?.also { h -> it.isHorizontalScrollBarEnabled = h }
    vertical?.also { v -> it.isVerticalScrollBarEnabled = v }
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
    val density = view.density
    width?.dip(density)?.also { view.minimumWidth = it }
    height?.dip(density)?.also { view.minimumHeight = it }
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
 * shapes (cannot use path) - see [View.setClipToOutline] for more info.
 * __Supported shapes__ on most of the platform versions:
 * - `Rectangle`
 * - `RoundedRectangle`
 * - `Circle`
 * - `Capsule` (uses rounded rect internally for outline)
 * __Semi-supported__ shapes:
 * - `Corners` (seem to be supported on Android 33, but not before)
 * - `Oval` (seems to be supported on Android 33, but not before)
 * __Not supported__ shapes (the rest of):
 * - `Arc`
 * - `Asset` (does not support outline by itself, redirects this call to the source drawable),
 * - `Label`, `Text`,
 * - `Line`
 * @see View.setClipToOutline
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.clipToOutline(
    clipToOutline: Boolean = true
): ViewElement<V, LP> = onView {
    it.clipToOutline = clipToOutline
}

interface OnViewPreDrawRegistration {
    fun unregisterOnPreDraw()
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewPreDraw(
    block: OnViewPreDrawRegistration.(V) -> Unit
): ViewElement<V, LP> = onView { view ->
    view.viewTreeObserver
        .takeIf { it.isAlive }
        ?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener,
            OnViewPreDrawRegistration {
            override fun onPreDraw(): Boolean {
                // deliver callback
                block(this, view)
                // do not block drawing
                return true
            }

            init {
                // when detached, we should no longer receive updates
                view.onDetachedOnce { unregisterOnPreDraw() }
            }

            override fun unregisterOnPreDraw() {
                view.viewTreeObserver
                    .takeIf { it.isAlive }
                    ?.removeOnPreDrawListener(this)
            }
        })
    // trigger view invalidation
    view.invalidate()
}

/**
 * An utility function to trigger called in on-pre drawing state,
 * when view is measured and is going to be drawn on canvas.
 * Useful when view dimensions (width and height) should be available (after measure,
 * but before being drawn on screen)
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewPreDrawOnce(
    block: (V) -> Unit
): ViewElement<V, LP> = onViewPreDraw {
    block(it)
    unregisterOnPreDraw()
}

/**
 * NB! This is a callback when view is attached to [android.view.Window], not its parent
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewAttachedOnce(
    block: (V) -> Unit
): ViewElement<V, LP> = onView { view ->
    view.onAttachedOnce(block)
}

/**
 * NB! This is a callback when view is attached to [android.view.Window], not its parent
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewDetachedOnce(
    block: (V) -> Unit
): ViewElement<V, LP> = onView { view ->
    view.onDetachedOnce(block)
}

interface OnViewAttachedStateChangedRegistration {
    fun unregisterOnViewAttachedStateChanged()
}

/**
 * Registers listener to receive all attach/detach events
 * `block` callback receives 2 arguments:
 * + `view` - view that has callback registered
 * + `attached` - boolean indicating the state, true - view becomes attached, false - detached
 * NB! This is a callback when view is attached to [android.view.Window], not its parent
 * @see View.addOnAttachStateChangeListener
 * @see OnViewAttachedStateChangedRegistration
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewAttachedStateChanged(
    block: OnViewAttachedStateChangedRegistration.(view: V, attached: Boolean) -> Unit
): ViewElement<V, LP> = onView { view ->
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener,
        OnViewAttachedStateChangedRegistration {
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

        override fun unregisterOnViewAttachedStateChanged() {
            view.removeOnAttachStateChangeListener(this)
        }
    })
}

/**
 * Checks if specified SDK version is available (a device runs greater or equal SDK version
 * than specified)
 * If `else` behaviour is required, it is better to use an explicit block:
 * ```kotlin
 * element
 *   .also {
 *     if (Build.VERSION_CODES.M >= Build.VERSION.SDK_INT) {
 *       it.foreground()
 *     } else {
 *       it.background()
 *     }
 *   }
 * ```
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

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onElementView(
    block: (ViewElement<V, LP>) -> Unit
): ViewElement<V, LP> {
    return onView {
        block(this)
    }
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onViewLayoutChanged(
    callback: (V, width: Int, height: Int) -> Unit
): ViewElement<V, LP> = onView {
    it.addOnLayoutChangeListener { v, left, top, right, bottom, _, _, _, _ ->
        @Suppress("UNCHECKED_CAST")
        callback(v as V, right - left, bottom - top)
    }
}

/**
 * Utility function that returns this element in order to to indent after an element group:
 * ```
 * VStack {
 *   Text()
 * }.indent()
 *   .layoutFill()
 * ```
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.indent() = this

/**
 * An extension to execute [block] if [condition] is `true`.
 * If `if/else` is required it is better to use an explicit block:
 * ```kotlin
 * element
 *   .also {
 *     if (condition) {
 *       it.elevation(4)
 *     } else {
 *       it.elevation(0)
 *     }
 *   }
 * ```
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.doIf(
    condition: Boolean,
    block: (ViewElement<V, LP>) -> Unit
) = this.also {
    if (condition) {
        block(it)
    }
}

/**
 * Absolute values (in dp) from top-left of view bounds
 * @see View.setPivotX
 * @see View.setPivotY
 * @see pivotRelative
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.pivot(
    x: Int? = null,
    y: Int? = null
) = onView { view ->
    val density = view.density
    x?.dip(density)?.toFloat()?.also { view.pivotX = it }
    y?.dip(density)?.toFloat()?.also { view.pivotY = it }
}

/**
 * Normally should be in [0..1] range, but they also could be negative, or exceed 0..1 range
 * @see View.setPivotX
 * @see View.setPivotY
 * @see pivot
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.pivotRelative(
    x: Float? = null,
    y: Float? = null
) = onViewPreDrawOnce { view ->
    x?.also { view.pivotX = view.width * it }
    y?.also { view.pivotY = view.height * it }
}

// when x and y have the same value, for example `0.5F` or `1F`
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.pivotRelative(
    value: Float
) = pivotRelative(value, value)

