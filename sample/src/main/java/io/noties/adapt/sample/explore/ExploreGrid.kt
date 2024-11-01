package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.ColorInt
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.R
import io.noties.adapt.sample.explore.ExploreGrid.Grid
import io.noties.adapt.sample.explore.ExploreGrid.GridRow
import io.noties.adapt.sample.explore.ExploreGrid.GridSpacer
import io.noties.adapt.sample.explore.ExploreGrid.gridBackground
import io.noties.adapt.sample.explore.ExploreGrid.gridColumns
import io.noties.adapt.sample.explore.ExploreGrid.gridForeground
import io.noties.adapt.sample.explore.ExploreGrid.gridOverlay
import io.noties.adapt.sample.explore.ExploreGrid.gridSpacing
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.white
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.sample.util.children
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewBuilder
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.ZStackSquare
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onElementView
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.preview.PreviewViewCustomization
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.Oval
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.ShapeFactory
import io.noties.adapt.ui.shape.ShapeFactoryBuilder
import io.noties.adapt.ui.util.AbsDrawable
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.GravityBuilder
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.withAlphaComponent
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object ExploreGrid {
    // raw Grid and GridRow

    @Suppress("FunctionName")
    fun <LP : io.noties.adapt.ui.LayoutParams> ViewFactory<LP>.Grid(
        children: ViewFactory<GridLayout.LayoutParams>.() -> Unit
    ) = ElementGroup(
        provider = { GridLayout(it) },
        children = children
    )

    fun <V : GridLayout, LP : io.noties.adapt.ui.LayoutParams> ViewElement<V, LP>.gridSpacing(
        spacing: Int
    ) = gridSpacing(vertical = spacing, horizontal = spacing)

    fun <V : GridLayout, LP : io.noties.adapt.ui.LayoutParams> ViewElement<V, LP>.gridSpacing(
        vertical: Int? = null,
        horizontal: Int? = null
    ) = this.onView { v ->
        vertical?.dip?.also { v.verticalSpacingPx = it }
        horizontal?.dip?.also { v.horizontalSpacingPx = it }
    }

    @Suppress("FunctionName")
    fun <LP : GridLayout.LayoutParams> ViewFactory<LP>.GridRow(
        children: ViewFactory<GridRowLayout.LayoutParams>.() -> Unit
    ) = ElementGroup(
        provider = { GridRowLayout(it) },
        children = children
    )

    @Suppress("FunctionName")
    fun <LP : GridRowLayout.LayoutParams> ViewFactory<LP>.GridSpacer() = Element { GridSpacer(it) }

    fun <V : View, LP : GridRowLayout.LayoutParams> ViewElement<V, LP>.gridColumns(columns: Int) =
        this.onLayoutParams {
            it.spanColumns = columns
        }

    fun <V : View, LP : GridRowLayout.LayoutParams> ViewElement<V, LP>.gridCellGravity(gravity: GravityBuilder) =
        this.onLayoutParams {
            it.gravity = gravity(Gravity)
        }

    fun <V : GridRowLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridRowContentGravity(
        gravity: GravityBuilder
    ) = onView { it.contentGravity = gravity(Gravity) }

    // so, actually let's add FrameLayout as background and foreground and
    //  provide it when building View or Drawbale
    class GridOverlayFactory(
        val gridColumnCount: Int,
        val gridRowCount: Int
    ) {
        sealed class Entry
        class ViewEntry(val builder: ViewBuilder<out View, GridOverlay.LayoutParams>) : Entry()
        class DrawableEntry(val drawable: Drawable) : Entry()

        // TODO: we can actually expose number of columns/rows here

        // entries must preserve order
        private val entriesMutable = linkedMapOf<GridOverlay.Key, Entry>()
        val entries: Map<GridOverlay.Key, Entry> get() = entriesMutable

        // it might be that it should be ViewBuilder<V, LP> = ViewFactory<LP>.() -> ViewElement<V, LP>
        fun <V : View> View(
            x: IntRange,
            y: IntRange,
            view: ViewBuilder<V, GridOverlay.LayoutParams>
        ) {
            entriesMutable[GridOverlay.Key(x, y)] = ViewEntry(view)
        }

        fun Shape(
            x: IntRange,
            y: IntRange,
            shape: ShapeFactoryBuilder
        ) {
            Drawable(
                x = x,
                y = y,
                drawable = ShapeFactory.create(shape).newDrawable()
            )
        }

        fun Drawable(
            x: IntRange,
            y: IntRange,
            drawable: Drawable
        ) {
            entriesMutable[GridOverlay.Key(x, y)] = DrawableEntry(drawable)
        }
    }

    // TODO: the same could be applied to any overlay: foreground, by manual priority
    fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridBackground(
        background: GridOverlayFactory.() -> Unit
    ) = gridOverlay(GridLayout.OVERLAY_PRIORITY_BACKGROUND, background)

    fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridForeground(
        background: GridOverlayFactory.() -> Unit
    ) = gridOverlay(GridLayout.OVERLAY_PRIORITY_FOREGROUND, background)

    // TODO: the same could be applied to any overlay: foreground, by manual priority
    fun <V : GridLayout, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.gridOverlay(
        priority: Int,
        background: GridOverlayFactory.() -> Unit
    ) = onView {
        val factory = GridOverlayFactory(
            gridColumnCount = it.columnsCount,
            gridRowCount = it.rowsCount
        )
        background(factory)
        val entries = factory.entries
        if (entries.isNotEmpty()) {
            val overlay = it.getOrCreateOverlay(priority)
            entries.forEach { (key, value) ->
                overlay[key] = when (value) {
                    is GridOverlayFactory.ViewEntry -> ViewFactory.newView(it).layoutParams(
                        GridOverlay.LayoutParams()
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

    // TODO: it should be measured afterwards (after all children of GridLayout are measured)
    //  but in case of background - drawn very first
    class GridOverlay(context: Context) : ViewGroup(context) {
        data class Key(
            val x: IntRange,
            val y: IntRange
        )

        // TODO: padding should be ignored/removed
        init {
            clipChildren = false
            clipToPadding = false
        }

        private val gridLayout: GridLayout get() = parent as GridLayout

        // TODO: expose public + private mutable
        private val entries = linkedMapOf<Key, View>()

        operator fun set(key: Key, value: View) {
            val previous = entries.put(key, value)
            if (previous != null) {
                removeView(previous)
            }
            addView(value)
        }

        fun clear() {
            entries.clear()
            removeAllViews()
        }

        // how do we make it sync its state with the parent?

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            // TODO: can we rely on GridLayout to contain measured values already?
//
//            val width = MeasureSpec.getSize(widthMeasureSpec)
//            val height = MeasureSpec.getSize(heightMeasureSpec)

//            val columns = gridLayout.columnsCount
//            val rows = gridLayout.rowsCount

            val columnWidth = gridLayout.columnWidth
            val horizontalSpacing = gridLayout.horizontalSpacingPx

//            val horizontalSpacing = gridLayout.horizontalSpacingPx
            val verticalSpacing = gridLayout.verticalSpacingPx

//            val heights = gridLayout.rows.map { it.height }

            entries.forEach { (key, view) ->
                val lp = view.overlayLayoutParams

                println(":(over.measure lastHorizontal:${gridLayout.lastColumn} .vertical:${gridLayout.lastRow}")
                println(":(over.measure range horizontal:${key.x} .last:${key.x.last} vertical:${key.y} .last:${key.y.last}")

                val (startX, endX) = key.x.horizontal()
                val (startY, endY) = key.y.vertical()

                println(":(overlay.measure horizontal:[$startX $endX] vertical:[$startY $endY]")

                lp.x = if (startX == 0) {
                    0
                } else {
                    // always an item and horizontal space that comes before this one
                    (startX * columnWidth) + (startX * horizontalSpacing)
                }

                lp.y = if (startY == 0) {
                    0
                } else {
                    gridLayout.rowHeights.subList(0, startY).sum() + (startY * verticalSpacing)
                }

                // width is unified between all columns/rows
                val width = gridLayout.calculateCellWidthWithSpan(
                    // 2 to 3 => means 2 and 3
                    (endX - startX) + 1,
                    columnWidth = columnWidth
                )

                // height is specific to rows (differ between them), use actual heights to obtain this value
                val height = gridLayout.calculateCellHeightWithSpan(startY, endY)

                println(":[overlay x:${lp.x} y:${lp.y} width:$width height:$height columnWidth:$columnWidth")

                view.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                )
            }

            setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
            )
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            children.forEach { child ->
                val lp = child.overlayLayoutParams
                child.layout(
                    lp.x,
                    lp.y,
                    lp.x + child.measuredWidth,
                    lp.y + child.measuredHeight
                )
            }
        }

        @Deprecated("Has no effect, padding on GridOverlay is ignored")
        override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
            // no op
        }

        private fun IntRange.horizontal(): IntRange {
            val last = last.coerceAtMost(gridLayout.lastColumn)
            return IntRange(
                // start cannot be less than 0
                start = first.coerceIn(0..last),
                // end cannot be more than columns
                endInclusive = last
            )
        }

        private fun IntRange.vertical(): IntRange {
            val last = last.coerceAtMost(gridLayout.lastRow)
            return IntRange(
                start = first.coerceIn(0..last),
                endInclusive = last
            )
        }

        private operator fun IntRange.component1(): Int {
            return first
        }

        private operator fun IntRange.component2(): Int {
            return last
        }

        private val View.overlayLayoutParams: LayoutParams get() = this.layoutParams as LayoutParams

        class LayoutParams : ViewGroup.LayoutParams {
            // lateinit is not available on primitives.. :'( AND it is a typo when used in a comment
            var x: Int = 0
            var y: Int = 0

            constructor() : super(MATCH_PARENT, MATCH_PARENT)
            constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
            constructor(width: Int, height: Int) : super(width, height)
            constructor(source: ViewGroup.LayoutParams) : super(source)
        }

        override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
            return super.checkLayoutParams(p)
        }

        override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
            return LayoutParams(context, attrs)
        }

        override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
            return LayoutParams(p)
        }

        override fun generateDefaultLayoutParams(): LayoutParams {
            return LayoutParams()
        }
    }

    // TODO: secondary constructor, do we need it? maybe for preview?
    class GridLayout : ViewGroup {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        companion object {
            const val OVERLAY_PRIORITY_BACKGROUND = -1
            const val OVERLAY_PRIORITY_NONE = 0
            const val OVERLAY_PRIORITY_FOREGROUND = 1

            private val OVERLAY_TAG_ID get() = R.id.adapt_grid_overlay_priority
        }

        private var additionalHierarchyChangeListener: OnHierarchyChangeListener? = null

        init {
            // required in order to customize drawing order
            //  we need it right now to properly position overlays
            isChildrenDrawingOrderEnabled = true

            clipChildren = false
            clipToPadding = false

            // ensure our listener is always registered (override + call super)
            super.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View?, child: View?) {
                    calculateRowsAndOverlays()
                    calculateDrawingPositions()
                    additionalHierarchyChangeListener?.onChildViewAdded(parent, child)
                }

                override fun onChildViewRemoved(parent: View?, child: View?) {
                    calculateRowsAndOverlays()
                    calculateDrawingPositions()
                    additionalHierarchyChangeListener?.onChildViewRemoved(parent, child)
                }
            })
        }

        // See the connection diagram between different layout stages
        // Basically, when children change -> we need to forceLayout here and requestLayout from parent
        // https://stackoverflow.com/questions/45383948/how-does-forcelayout-work-in-android
        override fun requestLayout() {
            super.requestLayout()

            children.forEach {
                it.forceLayout()
            }
        }

        override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener?) {
            additionalHierarchyChangeListener = listener
        }

        private fun calculateRowsAndOverlays() {
            val (overlays, rows) = children
                .partition { it is GridOverlay }

            overlaysMutable.clear()
            @Suppress("UNCHECKED_CAST")
            overlaysMutable.addAll(overlays as List<GridOverlay>)

            rowsMutable.clear()
            rowsMutable.addAll(rows)
        }

        private fun calculateDrawingPositions() {
            drawingOrder.clear()
            children
                .asSequence()
                .withIndex()
                .map {
                    it.index to (it.value.getTag(OVERLAY_TAG_ID) as? Int ?: OVERLAY_PRIORITY_NONE)
                }
                .sortedWith { o1, o2 ->
                    val priority = o1.second.compareTo(o2.second)
                    if (priority == 0) {
                        // compare original indexes to preserve original order
                        o1.first.compareTo(o2.first)
                    } else {
                        priority
                    }
                }
                .withIndex()
                .onEach { (i, v) ->
                    // manually assign z value, otherwise drawing order
                    //  is (even though getChildDrawingOrder overridden, it is unstable and
                    //  android is not following what is inside)
                    getChildAt(v.first).z = i.toFloat()
                }
                .map { (i, v) ->
                    // after sorted, take index in out list and use it as position
                    v.first to i
                }
                .also {
                    drawingOrder.putAll(it)
                }
        }

        // get drawing order, background - first, overlay - last
        override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
            return drawingOrder[drawingPosition] ?: run {
                // could happen when empty for example
                println("[GridLayout.getChildDrawingOrder] Drawing position is missing for:$drawingPosition childCount:$childCount")
                drawingPosition
            }
        }

        // TODO: recalculate on hierarchy change and cache?
        // TODO: or obtain when measuring and cache? even better we know already there
        val columnsCount: Int
            // must not calculate overlays, only rows
            get() = rows.maxOf {
                (it as? GridRowLayout)?.contentColumns ?: 1
            }

        val lastColumn get() = columnsCount - 1
        val lastRow get() = rowsCount - 1

        // calculate only actual rows, ignore overlays
        val rowsCount: Int get() = rows.size

        private val rowsMutable = mutableListOf<View>()
        val rows: List<View> get() = rowsMutable

        private val rowHeightsMutable = mutableListOf<Int>()
        val rowHeights: List<Int> = rowHeightsMutable

        private val overlaysMutable = mutableListOf<GridOverlay>()
        val overlays: List<GridOverlay> get() = overlaysMutable

        // predefined overlay for background
        val gridBackground: GridOverlay get() = getOrCreateOverlay(OVERLAY_PRIORITY_BACKGROUND)

        // predefined overlay for foreground
        val gridForeground: GridOverlay get() = getOrCreateOverlay(OVERLAY_PRIORITY_FOREGROUND)

        private val drawingOrder = mutableMapOf<Int, Int>()

        fun getOrCreateOverlay(priority: Int): GridOverlay {
            return overlays.firstOrNull {
                priority == it.getTag(OVERLAY_TAG_ID)
            } ?: run {
                val view = GridOverlay(context)
                view.setTag(OVERLAY_TAG_ID, priority)
                addView(view)
                view
            }
        }

