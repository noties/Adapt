package io.noties.adapt.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

/**
 * A version of vertical `LinearLayout` that measures children based on priority.
 * Normally, layout receives available height dimensions (in the form of $HEIGHT and AT_MOST
 * measure-spec mode). So, in order to not exceed the available in parent height - instead
 * wrap content-view into a scrollable-component (like `ScrollView`) without actually exceeding
 * parent height, but receiving such event to adjust current layout. For this, this layout
 * first measures all _normal_ children occupying all required height. And then passing all rest
 * height to the content child(ren). This way content should not exceed available height, thus growing
 * this whole view (as normally it is allowed with AT_MOST mode) won\'t happen.
 * This layout is intended to be used with the [FrameLayoutWrapHeightOrScroll] which would
 * wrap content into a [ScrollView] when content height is greater than available height.
 *
 * @see io.noties.adapt.ui.element.VStackContentMeasuredLast
 * @see FrameLayoutWrapHeightOrScroll
 */
class LinearLayoutContentMeasuredLast : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        orientation = VERTICAL
    }

    class LayoutParams : LinearLayout.LayoutParams {
        var isContent: Boolean = false

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, weight: Float) : super(width, height, weight)
        constructor(p: ViewGroup.LayoutParams) : super(p)
        constructor(source: MarginLayoutParams) : super(source)
        constructor(source: LinearLayout.LayoutParams) : super(source)
    }

    private companion object {
        val View.isContentView: Boolean
            get() {
                return true == (this.layoutParams as? LayoutParams)?.isContent
            }

        val ViewGroup.children: List<View>
            get() = (0 until childCount)
                .map { getChildAt(it) }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            io.noties.adapt.ui.LayoutParams.MATCH_PARENT,
            io.noties.adapt.ui.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(lp)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // if content is not found -> just super

        val (contentChildren, children) = children
            .partition { it.isContentView }

        if (contentChildren.isEmpty()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        var availableHeight = height - paddingTop - paddingBottom

        fun measureChild(child: View) {
            measureChild(
                child,
                childWidthMeasureSpec(width, child),
                childHeightMeasureSpec(
                    availableHeight - child.heightLayoutMargins()
                )
            )
            availableHeight -= (child.measuredHeight + child.heightLayoutMargins())
        }

        children.forEach { measureChild(it) }
        contentChildren.forEach { measureChild(it) }

        setMeasuredDimension(
            width,
            height - availableHeight
        )
    }

    private fun childWidthMeasureSpec(parentWidth: Int, child: View): Int {
        return MeasureSpec.makeMeasureSpec(
            (parentWidth - paddingLeft - paddingRight - child.widthLayoutMargins()),
            MeasureSpec.EXACTLY
        )
    }

    private fun childHeightMeasureSpec(height: Int): Int {
        return MeasureSpec.makeMeasureSpec(
            height,
            MeasureSpec.EXACTLY
        )
    }

    private fun View.widthLayoutMargins(): Int {
        val lp = this.layoutParams as? LayoutParams
        return if (lp != null) {
            lp.leftMargin + lp.rightMargin
        } else 0
    }

    private fun View.heightLayoutMargins(): Int {
        val lp = this.layoutParams as? LayoutParams
        return if (lp != null) {
            lp.topMargin + lp.bottomMargin
        } else 0
    }

    // pass scroll to content
    override fun canScrollVertically(direction: Int): Boolean {
        return children
            .filter { it.isContentView }
            .any { it.canScrollVertically(direction) }
    }
}