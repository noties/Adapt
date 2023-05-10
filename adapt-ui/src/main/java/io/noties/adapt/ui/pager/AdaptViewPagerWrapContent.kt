package io.noties.adapt.ui.pager

import android.content.Context
import android.os.Build
import android.transition.TransitionManager
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element
import io.noties.adapt.viewpager.AdaptViewPager
import kotlin.math.roundToInt

/**
 * NB! in order to function ViewPager must be initialized with AdaptViewPager.init
 * @since $UNRELEASED;
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.AdaptPagerWrapContent(
) = Element { AdaptViewPagerWrapContent(it) }

// view pager is great, if it is added to a scroll view, then the scroll view is not scrolling
/**
 * Requires ViewPager to be initialized with Adapt - [AdaptViewPager.init]
 * @since $UNRELEASED;
 */
class AdaptViewPagerWrapContent(context: Context) : ViewPager(context) {

    interface ChangeHandler {
        // before request layout is called
        fun beforeChange(pager: AdaptViewPagerWrapContent)

        // after request layout is called
        fun afterChange(pager: AdaptViewPagerWrapContent)
    }

    var changeHandler: ChangeHandler = DefaultChangeHandler()

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

        changeHandler.beforeChange(this)
        requestLayout()
        changeHandler.afterChange(this)
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
        // should we bind it here?
        val view = adapt.findViewForAdapterPosition(currentItem) ?: return null

        // render the item before measuring
        adapt.notifyItemChanged(adapt.items()[currentItem])

        val pageWidth = adapt.pagerAdapter().getPageWidth(currentItem)

        // we need to have real width in order to properly calculate height
        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            ((MeasureSpec.getSize(originalWidthSpec) - paddingLeft - paddingRight) * pageWidth).roundToInt(),
            MeasureSpec.EXACTLY
        )
        val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        view.measure(childWidthSpec, childHeightSpec)

        return MeasureSpec.makeMeasureSpec(
            view.measuredHeight + paddingTop + paddingBottom,
            MeasureSpec.EXACTLY
        )
    }

    class DefaultChangeHandler : ChangeHandler {
        override fun beforeChange(pager: AdaptViewPagerWrapContent) {
            val parent = pager.parent as? ViewGroup ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TransitionManager.endTransitions(parent)
            }
            TransitionManager.beginDelayedTransition(parent)
        }

        override fun afterChange(pager: AdaptViewPagerWrapContent) = Unit
    }
}