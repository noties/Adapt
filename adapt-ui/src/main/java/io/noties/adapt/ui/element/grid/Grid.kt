@file:Suppress("FINAL_UPPER_BOUND")

package io.noties.adapt.ui.element.grid

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewBuilder
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.shape.ShapeFactory
import io.noties.adapt.ui.shape.ShapeFactoryBuilder
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.widget.grid.GridOverlayIntRangeBuilder
import io.noties.adapt.ui.widget.grid.GridLayout
import io.noties.adapt.ui.widget.grid.GridOverlayLayout
import io.noties.adapt.ui.widget.grid.GridOverlayLayout.Axis

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Grid(
    children: ViewFactory<GridLayout.LayoutParams>.() -> Unit
) = ElementGroup(
    provider = { GridLayout(it) },
    children = children
)


fun <V : GridLayout, LP : LayoutParams> ViewElement<V, LP>.gridSpacing(
    spacing: Int
) = gridSpacing(vertical = spacing, horizontal = spacing)

fun <V : GridLayout, LP : LayoutParams> ViewElement<V, LP>.gridSpacing(
    vertical: Int? = null,
    horizontal: Int? = null
) = this.onView { v ->
    vertical?.dip?.also { v.verticalSpacingPx = it }
    horizontal?.dip?.also { v.horizontalSpacingPx = it }
}

interface GridOverlayFactoryConstants {
    companion object {
        @Suppress("EmptyRange")
        val skip: IntRange = 0 until 0
    }

    val skip: IntRange get() = Companion.skip

    fun just(value: Int): IntRange = value until (value + 1)

    fun fill(): IntRange = 0 until Int.MAX_VALUE

    fun first(): IntRange = 0 until 1

    fun last(count: Int): IntRange = (count - 1) until count

    /**
     * Moves forward receiver int-range by 1
     * ```kotlin
     *
     * (0 until 1) + 1 // => (1 until 2)
     * (10 ... 20) + 1 // => (11 ... 21)
     *
     * // can combine with helper ranges:
     * first() + 1 // => (1 until 2)
     * just(2) + 1 // => (3 until 4)
     * ```
     */
    operator fun IntRange.plus(value: Int) = IntRange(start + value, endInclusive + value)

    /**
     * Moves backward received int-range by 1
     * ```kotlin
     * (1 until 2) - 1 // => (0 until 1)
     * (11 ... 21) - 1 // => (10 ... 20)
     *
     * // can combine with helper ranges:
     * last(5) - 1 // => (3 until 4)
     * just(1) - 1 // => (0 until 1)
     * ```
     */
    operator fun IntRange.minus(value: Int) = IntRange(start - value, endInclusive - value)
}

// so, actually let's add FrameLayout as background and foreground and
//  provide it when building View or Drawable
@Suppress("FunctionName")
class GridOverlayFactory: GridOverlayFactoryConstants {
    companion object: GridOverlayFactoryConstants

    sealed class Entry
    class ViewEntry(val builder: ViewBuilder<out View, GridOverlayLayout.LayoutParams>) : Entry()
    class DrawableEntry(val drawable: Drawable) : Entry()

    // entries must preserve order
    private val entriesMutable = linkedMapOf<GridOverlayLayout.Key, Entry>()
    val entries: Map<GridOverlayLayout.Key, Entry> get() = entriesMutable

    ////////////////////////////////////////////////////////////////
    // VIEW
    ////////////////////////////////////////////////////////////////
    fun <V : View> View(
        x: GridOverlayIntRangeBuilder,
        y: GridOverlayIntRangeBuilder,
        view: ViewBuilder<V, GridOverlayLayout.LayoutParams>
    ) = add(x = Axis(x), y = Axis(y), entry = ViewEntry(view))

    fun <V : View> View(
        x: IntRange,
        y: GridOverlayIntRangeBuilder,
        view: ViewBuilder<V, GridOverlayLayout.LayoutParams>
    ) = add(x = raw(x), y = Axis(y), entry = ViewEntry(view))

