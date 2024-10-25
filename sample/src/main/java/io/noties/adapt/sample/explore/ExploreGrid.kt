package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.explore.ExploreGrid.Grid
import io.noties.adapt.sample.explore.ExploreGrid.GridRow
import io.noties.adapt.sample.explore.ExploreGrid.GridSpacer
import io.noties.adapt.sample.explore.ExploreGrid.gridCellColumns
import io.noties.adapt.sample.explore.ExploreGrid.gridSpacing
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.sample.util.children
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.ElementGroup
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.withAlphaComponent
import kotlin.math.max
import kotlin.math.min

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

    fun <V : View, LP : GridRowLayout.LayoutParams> ViewElement<V, LP>.gridCellColumns(columns: Int) =
        this.onLayoutParams {
            it.spanColumns = columns
        }

    class GridLayout : ViewGroup {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        val columns: Int
            get() = children.maxOf {
                (it as? GridRowLayout)?.columns ?: 1
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
            children.forEach { child ->
                child.layout(
                    paddingStart,
                    y,
                    paddingStart + child.measuredWidth,
                    y + child.measuredHeight
                )
                y += child.measuredHeight + verticalSpacingPx
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

            // adjust to padding too
            val w = MeasureSpec.getSize(widthMeasureSpec)
            if (w <= 0) {
                error("GridLayout requires definite width: match_parent or px/dp")
            }

            val paddingLeading = paddingStart
            val paddingTrailing = paddingEnd

            val contentWidth = w - paddingLeading - paddingTrailing

            val childWidthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY)

//            val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

            var measuredHeight = paddingTop

            // it seems ViewGroup does not measure children by default
            // spacing is applied only between (add always but not first)
            children.withIndex()
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

                    println(":( measure at:$i v:{w:${v.measuredWidth} h:${v.measuredHeight}}")
                }

            setMeasuredDimension(
                w,
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

        override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
            return LayoutParams(context, attrs)
        }

        override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
            return LayoutParams(p)
        }

        override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
            return LayoutParams()
        }
    }

    // TODO: make it take proper values (fill width and height)
    // TODO: make it view-group, so space be filled with other views
    class GridSpacer : View {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            // never take any dimension
            setMeasuredDimension(0, 0)
        }
    }

    // accepts content gravity
    // NB! ignores padding and layout-params
    //  padding is always 0, lp = fill/wrap
    // TODO: allow specifying height
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

        val columns: Int
            get() {
                // children should have our LP
                return children
                    .map { it.layoutParams as LayoutParams }
                    .sumOf { it.spanColumns }
            }

        // total amount of columns that should be reported by parent
        val totalColumns: Int
            get() = (parent as? GridLayout)?.columns ?: 0

        val contentHeight: Int get() = contentHeightMutable

        // center by default (h + v)
        var contentGravity: Gravity = defaultGravity

        private var contentHeightMutable = 0
        private val rect = Rect()
        private val cellRect = Rect()

        override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
            return p is LayoutParams
        }

        override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
            return LayoutParams(context, attrs)
        }

        override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
            return LayoutParams(p)
        }

        override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
            return LayoutParams()
        }

        class LayoutParams : ViewGroup.LayoutParams {
            var gravity: Gravity = defaultGravity

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

        private data class LayoutInfo(
            val totalAvailableWidth: Int,
            val totalColumns: Int,
            val rowColumns: Int,
            val horizontalSpacing: Int,
            val columnWidth: Int
        )

//        private fun layoutInfo(width: Int): LayoutInfo? {
//            val totalColumns = this.totalColumns
//                .takeIf { it > 0 }
//                ?: return null
//            val horizontalSpacing = (parent as? GridLayout)?.horizontalSpacingPx ?: 0,
//            return LayoutInfo(
//                totalAvailableWidth = width,
//                totalColumns = totalColumns,
//                rowColumns = this.columns,
//                horizontalSpacing = horizontalSpacing,
//                columnWidth = (width - (horizontalSpacing * (totalColumns - 1))) / totalColumns
//            )
//        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//            val layoutInfo = layoutInfo(measuredWidth) ?: return
            
            val totalColumns = this.totalColumns
                .takeIf { it > 0 }
                ?: return

            val columns = this.columns

            val horizontalSpacing = (parent as GridLayout).horizontalSpacingPx

            val cellWidth =
                (measuredWidth - (horizontalSpacing * (totalColumns - 1))) / totalColumns
            val cellHeight = measuredHeight
            cellRect.set(0, 0, cellWidth, cellHeight)

            var x = 0

            // integer rounding is applied, spacings should try to be even for proper visual representation
            val spacerSpans = children
                .mapNotNull { it as? GridSpacer }
                .map { 1 }
                // calculate difference in space (that we need to fill)
                .sum().let {
                    if (it > 0) {
                        // each spacer is always occupying a column span, thus remove them from our counted columns
                        (totalColumns - (columns - it)) / it
                    } else {
                        0
                    }
                }

            // TODO: at the end, if spacerSpans* spansLeft > 0,
            //  then take last grid-spacer and add 1 to it (well
            //  the difference, but it should be just 1)

            println(":(layout cell{w:$cellWidth h:$cellHeight} totalColumns:$totalColumns columns:$columns spacerSpans:$spacerSpans")

            children
                .withIndex()
                .forEach { (i, child) ->
                    val isGridSpacer = child is GridSpacer
                    val lp = child.layoutParams as LayoutParams

                    val gravity = lp.gravity

                    val spans = if (isGridSpacer) {
                        spacerSpans
                    } else {
                        lp.spanColumns
                    }

                    val childCellWidth =
                        (spans * cellWidth) + (horizontalSpacing * (spans - 1))
                    val childContentWidth =
                        if (child is GridSpacer) childCellWidth else {
                            if (lp.width == MATCH_PARENT) {
                                childCellWidth
                            } else {
                                child.measuredWidth
                            }
                        }
                    val childContentHeight = if (lp.height == MATCH_PARENT || child is GridSpacer) {
                        cellHeight
                    } else {
                        child.measuredHeight
                    }

                    cellRect.right = childCellWidth

                    println(":( spans:$spans measured:{w:${child.measuredWidth} h:${child.measuredHeight}}, cell:${cellRect.toShortString()} gravity:$gravity")
                    println(":[ child{w:$childContentWidth h:$childContentHeight} cellRect:${cellRect.toShortString()}")

                    android.view.Gravity.apply(
                        gravity.rawValue,
                        childContentWidth,
                        childContentHeight,
                        cellRect,
                        rect
                    )
                    println(":( x:$x rect:${rect.toShortString()}")

                    if (i != 0) {
                        x += horizontalSpacing
                    }

                    child.layout(
                        x + rect.left,
                        rect.top,
                        x + rect.right,
                        rect.bottom
                    )
                    x += childCellWidth
                }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val totalColumns = this.totalColumns
                .takeIf { it > 0 }
                ?: run {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                    return
                }

            val horizontalSpacing = (parent as GridLayout).horizontalSpacingPx

            // width is required
            // height must be measured
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = MeasureSpec.getSize(heightMeasureSpec)

            // calculate available width for a cell
            val columnWidth = (width - (horizontalSpacing * (totalColumns - 1))) / totalColumns

            println(":[onMeasure total:$totalColumns columns:$columns width:$width spacing:$horizontalSpacing cell:$columnWidth")

//            val columnWidthSpec = MeasureSpec.makeMeasureSpec(
//                columnWidth,
//                MeasureSpec.AT_MOST
//            )

            var measureHeight = 0

            children
                .forEach { child ->
                    val lp = child.layoutParams as LayoutParams

                    val widthSpec = lp.spanColumns.let {
                        // if explicit size was specified use it (but still validate that it fits the available width)
                        val size = (columnWidth * it) + (horizontalSpacing * (it - 1))
                        val specSize = if (lp.width > 0) {
                            min(lp.width, size)
                        } else {
                            // else calculate
                            size
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
                    child.measure(widthSpec, heightSpec)
                    println("☺\uFE0F spans:${lp.spanColumns} spec:${(columnWidth * lp.spanColumns) + (horizontalSpacing * (lp.spanColumns - 1))} measured:{w:${child.measuredWidth} h:${child.measuredHeight}}")
                    measureHeight = max(measureHeight, child.measuredHeight)
                }

            contentHeightMutable = measureHeight

            setMeasuredDimension(
                width,
                measureHeight
            )

            // measurement does not need to measure GridSpacer
            //  (but it would be used in onLayout to layout properly based on measured dimensions)
        }
    }
}

