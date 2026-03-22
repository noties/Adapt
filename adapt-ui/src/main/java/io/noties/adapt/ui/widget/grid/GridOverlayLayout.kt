package io.noties.adapt.ui.widget.grid

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.element.grid.GridOverlayViewFactory
import io.noties.adapt.ui.util.children

class GridOverlayLayout(context: Context) : ViewGroup(context) {
    init {
        clipChildren = false
        clipToPadding = false
    }

    // Changes to overlay should be reflected here, but parent should not invalidated (layout)
    //  we could set a flag GridLayout (skipNextRequestLayout), but then this view is not
    //  being rendered properly (no changes are visible)

    private val gridLayout: GridLayout get() = parent as GridLayout

    fun updateView(
        view: View,
        x: GridOverlaySpanBuilder? = null,
        y: GridOverlaySpanBuilder? = null,
    ) {
        if (view.parent != this) {
            error("Provided view is not child of this overlay:$view")
        }

        // can the LP be missing if it already has us as parent?
        val lp = (view.layoutParams as LayoutParams)

        x?.also { lp.horizontalSpan = it }
        y?.also { lp.verticalSpan = it }

        // trigger update
        view.requestLayout()
    }

    fun addViews(block: GridOverlayViewFactory.() -> Unit) {
        GridOverlayViewFactory.addChildren(
            this,
            block
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val gridLayout = this.gridLayout

        val columnWidth = gridLayout.columnWidth

        val horizontalSpacing = gridLayout.horizontalSpacingPx
        val verticalSpacing = gridLayout.verticalSpacingPx

        fun skipMeasure(view: View) {
            view.measure(SPEC_NOOP, SPEC_NOOP)
        }

        children.forEach { view ->
            val lp = view.overlayLayoutParams

            // por favor.. draw-allocation
            @SuppressLint("DrawAllocation")
            val info = GridOverlayInfo(
                columns = gridLayout.columnsCount,
                rows = gridLayout.rowsCount
            )

            val (startX, endX) = verifyAxisHorizontal(info, lp.horizontalSpan)
                .let { it ?: return@forEach skipMeasure(view) }
            val (startY, endY) = verifyAxisVertical(info, lp.verticalSpan)
                .let { it ?: return@forEach skipMeasure(view) }

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

    private fun verifyAxisHorizontal(
        info: GridOverlayInfo,
        axis: GridOverlaySpanBuilder?
    ): IntRange? {
        if (axis == null) {
            return null
        }

        val range = verifyAxisRange(axis(GridOverlaySpanFactory, info))
            ?: return null

        val last = range.last.coerceAtMost(gridLayout.lastColumnIndex)
        return IntRange(
            // start cannot be less than 0
            start = range.first.coerceIn(0..last),
            // end cannot be more than columns
            endInclusive = last
        )
    }

    private fun verifyAxisVertical(
        info: GridOverlayInfo,
        axis: GridOverlaySpanBuilder?
    ): IntRange? {
        if (axis == null) {
            return null
        }

        val range = verifyAxisRange(axis(GridOverlaySpanFactory, info))
            ?: return null

        val last = range.last.coerceAtMost(gridLayout.lastRowIndex)
        return IntRange(
            start = range.first.coerceIn(0..last),
            endInclusive = last
        )
    }

    private fun verifyAxisRange(range: IntRange): IntRange? {
        return range
            .takeIf {
                // ensures size of at least 1
                it.first <= it.last
            }
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

        var horizontalSpan: GridOverlaySpanBuilder? = null
        var verticalSpan: GridOverlaySpanBuilder? = null

        constructor() : super(MATCH_PARENT, MATCH_PARENT)
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

    private companion object {
        val SPEC_NOOP = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
    }
}