package io.noties.adapt.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeFactoryBuilder
import io.noties.adapt.ui.util.onDetachedOnce

/**
 * Please note that this overlay adds a [FrameLayout] as overlay view and then
 * adds all views from [builder] to it. Multiple view-elements can be added.
 *
 * Please also note, that supplied views are limited in functionality, for example,
 * one cannot add an on-click listener to it. Use solely as a decoration supplement.
 *
 * Please note, that overlays are normally excluded from transition animations
 *
 * @see android.view.ViewGroupOverlay.add
 * @see android.view.ViewGroupOverlay.clear
 */
fun <V : ViewGroup, LP : LayoutParams> ViewElement<V, LP>.overlayView(
    builder: ViewFactory<FrameLayout.LayoutParams>.() -> Unit
) = onView { viewGroup ->
    val frameLayout = FrameLayout(viewGroup.context)
    frameLayout.layoutParams =
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    ViewFactory.addChildren(frameLayout, builder)

    // required to measure and layout manually, as overlay does not do it
    //  by itself
    fun measureAndLayout() {
        frameLayout.measure(
            View.MeasureSpec.makeMeasureSpec(viewGroup.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(viewGroup.height, View.MeasureSpec.EXACTLY)
        )
        frameLayout.layout(0, 0, viewGroup.width, viewGroup.height)
    }

    measureAndLayout()

    viewGroup.overlay.add(frameLayout)

    val listener: View.OnLayoutChangeListener =
        View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            measureAndLayout()
        }

    viewGroup.addOnLayoutChangeListener(listener)

    // NB! when overlay is removed, it would receive _regular_ detached event,
    //  unregister layout listener
    frameLayout.onDetachedOnce { viewGroup.removeOnLayoutChangeListener(listener) }
}

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.overlay(
    builder: ShapeFactoryBuilder
) = overlay(builder(io.noties.adapt.ui.shape.ShapeFactory.NoOp))

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.overlay(
    shape: Shape
) = overlay(shape.newDrawable())

/**
 * Please note, that this drawable would receive drawable state of parent view,
 * but some other drawable must first be added to the view that could process this state.
 * So, for example, if view has on-click listener, but only a stateful overlay is added,
 * it won't receive `pressed` state. As a workaround - add to parent view a stateful drawable
 * (for example, `RectangleShape().newDrawable().stateful()` would capture all states) or
 * use this overlay directly as a background or foreground.
 *
 * Please note, that overlays are normally excluded from transition animations
 *
 * @see android.view.ViewOverlay.add
 * @see android.view.ViewOverlay.clear
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.overlay(
    drawable: Drawable
) = onView { view ->

    fun syncStateAndBounds() {
        drawable.state = view.drawableState
        drawable.setBounds(0, 0, view.width, view.height)
    }

    syncStateAndBounds()

    view.overlay.add(drawable)

    view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            // drawable was removed from overlay
            if (drawable.callback == null) {
                view.viewTreeObserver
                    .takeIf { it.isAlive }
                    ?.removeOnPreDrawListener(this)
            } else {
                syncStateAndBounds()
            }
            return true
        }
    })
}