@Preview
private class PreviewExploreGrid(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        Grid {

            GridRow {
                Text("1")
                    .layoutWrap()
                    .backgroundColor { accent }
                    .textSize { 48 }
                    .padding(16)
                    .gridCellColumns(2)
                Text("2")
                    .layoutWrap()
                    .backgroundColor { orange }
                    .textSize { 76 }
                    .textBold()
                    .padding(24)
                    .gridCellColumns(2)
                ZStack {
                    Text("3")
                        .layoutWrap()
                        .backgroundColor { primary }
                        .textSize { 16 }
                        .textBold()
                        .padding(8)
                }.indent()
                    .layoutFill()
                    .gridCellColumns(4)
                    .backgroundColor { yellow.withAlphaComponent(0.2F) }
            }.noClip()

            View()
                .layout(12, 8)
                .backgroundColor { black }

            GridRow {
                View().layout(fill, 48).backgroundColor { orange }
                GridSpacer()
                View().layout(fill, 48).backgroundColor { orange }
                GridSpacer()
                View().layout(fill, 48)
                    .backgroundColor { primary }
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
                    .gridCellColumns(2)
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
                            ).random().withAlphaComponent(0.2F)
                        }
                }
//                View().layout(0, 48)
            }.layout(fill, 48)

        }.indent()
//            .gridSpacing(vertical = 24, horizontal = 8)
            .layoutFill()
            .padding(leading = 8, top = 16, trailing = 24, bottom = 32)
            .preview {
                it
                    .previewBounds()
                    .backgroundColor { black.withAlphaComponent(0.2F) }

            }

    }
}