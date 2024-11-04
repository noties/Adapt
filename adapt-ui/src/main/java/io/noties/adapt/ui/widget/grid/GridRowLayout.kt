package io.noties.adapt.ui.widget.grid

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.children
import kotlin.math.max
import kotlin.math.min

// accepts content gravity
// NB! ignores padding and layout-params
//  padding is always 0, lp = width=fill/height=wrap (or specific if provided)
class GridRowLayout : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

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
                // space might have _real_ spans set (how much of spans it should take),
                //  which would break most of the calculations (as spacer should not be counted
                //  as content column)
                if (it is GridSpacer) {
                    1
                } else {
                    it.gridRowLayoutParams.spanColumns
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

    init {
        clipChildren = false
        clipToPadding = false
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val gridLayout = this.gridLayout

        val columnsCount = gridLayout.columnsCount
            .takeIf { it > 0 }
            ?: return

        // in case row height is 0, then we do not need to render it,
        //  because it might create visual artifacts with applied gravity
        val rowHeight = measuredHeight
            .takeIf { it > 0 }
            ?: return

        val horizontalSpacing = gridLayout.horizontalSpacingPx

        val columnWidth = gridLayout.calculateColumnWidth(
            contentWidth = measuredWidth,
            columnsCount = columnsCount,
            horizontalSpacingPx = horizontalSpacing
        )

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

                // STOPSHIP: TODO
                println(":[ row.layout rect:${rect.toShortString()}")

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

            val specMode = if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT || lp.width > 0) {
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
                    if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
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

                if (child.layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT && height == 0) {
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

            // TODO: if row contain only spacers (even single spacer, then height is not
            //  measured = 0, and then in layout default center gravity will make it appear
            //  in half from the bottom. Consider falling back to spacer's height if measured height is 0?

            // now measure spacers
            spacers.forEach { spacer ->
                val lp = spacer.layoutParams

                // spacers are always MATCH_PARENT
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT

                // as height use measuredHeight, so spacer fill all available height
                measureCell(spacer, columnWidth, horizontalSpacing, measuredHeight)
            }
        }


        // fill height parent (do not have own size, fills whatever parent has)
        //  measure after spacers, as we might have height reported by them (when there is
        //  no explicit height (or no children that report height) is present
        fillHeightChildren.forEach { child ->
            measureCell(
                view = child,
                columnWidth = columnWidth,
                horizontalSpacing = horizontalSpacing,
                height = measuredHeight
            )
        }

        contentHeightMutable = measuredHeight

        setMeasuredDimension(
            width,
            measuredHeight
        )

        // measurement does not need to measure GridSpacer
        //  (but it would be used in onLayout to layout properly based on measured dimensions)
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

    private val View.gridRowLayoutParams: LayoutParams get() = this.layoutParams as LayoutParams
}