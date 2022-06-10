package io.noties.adapt.sample.samples.viewpager

import android.content.Context
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.viewgroup.TransitionChangeHandler
import io.noties.adapt.viewpager.AdaptViewPager

class ViewPagerWrapContent(context: Context) : ViewPager(context) {

    private val handler: TransitionChangeHandler by lazy(LazyThreadSafetyMode.NONE) {
        TransitionChangeHandler.createTransitionOnParent()
    }

    init {
        addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                invalidateHeight()
            }
        })
    }

    fun invalidateHeight(animate: Boolean = true) {
        if (!animate) {
            requestLayout()
            return
        }

        handler.begin(this)
        requestLayout()
        handler.end(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // detect if we have WRAP_CONTENT as the height
        //  if not, just use super
        val spec = heightSpec(widthMeasureSpec, heightMeasureSpec) ?: heightMeasureSpec
        super.onMeasure(widthMeasureSpec, spec)
    }

    private fun heightSpec(originalWidthSpec: Int, originalHeightSpec: Int): Int? {

        val adapt = AdaptViewPager.find(this)
        val heightMode = MeasureSpec.getMode(originalHeightSpec)
        val currentItem = this.currentItem

        if (adapt == null || currentItem < 0 || MeasureSpec.AT_MOST != heightMode) {
            return null
        }

        // obtain current item view
        val view = adapt.findViewForAdapterPosition(currentItem) ?: return null

        // it is mock measure to determine actual size of a child
        val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        view.measure(originalWidthSpec, childHeightSpec)

        return MeasureSpec.makeMeasureSpec(
            view.measuredHeight + paddingTop + paddingBottom,
            MeasureSpec.EXACTLY
        )
    }
}