//        fun cellWidth(spans: Int, contentWidth: Int = width - paddingLeft - paddingRight): Int {
//            val columns = columnsCount
//            val cellWidth = contentWidth - ((columns - 1) * horizontalSpacingPx) / columns
//            return cellWidth + ((spans - 1) * horizontalSpacingPx)
//        }

        fun calculateCellWidthWithSpan(
            span: Int,
            columnWidth: Int = this.columnWidth,
            horizontalSpacingPx: Int = this.horizontalSpacingPx
        ): Int {
            return (span * columnWidth) + ((span - 1) * horizontalSpacingPx)
        }

        // height requires actual span row numbers, as each row can have different height
        fun calculateCellHeightWithSpan(
            startSpan: Int,
            endSpan: Int,
            verticalSpacingPx: Int = this.verticalSpacingPx
        ): Int {
            val heights = rowHeights
            val s = startSpan.coerceIn(heights.indices)
            val e = endSpan.coerceIn(heights.indices)
                .takeIf { it >= s }
                ?: return 0
            println(
                ":{cchws start:$startSpan end:$endSpan vertical:$verticalSpacingPx " +
                        "list:${heights.drop(s).take(e - s + 1)} take:${e - s + 1}"
            )
            println(
                ":{cchws heights:$heights s:$s e:$e sum:${
                    heights.subList(
                        s,
                        e
                    ).sum()
                } minus:${e - s - 1}"
            )
            println(":{cchws sub:${heights.subList(s, e)}")
            return heights.drop(s).take(e - s + 1).sum() + ((e - s) * verticalSpacingPx)
        }

        val columnWidth: Int get() = calculateColumnWidth()

        // We might loose a little precision when doing the int division,
        //  if we have children that would need to match_parent (not a grid-row)
        //  then it should use proper calculated width for total-span count, otherwise
        //  those views might be greater/less than our rows
        fun calculateColumnWidth(
            contentWidth: Int = (measuredWidth - paddingLeft - paddingRight),
            columnsCount: Int = this.columnsCount,
            horizontalSpacingPx: Int = this.horizontalSpacingPx
        ): Int {
            return (contentWidth - ((columnsCount - 1) * horizontalSpacingPx)) / columnsCount
        }

        var verticalSpacingPx: Int = 0
            set(value) {
                field = value
                requestLayout()
            }

        var horizontalSpacingPx: Int = 0
            set(value) {
                field = value
                requestLayout()
            }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            var y = paddingTop

            rows.forEach { row ->
                row.layout(
                    paddingStart,
                    y,
                    paddingStart + row.measuredWidth,
                    y + row.measuredHeight
                )
                y += row.measuredHeight + verticalSpacingPx
            }

            // layout overlays after rows
            overlays.forEach { overlay ->
                overlay.layout(
                    paddingStart,
                    paddingTop,
                    paddingStart + overlay.measuredWidth,
                    paddingTop + overlay.measuredHeight
                )
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val columnsCount = this.columnsCount
                .takeIf { it > 0 }
                ?: return super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            // adjust to padding too
            val paddingLeading = paddingStart
            val paddingTrailing = paddingEnd

            val (measuredWidth, contentWidth) = run {
                val w = MeasureSpec.getSize(widthMeasureSpec)
                if (w <= 0) {
                    error("GridLayout requires definite width: match_parent or px/dp")
                }

                val width = w - paddingLeading - paddingTrailing
                val columnWidth = calculateColumnWidth(
                    contentWidth = width,
                    columnsCount = columnsCount
                )
                // validate that all columns would equal received width
                //  otherwise we need to update it (due to int division, we might
                //  loose some precision, but we should make children of equal
                //  widths)
                val totalColumnsWidth = calculateCellWidthWithSpan(
                    columnsCount,
                    columnWidth = columnWidth
                )
                if (width != totalColumnsWidth) {
                    (totalColumnsWidth + paddingLeading + paddingTrailing) to totalColumnsWidth
                } else {
                    width to totalColumnsWidth
                }
            }

            val childWidthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY)

            var measuredHeight = paddingTop

            // it seems ViewGroup does not measure children by default
            // spacing is applied only between (add always but not first)
            rows.withIndex()
                .forEach { (i, v) ->
                    if (i != 0) {
                        measuredHeight += verticalSpacingPx
                    }
                    val lp = v.layoutParams as LayoutParams
                    val childHeightSpec = if (lp.height > 0) {
                        MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY)
                    } else {
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    }
                    v.measure(childWidthSpec, childHeightSpec)
                    measuredHeight += v.measuredHeight
                }

            // recreate each time measured
            rowHeightsMutable.clear()
            // cache heights
            rows.map { it.measuredHeight }.also { rowHeightsMutable.addAll(it) }

            overlays.forEach { overlay ->
                overlay.measure(
                    MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY),
                    // measuredHeight contains padding-top, overlay should not contain padding
                    MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop, MeasureSpec.EXACTLY)
                )
            }

            setMeasuredDimension(
                measuredWidth,
                measuredHeight + paddingBottom
            )
        }

        open class LayoutParams : ViewGroup.LayoutParams {
            // by default content is wrap
            constructor() : super(MATCH_PARENT, WRAP_CONTENT)
            constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
            constructor(width: Int, height: Int) : super(width, height)
            constructor(source: ViewGroup.LayoutParams) : super(source)
        }

        override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
            return p is LayoutParams
        }

        override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
            return LayoutParams(context, attrs)
        }

        override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
            return LayoutParams(p)
        }

        override fun generateDefaultLayoutParams(): LayoutParams {
            return LayoutParams()
        }
    }

    // TODO: make it take proper values (fill width and height)
    // TODO: make it view-group, so space be filled with other views
    class GridSpacer : View, PreviewViewCustomization {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        override fun preview(color: Int, view: View): Drawable {
            // TODO: different locations fro drawables
            return PreviewViewCustomization.Companion.PreviewDrawable(
                listOf(
                    PreviewViewCustomization.Companion.PreviewPaddingDrawable(color, view),
                    PreviewViewCustomization.Companion.PreviewBoundsDrawable(color, true),
                    PreviewGridSpacerDrawable(color, view)
                )
            )
        }

        private class PreviewGridSpacerDrawable(
            @ColorInt color: Int,
            private val view: View
        ) : AbsDrawable() {

            private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
                it.style = Paint.Style.FILL
                it.color = color
                it.strokeWidth = 2.dip.toFloat()
                it.pathEffect = DashPathEffect(
                    floatArrayOf(4.dip.toFloat(), 8.dip.toFloat()),
                    0F
                )
            }

            override fun draw(canvas: Canvas) {
                val spans = (view.layoutParams as? GridRowLayout.LayoutParams)
                    ?.spanColumns
                    ?.takeIf { it > 1 } ?: return

                val w = bounds.width()
                val h = bounds.height()

                // horizontal
                run {
                    val step = w / spans
                    repeat(spans) {
                        val x = ((it + 1) * step).toFloat()
                        canvas.drawLine(
                            x,
                            0F,
                            x,
                            h.toFloat(),
                            paint
                        )
                    }
                }
            }
        }
    }

    // accepts content gravity
    // NB! ignores padding and layout-params
    //  padding is always 0, lp = width=fill/height=wrap (or specific if provided)
    class GridRowLayout : ViewGroup {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        init {
            clipChildren = false
            clipToPadding = false
        }

        companion object {
            val defaultGravity = Gravity.center
        }

        // NB! GridSpacer is using `spanColumns` to report how many
        //  of spans a spacer is going to take, but in general Spacer should
        //  not be used in these calculations, otherwise it would always be 1
        // Does not include GridSpacer
        val contentColumns: Int
            get() {
                // children should have our LP
                return children.sumOf {
                    if (it is GridSpacer) {
                        1
                    } else {
                        (it.layoutParams as? LayoutParams)?.spanColumns ?: 1
                    }
                }
            }

        val gridLayout: GridLayout get() = (parent as GridLayout)

        val contentHeight: Int get() = contentHeightMutable

        var contentGravity: Gravity? = null
            set(value) {
                field = value
                requestLayout()
            }

        private var contentHeightMutable = 0
        private val rect = Rect()
        private val cellRect = Rect()

        override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
            return p is LayoutParams
        }

        override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
            return LayoutParams(context, attrs)
        }

        override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
            return LayoutParams(p)
        }

        override fun generateDefaultLayoutParams(): LayoutParams {
            return LayoutParams()
        }

        class LayoutParams : ViewGroup.LayoutParams {
            // fallback to default gravity if specified
            var gravity: Gravity? = null

            // by default span only 1 column
            var spanColumns: Int = 1
                set(value) {
                    // ensure in 0...MAX_VALUE range
                    field = min(max(0, value), Int.MAX_VALUE)
                }

            // by default content is wrap
            constructor() : super(WRAP_CONTENT, WRAP_CONTENT)
            constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
            constructor(width: Int, height: Int) : super(width, height)
            constructor(source: ViewGroup.LayoutParams) : super(source)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            val gridLayout = this.gridLayout
            val columnsCount = gridLayout.columnsCount
                .takeIf { it > 0 }
                ?: return

            val horizontalSpacing = gridLayout.horizontalSpacingPx

            val columnWidth = gridLayout.calculateColumnWidth(
                contentWidth = measuredWidth,
                columnsCount = columnsCount,
                horizontalSpacingPx = horizontalSpacing
            )
            val rowHeight = measuredHeight

            var x = 0

            children
                .withIndex()
                .forEach { (i, child) ->
                    val lp = child.gridRowLayoutParams

                    // 1) see if View itself has gravity defined
                    // 2) see if this row has gravity specified
                    // 3) fallback to default gravity
                    val gravity = lp.gravity
                        ?: contentGravity
                        ?: defaultGravity

                    val spans = lp.spanColumns

                    val cellWidth = cellWidthWithSpanCount(
                        columnWidth = columnWidth,
                        columnSpans = spans,
                        horizontalSpacing = horizontalSpacing
                    )

                    cellRect.set(0, 0, cellWidth, rowHeight)

                    android.view.Gravity.apply(
                        gravity.rawValue,
                        child.measuredWidth,
                        child.measuredHeight,
                        cellRect,
                        rect
                    )

                    if (i != 0) {
                        x += horizontalSpacing
                    }

                    child.layout(
                        x + rect.left,
                        rect.top,
                        x + rect.right,
                        rect.bottom
                    )

                    x += cellWidth
                }
        }

        private fun cellWidthWithSpanCount(
            columnWidth: Int,
            columnSpans: Int,
            horizontalSpacing: Int
        ): Int {
            return (columnWidth * columnSpans) + (horizontalSpacing * (columnSpans - 1))
        }

        private fun measureCell(
            view: View,
            columnWidth: Int,
            horizontalSpacing: Int,
            height: Int
        ) {
            val lp = view.gridRowLayoutParams

            val columnSpans = lp.spanColumns

            val widthSpec = columnSpans.let {
                // if explicit size was specified use it (but still validate that it fits the available width)
                val cellWidth = cellWidthWithSpanCount(columnWidth, it, horizontalSpacing)

                val specSize = if (lp.width > 0) {
                    min(lp.width, cellWidth)
                } else {
                    // else calculate
                    cellWidth
                }

                val specMode = if (lp.width == MATCH_PARENT || lp.width > 0) {
                    MeasureSpec.EXACTLY
                } else {
                    MeasureSpec.AT_MOST
                }

                MeasureSpec.makeMeasureSpec(specSize, specMode)
            }

            val heightSpec = if (lp.height > 0) {
                MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY)
            } else {
                // check if explicit height is present
                if (height > 0) {
                    MeasureSpec.makeMeasureSpec(
                        height,
                        if (lp.height == MATCH_PARENT) {
                            MeasureSpec.EXACTLY
                        } else {
                            MeasureSpec.AT_MOST
                        }
                    )
                } else {
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                }
            }

            view.measure(widthSpec, heightSpec)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val gridLayout = this.gridLayout
            val columnsCount = gridLayout.columnsCount
                .takeIf { it > 0 }
                ?: return super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            val horizontalSpacing = gridLayout.horizontalSpacingPx

            // width is required
            // height must be measured
            val width = MeasureSpec.getSize(widthMeasureSpec)
                .takeIf { it > 0 }
                ?: getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

            val height = MeasureSpec.getSize(heightMeasureSpec)

            val columnWidth = gridLayout.calculateColumnWidth(
                contentWidth = width,
                columnsCount = columnsCount,
                horizontalSpacingPx = horizontalSpacing
            )

            val spacers = mutableListOf<GridSpacer>()
            val fillHeightChildren = mutableListOf<View>()

            var measuredHeight = 0

            children
                .forEach { child ->

                    // measure them afterwards
                    //  they do no affect actual size - they fill all available,
                    //  so we need to measure content first, then provide spacers
                    //  with proper dimensions
                    if (child is GridSpacer) {
                        spacers.add(child)
                        return@forEach
                    }

                    if (child.layoutParams.height == MATCH_PARENT && height == 0) {
                        fillHeightChildren.add(child)
                        return@forEach
                    }

                    measureCell(
                        view = child,
                        columnWidth = columnWidth,
                        horizontalSpacing = horizontalSpacing,
                        height = height
                    )

                    measuredHeight = max(measuredHeight, child.measuredHeight)
                }

            // fill height parent (do not have own size, fills whatever parent has)
            fillHeightChildren.forEach { child ->
                measureCell(
                    view = child,
                    columnWidth = columnWidth,
                    horizontalSpacing = horizontalSpacing,
                    height = measuredHeight
                )
            }

            // iterate over GridSpacers and measure them
            if (spacers.isNotEmpty()) {
                val size = spacers.size
                // Do we actually need to make them uneven?
                //  wouldn't it be just better to use the proper value
                //  even though there might be blank spots not covered by spacer?
                //  well, this one is weird, spacer should fill, no matter, leaving
                //  empty space feels wrong

                // total amount of spacers
                // columns already include spacers, so remove them first
                var totalSpacerSpans = (columnsCount - (contentColumns - size))
                val spacerSpans = totalSpacerSpans / size

                for (i in 0 until size) {
                    spacers[i].gridRowLayoutParams.spanColumns = spacerSpans
                    totalSpacerSpans -= spacerSpans
                }

                // check if there is something left from uneven division
                //  append it to the last one
                if (totalSpacerSpans > 0) {
                    spacers[size - 1].gridRowLayoutParams.spanColumns += (totalSpacerSpans % size)
                }

                // now measure spacers
                spacers.forEach { spacer ->
                    val lp = spacer.layoutParams

                    // spacers are always MATCH_PARENT
                    lp.width = MATCH_PARENT
                    lp.height = MATCH_PARENT

                    // as height use measuredHeight, so spacer fill all available height
                    measureCell(spacer, columnWidth, horizontalSpacing, measuredHeight)
                }
            }

            contentHeightMutable = measuredHeight

            setMeasuredDimension(
                width,
                measuredHeight
            )

            // measurement does not need to measure GridSpacer
            //  (but it would be used in onLayout to layout properly based on measured dimensions)
        }

        private val View.gridRowLayoutParams: LayoutParams get() = this.layoutParams as LayoutParams
    }
}