    fun <V : View> View(
        x: GridOverlayIntRangeBuilder,
        y: IntRange,
        view: ViewBuilder<V, GridOverlayLayout.LayoutParams>
    ) = add(x = Axis(x), y = raw(y), entry = ViewEntry(view))

    fun <V : View> View(
        x: IntRange,
        y: IntRange,
        view: ViewBuilder<V, GridOverlayLayout.LayoutParams>
    ) = add(x = raw(x), y = raw(y), entry = ViewEntry(view))

    ////////////////////////////////////////////////////////////////
    // DRAWABLE
    ////////////////////////////////////////////////////////////////
    fun Drawable(
        x: IntRange,
        y: IntRange,
        drawable: Drawable
    ) = add(x = raw(x), y = raw(y), entry = DrawableEntry(drawable))

    fun Drawable(
        x: GridOverlayIntRangeBuilder,
        y: IntRange,
        drawable: Drawable
    ) = add(x = Axis(x), y = raw(y), entry = DrawableEntry(drawable))

    fun Drawable(
        x: IntRange,
        y: GridOverlayIntRangeBuilder,
        drawable: Drawable
    ) = add(x = raw(x), y = Axis(y), entry = DrawableEntry(drawable))

    fun Drawable(
        x: GridOverlayIntRangeBuilder,
        y: GridOverlayIntRangeBuilder,
        drawable: Drawable
    ) = add(x = Axis(x), y = Axis(y), entry = DrawableEntry(drawable))

    ////////////////////////////////////////////////////////////////
    // SHAPE
    ////////////////////////////////////////////////////////////////
    fun Shape(
        x: IntRange,
        y: IntRange,
        shape: ShapeFactoryBuilder
    ) = Drawable(x = x, y = y, drawable(shape))

    fun Shape(
        x: GridOverlayIntRangeBuilder,
        y: IntRange,
        shape: ShapeFactoryBuilder
    ) = Drawable(x = x, y = y, drawable(shape))

    fun Shape(
        x: IntRange,
        y: GridOverlayIntRangeBuilder,
        shape: ShapeFactoryBuilder
    ) = Drawable(x = x, y = y, drawable(shape))

    fun Shape(
        x: GridOverlayIntRangeBuilder,
        y: GridOverlayIntRangeBuilder,
        shape: ShapeFactoryBuilder
    ) = Drawable(x = x, y = y, drawable(shape))

    private fun add(x: Axis, y: Axis, entry: Entry) {
        entriesMutable[GridOverlayLayout.Key(x = x, y = y)] = entry
    }

    private fun raw(range: IntRange): Axis {
        // ignore received values, range is static and never change
        return Axis { _, _ ->
            range
        }
    }

    private fun drawable(shape: ShapeFactoryBuilder): Drawable {
        return ShapeFactory.create(shape).newDrawable()
    }
}

/**
 * @see gridOverlay
 */
fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridBackground(
    background: GridOverlayFactory.() -> Unit
) = gridOverlay(GridLayout.OVERLAY_PRIORITY_BACKGROUND, background)

/**
 * @see gridOverlay
 */
fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridForeground(
    background: GridOverlayFactory.() -> Unit
) = gridOverlay(GridLayout.OVERLAY_PRIORITY_FOREGROUND, background)

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
    background: GridOverlayFactory.() -> Unit
) = onView {
    val factory = GridOverlayFactory()
    background(factory)
    val entries = factory.entries
    if (entries.isNotEmpty()) {
        val overlay = it.getOrCreateOverlay(priority)
        entries.forEach { (key, value) ->
            overlay[key] = when (value) {
                is GridOverlayFactory.ViewEntry -> ViewFactory.newView(it).layoutParams(
                    GridOverlayLayout.LayoutParams()
                ).create {
                    value.builder.invoke(this)
                }

                is GridOverlayFactory.DrawableEntry -> View(it.context).also {
                    it.background = value.drawable
                }
            }
        }
    }
}