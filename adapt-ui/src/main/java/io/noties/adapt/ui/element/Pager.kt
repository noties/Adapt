package io.noties.adapt.ui.element

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip

@Suppress("FunctionName", "unused")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Pager(
    children: ViewFactory<ViewPagerLayoutParams>.() -> Unit
): ViewElement<ViewPager, LP> {
    return ViewElement<ViewPager, LP> { context ->
        ViewPager(context).also { vp ->

            val factory = ViewFactory<ViewPagerLayoutParams>(context)
            children(factory)

            // layout blocks must be evaluated before adding to the view and to our constructed
            //  params (otherwise view-pager would create a default ones)

            // each entry becomes an item
            // remove decor views
            val (decorItems, items) = factory.elements
                .map {
                    @Suppress("UNCHECKED_CAST")
                    val element = (it as ViewElement<out View, ViewPagerLayoutParams>)
                    val lp = ViewPagerLayoutParams()
                    lp.viewPager = vp
                    // sync provided values with our layout params, do not call `render` here
                    element.layoutBlocks.forEach { it(lp) }
                    PagerItem(element, lp)
                }
                .partition { it.layoutParams.isDecor }

            // add decor views
            decorItems.forEach { item ->
                val decorView = item.element.init(context)
                decorView.layoutParams = item.layoutParams
                vp.addView(decorView)
                item.element.render()

                // view-pager instance is initialized with onLayout, which take
                //  value from layout-params
                item.layoutParams.onPageChangeListener?.also(vp::addOnPageChangeListener)

                // decor view cannot be selected as a page
                item.layoutParams.onPageSelectedListener = null
            }

            val adapter = PagerElementAdapter(items)
            vp.adapter = adapter
        }
    }.also(elements::add)
}

typealias ViewPagerOnPageSelectedListener = (selected: Boolean) -> Unit

/**
 * Special [ViewPager.OnPageChangeListener] that additionally holds ViewPager
 * which simplifies the retrieval of adapter and total pages count
 *
 * @since $UNRELEASED;
 */
abstract class ViewPagerOnPageChangeListener : ViewPager.OnPageChangeListener {

    lateinit var viewPager: ViewPager

    val pagesCount: Int get() = viewPager.adapter?.count ?: 0

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) = Unit

    override fun onPageSelected(position: Int) = Unit
    override fun onPageScrollStateChanged(state: Int) = Unit
}

/**
 * Special [ViewPager.LayoutParams] which allows additional customization of pages
 *
 * @since $UNRELEASED;
 */
class ViewPagerLayoutParams : ViewPager.LayoutParams() {
    lateinit var viewPager: ViewPager

    var pageWidthRatio: Float? = null

    var onPageChangeListener: ViewPagerOnPageChangeListener? = null

    var onPageSelectedListener: ViewPagerOnPageSelectedListener? = null
}

/**
 * Extension function to obtain containing ViewPager
 */
val ViewElement<out View, ViewPagerLayoutParams>.viewPager: ViewPager
    get() = (view.layoutParams as ViewPagerLayoutParams).viewPager

/**
 * Sets currently displayed item
 *
 * @see [ViewPager.setCurrentItem]
 */
fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerCurrentItem(
    itemPosition: Int,
    smoothScroll: Boolean = false,
) = this.onView {
    setCurrentItem(itemPosition, smoothScroll)
}

fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerPageTransformer(
    reverseDrawingOrder: Boolean = false,
    pageLayerType: Int = View.LAYER_TYPE_HARDWARE,
    transformer: ViewPager.PageTransformer?
) = this.onView {
    setPageTransformer(reverseDrawingOrder, transformer, pageLayerType)
}

fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerOffscreenPageLimit(
    limit: Int = 1
) = this.onView {
    offscreenPageLimit = limit
}

fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerPageMargin(
    margin: Int?,
    marginDrawable: Drawable? = null
) = this.onView {
    margin?.dip?.also {
        pageMargin = it
        setPageMarginDrawable(marginDrawable)
    }
}

fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerPageMargin(
    margin: Int?,
    marginShape: Shape? = null
) = pagerPageMargin(margin, marginShape?.drawable())


@JvmName("pagerOnPageChangeListenerViewPager")
fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerOnPageChangedListener(
    onPageChangeListener: ViewPagerOnPageChangeListener?
) = this.onView {
    onPageChangeListener?.also {
        it.viewPager = this
        addOnPageChangeListener(it)
    }
}

/**
 * Marks a child of [ViewPager] as decor view (persist between pages)
 */
fun <V : View> ViewElement<V, ViewPagerLayoutParams>.pagerDecor(
    gravity: Gravity? = null
) = this.onLayout {
    isDecor = true
    gravity?.also { this.gravity = it.gravityValue }
}

/**
 * Specifies with width ratio a page should take
 * @see [PagerAdapter.getPageWidth]
 */
fun <V : View> ViewElement<V, ViewPagerLayoutParams>.pagerPageWidthRatio(
    @FloatRange(from = 0.0, to = 1.0) ratio: Float?
) = this.onLayout {
    ratio?.also { pageWidthRatio = it }
}

/**
 * Registers special instance of [ViewPager.OnPageChangeListener] listener that additionally
 * holds ViewPager adapter, which allows inspection, for example, of total pages count
 *
 * @see ViewPagerOnPageChangeListener
 * @see pagerOnPageSelectedListener to receive a callback when page is selected/unselected
 */
@JvmName("pagerOnPageChangeListenerWithAdapterPage")
fun <V : View> ViewElement<V, ViewPagerLayoutParams>.pagerOnPageChangedListener(
    onPageChangeListener: ViewPagerOnPageChangeListener?
) = this.onLayout {
    this.onPageChangeListener = onPageChangeListener?.also {
        it.viewPager = viewPager
    }
}

fun <V : View> ViewElement<V, ViewPagerLayoutParams>.pagerOnPageSelectedListener(
    onPageSelectedListener: ViewPagerOnPageSelectedListener?
) = this.onLayout {
    this.onPageSelectedListener = onPageSelectedListener
}

internal class PagerItem(
    val element: ViewElement<out View, ViewPagerLayoutParams>,
    val layoutParams: ViewPagerLayoutParams
)

internal class PagerElementAdapter(val items: List<PagerItem>) : PagerAdapter() {

    override fun getCount(): Int = items.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = items[position]
        val element = item.element
        // reuse the view if it has been created
        val view = if (element.isInitialized) {
            element.view
        } else {
            element.init(container.context).also { v ->
                v.layoutParams = item.layoutParams

                val vp = container as ViewPager

                item.layoutParams.onPageChangeListener?.also(vp::addOnPageChangeListener)

                item.layoutParams.onPageSelectedListener?.also { listener ->
                    vp.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                        override fun onPageSelected(p: Int) {
                            listener(position == p)
                        }
                    })
                }
            }
        }
        container.addView(view)
        element.render()
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

        // TODO: should unregister on-page-changed-listener?
    }

    // page width
    override fun getPageWidth(position: Int): Float {
        return items[position].layoutParams.pageWidthRatio ?: 1F
    }
}