@Preview
private class PreviewExploreGrid(
    context: Context,
    attrs: AttributeSet?
) : AdaptUIPreviewLayout(context, attrs) {

    lateinit var gridRow: ExploreGrid.GridRowLayout

    @Suppress("FunctionName")
    fun <LP : io.noties.adapt.ui.LayoutParams> ViewFactory<LP>.Cell(text: String) = Text(text)
        .textSize { 21 }
        .textGravity { center }
        .layoutFill()
        .backgroundColor { orange }

    override fun ViewFactory<LayoutParams>.body() {

        if (true) {

            @Suppress("FunctionName")
            fun <LP : io.noties.adapt.ui.LayoutParams> ViewFactory<LP>.Cell(title: String, isBold: Boolean) = VStack {
                Image()
                    .layout(fill, 0, 1F)
                    .background {
                        Rectangle {
                            fill { black.withAlphaComponent(0.1F) }

                            context.getDrawable(R.drawable.sample_avatar_1)
                                ?.let {
                                    Asset(it) {
                                        gravity { center }
                                    }
                                }
                        }
                    }
                Text(title)
                    .layout(fill, wrap)
                    .textSize { 17 }
                    .textMaxLines(3)
                    .onView { it.setLines(3) }
                    .also {
                        if (isBold) {
                            it.textBold()
                        }
                    }
            }.layoutFill()

            Grid {
                for (row in 0 until 4) {
                    GridRow {
                        for (column in 0 until 4) {
                            ZStackSquare {  }.layoutFill()
                        }
                    }.indent()
                        // TODO: investigate the issue, is it checking LP for the height?
                        .layout(fill, 1)
                }
            }.indent()
                .gridSpacing(8)
                .padding(8)
                .gridForeground {
                    View(x = 0 until 1, y = 0 until 1) {
                        Cell("First", false)
                    }
                    View(x = 1 until gridColumnCount, y = 0 until 1) {
                        Cell("Second", true)
                    }
                }
            return
        }

        if (true) {
            Grid {
                val columns = 5
                val rows = 5

                for (row in 0 until rows) {
                    GridRow {
                        for (column in 0 until columns) {
                            ZStackSquare { }.layoutFill()
                        }
                    }.layout(fill, 1)
                }
            }.indent()
                .gridSpacing(2)
                .preview { it.previewBounds() }
                .gridBackground {
                    Shape(
                        x = 0..2,
                        y = 0..2
                    ) {
                        Rectangle {
                            fill { orange.withAlphaComponent(0.2F) }
                        }
                    }

                    Shape(
                        x = 2..4,
                        y = 0..2
                    ) {
                        Rectangle {
                            fill { primary.withAlphaComponent(0.2F) }
                        }
                    }

                    Shape(
                        x = 0..2,
                        y = 2..4
                    ) {
                        Rectangle {
                            fill { black.withAlphaComponent(0.2F) }
                        }
                    }

                    Shape(
                        x = 2..4,
                        y = 2..4
                    ) {
                        Rectangle {
                            fill { accent.withAlphaComponent(0.2F) }
                        }
                    }
                }

            return
        }

        if (true) {
            Grid {

                // maximum possible
                val rows = 6
                val columns = 7

                // TODO: should overlay entries be less than cells requested?
                //  and gravity added?

                // TODO: calendar for a month
                for (row in 0 until rows) {
                    GridRow {
                        for (column in 0 until columns) {
                            ZStackSquare { }
                                .layoutFill()
                        }
                    }.indent()
                        // TODO: somehow without it square is not rendered, it is measured wrong..
                        //  it should be the same, or 0-UNSPECIFIED affects it somehow?
                        .layout(fill, 1)
                }

            }.indent()
                .gridSpacing(4)
                .preview { it.previewBounds() }
            return
        }

        if (true) {
            Grid {
                // here we just define cell structure
                // actual views are added to overlay
                val columns = 4
                val rows = 3

                for (row in 0 until rows) {
                    GridRow {
                        for (column in 0 until columns) {
                            View()
                                .layout(fill, 64)
                        }
                    }
                }
            }.indent()
                .gridSpacing(8)
                .preview { it.previewBounds() }
                .gridBackground {

                    Shape(
                        x = 0..3,
                        y = 0 until 1
                    ) {
                        Rectangle().fill { orange }
                    }

                    Shape(
                        x = 0..1,
                        y = 1..2
                    ) {
                        Rectangle().fill { orange }
                    }

                    Shape(
                        x = 2..3,
                        y = 1 until 2
                    ) {
                        Rectangle().fill { orange }
                    }

                    Shape(
                        x = 2 until 3,
                        y = 2 until 3
                    ) {
                        Rectangle().fill { orange }
                    }

                    Shape(
                        x = 3 until 4,
                        y = 2 until 3
                    ) {
                        Rectangle().fill { orange }
                    }

                }
            return
        }

        if (true) {
            Grid {

                // here we just define cell structure
                // actual views are added to overlay
                val columns = 4
                val rows = 3

                for (row in 0 until rows) {
                    GridRow {
                        for (column in 0 until columns) {
                            View()
                                .layout(fill, 64)
                        }
                    }
                }

            }.indent()
                .gridSpacing(64)
                .gridForeground {

                    View(
                        x = 0..3,
                        y = 0..0
                    ) {
                        Cell("First")
                    }

                    View(
                        x = 0..1,
                        y = 1..2
                    ) {
                        Cell("Second")
                    }

                    View(
                        x = 2..4,
                        y = 1..1
                    ) {
                        Cell("Third")
                    }

                    View(
                        x = 2..2,
                        y = 3..3
                    ) {
                        Cell("4")
                    }

                    View(
                        x = 3..3,
                        y = 3..3
                    ) {
                        Cell("5")
                    }
                }
                .preview {
                    it.previewBounds()
                }
            return
        }

        if (true) {
            Grid {
                GridRow {
                    GridSpacer()
                    Text("Header")
                        .textGravity { center }
                        .backgroundColor { hex("#ccc") }
                        .padding(16)
                        .layout(fill, wrap)
                        .gridColumns(2)
                }
                GridRow {
                    Text("Sidebar")
                        .layout(fill, wrap)
                        .backgroundColor { hex("#333") }
                        .padding(16)
                        .textGravity { center }
                    Text("Main content")
                        .layout(fill, wrap)
                        .backgroundColor { hex("#eee") }
                        .padding(16)
                        .textGravity { center }
                        .gridColumns(2)
                }
                Text("Footer")
                    .layout(fill, wrap)
                    .padding(16)
                    .backgroundColor { hex("#999") }
            }
            return
        }

        Grid {

            GridRow {

                VStack {
                    Spacer()
                    Button("+", isAdd = true)
                    Spacer()
                    Button("-", isAdd = false)
                    Spacer()
                }.indent()
                    .layoutFill()

                Text("1")
                    .layoutWrap()
                    .backgroundColor { accent }
                    .textSize { 48 }
                    .padding(16)
                    .gridColumns(2)
                    .onElementView { el ->
                        el.onClick {
                            TransitionManager.beginDelayedTransition(el.view.parent.parent as ViewGroup)
                            (el.view.parent as ViewGroup).removeView(el.view)
                        }
                    }
                Text("2")
                    .layoutWrap()
                    .backgroundColor { orange }
                    .textSize { 76 }
                    .textBold()
                    .padding(24)
                    .gridColumns(2)
                ZStack {
                    Text("3")
                        .layoutWrap()
                        .backgroundColor { primary }
                        .textSize { 16 }
                        .textBold()
                        .padding(8)
                }.indent()
                    .layoutFill()
                    .gridColumns(4)
                    .backgroundColor { yellow.withAlphaComponent(0.2F) }
            }.indent()
                .reference(::gridRow)

            View()
                .layout(12, 8)
                .backgroundColor { black }

            GridRow {
                View().layout(fill, 48).backgroundColor { orange }
                GridSpacer()
                View().layout(fill, 48).backgroundColor { orange }
//                GridSpacer()
//                View().layout(fill, 48)
//                    .backgroundColor { primary }
                GridSpacer()
//                View().layout(fill, 48).backgroundColor { orange }
            }

            View()
                .layout(12, 8)
                .backgroundColor { black }

            GridRow {
                GridSpacer()
                Text("x")
                    .textSize { 56 }
                GridSpacer()
                    .backgroundColor { accent }
                View()
                    .layoutFill()
                    .backgroundColor { Color.CYAN }
                    .gridColumns(2)
                GridSpacer()
            }

            GridRow {
                repeat(9) {
                    Text(it.toString())
                        .padding(4)
                        .textGravity { center }
                        .layoutFill()
                        .backgroundColor {
                            listOf(
                                primary,
                                accent,
                                black,
                                yellow
                            ).random()
                        }
                }
//                View().layout(0, 48)
            }.layout(fill, 48)

        }.indent()
            .gridSpacing(vertical = 24, horizontal = 8)
            .layoutFill()
            .padding(leading = 8, top = 16, trailing = 8, bottom = 32)
            .preview {
                it
                    .previewBounds()
//                    .backgroundColor { black.withAlphaComponent(0.2F) }

            }
//            .onView {
//                it.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//                    println(":[addOnLayoutChangeListener left:$left top:$top right:$right bottom:$bottom")
//                }
//            }
//            .onView {
//                val overlay = it.gridBackground
//                overlay.setBackgroundColor(Color.BLACK)
//            }
            .gridBackground {
                View(
                    x = 0..5,
                    y = 0..3
                ) {
                    View().layoutFill().backgroundColor { orange }
                }

                Shape(
                    x = 4..8,
                    y = 1..5
                ) {
                    RoundedRectangle(12) {
                        fill { accent }
                    }
                }
            }
            .gridForeground {
                Shape(
                    x = 2..3,
                    y = 2..3
                ) {
                    Oval { fill { hex("#F00") } }
                }
            }
    }

    @Suppress("FunctionName")
    fun <LP : MarginLayoutParams> ViewFactory<LP>.Button(
        label: String,
        isAdd: Boolean
    ) = Text(label)
        .layoutMargin(4)
        .padding(4)
        .backgroundColor { white }
        .textColor { text }
        .textGravity { center }
        .foregroundDefaultSelectable()
        .onClick {
            val parent = gridRow

            if (!isAdd) {
                // remove last one if present
                if (parent.childCount > 1) {
                    TransitionManager.beginDelayedTransition(parent.gridLayout)
                    parent.removeViewAt(parent.childCount - 1)
                }
            } else {
                val view = ViewFactory.createView(parent.context) {
                    Text(parent.childCount.toString())
                        .textGravity { center }
                        .textSize { 21 }
                        .layout(
                            Random.nextInt(48, 480),
                            Random.nextInt(48, 128)
                        )
                        .backgroundColor {
                            listOf(Color.BLUE, Color.CYAN, Color.MAGENTA, Color.GREEN).random()
                        }
                }
                TransitionManager.beginDelayedTransition(parent.gridLayout)
                parent.addView(view)
            }
        }
}