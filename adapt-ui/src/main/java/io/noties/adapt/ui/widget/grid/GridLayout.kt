package io.noties.adapt.ui.widget.grid

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.R
import io.noties.adapt.ui.util.addOnHierarchyChangeListener
import io.noties.adapt.ui.util.children

class GridLayout : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    companion object {
        const val OVERLAY_PRIORITY_BACKGROUND = Int.MIN_VALUE
        const val OVERLAY_PRIORITY_NONE = 0
        const val OVERLAY_PRIORITY_FOREGROUND = Int.MAX_VALUE

        private val OVERLAY_TAG_ID get() = R.id.adaptui_grid_overlay_priority
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

    val columnsCount: Int get() = columnsCountMutable
    private var columnsCountMutable: Int = 1

    // calculate only actual rows, ignore overlays
    val rowsCount: Int get() = rows.size

    val rows: List<View> get() = rowsMutable
    private val rowsMutable = mutableListOf<View>()

    val columnWidth: Int get() = columnWidthMutable
    private var columnWidthMutable: Int = 0

    val rowHeights: List<Int> get() = rowHeightsMutable
    private val rowHeightsMutable = mutableListOf<Int>()


    val lastColumnIndex get() = columnsCount - 1

    val lastRowIndex get() = rowsCount - 1


    val overlays: List<GridOverlayLayout> get() = overlaysMutable
    private val overlaysMutable = mutableListOf<GridOverlayLayout>()

    // predefined overlay for background
    val gridBackground: GridOverlayLayout get() = getOrCreateOverlay(OVERLAY_PRIORITY_BACKGROUND)

    // predefined overlay for foreground
    val gridForeground: GridOverlayLayout get() = getOrCreateOverlay(OVERLAY_PRIORITY_FOREGROUND)

    private val drawingOrder = mutableMapOf<Int, Int>()

    init {
        // required in order to customize drawing order
        //  we need it right now to properly position overlays
        isChildrenDrawingOrderEnabled = true

        clipChildren = false
        clipToPadding = false

        // this must be done in onAttache, otherwise when view is attached/detached it would loose
        // this crucial config
    }

    private fun onChildAdded(child: View) {
        if (child is GridRowLayout) {
            child.addOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View?, child: View) {
                    invalidateColumnsCount()
                    requestLayout()
                }

                override fun onChildViewRemoved(parent: View?, child: View) {
                    invalidateColumnsCount()
                    requestLayout()
                }
            })
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        children.forEach { onChildAdded(it) }

        // NB! rows must be invalidated before columns
        invalidateRowsAndOverlays()
        invalidateColumnsCount()
        invalidateDrawingPositions()

        addOnHierarchyChangeListener(object : OnHierarchyChangeListener {

            override fun onChildViewAdded(parent: View?, child: View) {
                onChildAdded(child)

                val col = columnsCount

                // NB! rows must be invalidated before columns
                invalidateRowsAndOverlays()
                invalidateColumnsCount()
                invalidateDrawingPositions()
                requestLayout()

                // STOPSHIP: TODO
                println(":[ grid.onChildViewAdded columns old:$col new:$columnsCount")
            }

            override fun onChildViewRemoved(parent: View?, child: View) {
                // addOnHierarchyChangeListener automatically manages when detached, so
                //  no need to unregister additionally here

                // STOPSHIP: TODO
                println(":[ grid.onChildViewRemoved")

                // NB! rows must be invalidated before columns
                invalidateRowsAndOverlays()
                invalidateColumnsCount()
                invalidateDrawingPositions()
                requestLayout()
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

    // get drawing order, background - first, overlay - last
    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        return drawingOrder[drawingPosition] ?: run {
            // could happen when empty for example
            drawingPosition
        }
    }

    fun getOrCreateOverlay(priority: Int): GridOverlayLayout {
        return overlays.firstOrNull {
            priority == it.getTag(OVERLAY_TAG_ID)
        } ?: run {
            val view = GridOverlayLayout(context)
            view.setTag(OVERLAY_TAG_ID, priority)
            addView(view)
            view
        }
    }

    fun calculateColumnsCount(): Int {
        return rows.maxOfOrNull {
            (it as? GridRowLayout)?.contentColumns ?: 1
        } ?: 1
    }

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
        return heights.drop(s).take(e - s + 1).sum() + ((e - s) * verticalSpacingPx)
    }

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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val paddingTop = paddingTop
        val paddingLeading = paddingStart

        var y = paddingTop

        rows.forEach { row ->
            row.layout(
                paddingLeading,
                y,
                paddingLeading + row.measuredWidth,
                y + row.measuredHeight
            )
            y += row.measuredHeight + verticalSpacingPx
        }

        // layout overlays after rows
        overlays.forEach { overlay ->
            overlay.layout(
                paddingLeading,
                paddingTop,
                paddingLeading + overlay.measuredWidth,
                paddingTop + overlay.measuredHeight
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // STOPSHIP:
        println(":[ grid.onMeasure columns:$columnsCount")

        val columnsCount = this.columnsCount
            .takeIf { it > 0 }
            ?: return super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // adjust to padding too
        val paddingLeading = paddingStart
        val paddingTrailing = paddingEnd

        val contentWidth = run {
            val w = MeasureSpec.getSize(widthMeasureSpec)
            if (w <= 0) {
                error(
                    "GridLayout requires definite width: match_parent or exact px/dp, " +
                            "wrap_content is not supported"
                )
            }

            val width = w - paddingLeading - paddingTrailing
            val columnWidth = calculateColumnWidth(
                contentWidth = width,
                columnsCount = columnsCount
            ).also {
                columnWidthMutable = it
            }

            // validate that all columns would equal received width
            //  otherwise we need to update it (due to int division, we might
            //  loose some precision, but we should make children of equal
            //  widths)
            calculateCellWidthWithSpan(
                columnsCount,
                columnWidth = columnWidth
            )
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
                val lp = v.gridLayoutParams
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
                //  (so, we extract the padding that was added before)
                MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop, MeasureSpec.EXACTLY)
            )
        }

        setMeasuredDimension(
            contentWidth + paddingLeading + paddingTrailing,
            measuredHeight + paddingBottom
        )
    }

    class LayoutParams : ViewGroup.LayoutParams {
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

    private val View.gridLayoutParams: LayoutParams get() = layoutParams as LayoutParams

    private fun invalidateColumnsCount() {
        columnsCountMutable = calculateColumnsCount()
    }

    private fun invalidateRowsAndOverlays() {
        val (overlays, rows) = children
            .partition { it is GridOverlayLayout }

        overlaysMutable.clear()
        @Suppress("UNCHECKED_CAST")
        overlaysMutable.addAll(overlays as List<GridOverlayLayout>)

        rowsMutable.clear()
        rowsMutable.addAll(rows)
    }

    private fun invalidateDrawingPositions() {
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
}