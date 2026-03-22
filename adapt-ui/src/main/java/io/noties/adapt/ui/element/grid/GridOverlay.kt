@file:Suppress("FINAL_UPPER_BOUND")

package io.noties.adapt.ui.element.grid

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.widget.grid.GridLayout
import io.noties.adapt.ui.widget.grid.GridOverlayLayout
import io.noties.adapt.ui.widget.grid.GridOverlaySpanBuilder

class GridOverlayViewFactory(
    val gridOverlayLayout: GridOverlayLayout
) : ViewFactory<GridOverlayLayout.LayoutParams>(gridOverlayLayout) {

    companion object {
        fun addChildren(
            overlay: GridOverlayLayout,
            children: GridOverlayViewFactory.() -> Unit
        ) {
            addChildren(
                GridOverlayViewFactory(overlay),
                overlay,
                children
            )
        }
    }

    val skip: GridOverlaySpanBuilder get() = { skip }

    fun fill(): GridOverlaySpanBuilder = { fill() }
    fun first(): GridOverlaySpanBuilder = { first() }
    fun just(value: Int): GridOverlaySpanBuilder = { just(value) }
    fun last(count: Int): GridOverlaySpanBuilder = { last(count) }

    operator fun GridOverlaySpanBuilder.plus(value: Int): GridOverlaySpanBuilder {
        val ref = this
        return {
            ref.invoke(this, it).plus(value)
        }
    }

    operator fun GridOverlaySpanBuilder.minus(value: Int): GridOverlaySpanBuilder {
        val ref = this
        return {
            ref.invoke(this, it).minus(value)
        }
    }
}

fun <V : View, LP : GridOverlayLayout.LayoutParams> ViewElement<V, LP>.gridSpan(
    x: GridOverlaySpanBuilder? = null,
    y: GridOverlaySpanBuilder? = null
) = onLayoutParams { lp ->
    x?.also { lp.horizontalSpan = it }
    y?.also { lp.verticalSpan = it }
}

/**
 * @see gridOverlay
 */
fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridBackground(
    children: GridOverlayViewFactory.() -> Unit
) = gridOverlay(GridLayout.OVERLAY_PRIORITY_BACKGROUND, children)

///**
// * @see gridOverlay
// */
//fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridForeground(
//    background: GridOverlayFactory.() -> Unit
//) = gridOverlay(GridLayout.OVERLAY_PRIORITY_FOREGROUND, background)

/**
 * @see gridOverlay
 */
fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridForeground(
    children: GridOverlayViewFactory.() -> Unit
) = gridOverlay(GridLayout.OVERLAY_PRIORITY_FOREGROUND, children)

/**
 * Special _overlay_ layer that can span multiple columns or rows. Does not
 * affect measuring of [GridLayout], as it takes as much space as [GridLayout]
 * allocates to its children. But unlike children of [GridLayout] allows spanning
 * multiple columns and rows. As well as be behind or in front of [GridLayout] content by
 * specifying `priority`. `OVERLAY_PRIORITY_BACKGROUND` (-1) is drawn behind [GridLayout] content
 * and `OVERLAY_PRIORITY_FOREGROUND` (1) is drawn on top of content. Those 2 are merely a convenience
 * and priorities could be defined freely (like -2, 3, -7, etc) which allows building multi-layered
 * layouts based on grid structure.
 *
 * @see GridLayout.OVERLAY_PRIORITY_BACKGROUND
 * @see GridLayout.OVERLAY_PRIORITY_FOREGROUND
 */
fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridOverlay(
    priority: Int,
    children: GridOverlayViewFactory.() -> Unit
) = onView {
    val overlay = it.getOrCreateOverlay(priority)
    GridOverlayViewFactory.addChildren(
        overlay,
        children
    )
}