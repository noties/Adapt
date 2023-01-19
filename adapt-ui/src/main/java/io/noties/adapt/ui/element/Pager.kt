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

/**
 * @see ViewPager
 */
@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Pager(
    children: ViewFactory<ViewPagerLayoutParams>.() -> Unit
): ViewElement<ViewPager, LP> {
    return ViewElement<ViewPager, LP> { context ->
        ElementViewFactory.Pager(context).also { vp ->

            val factory = ViewFactory<ViewPagerLayoutParams>(vp)
            children(factory)

            // layout blocks must be evaluated before adding to the view and to our constructed
            //  params (otherwise view-pager would create a default ones)

            // each entry becomes an item
            // remove decor views
            val (decorItems, items) = factory.useElements()
                .map {
                    @Suppress("UNCHECKED_CAST")
                    val element = (it as ViewElement<out View, ViewPagerLayoutParams>)
                    val lp = ViewPagerLayoutParams()
                    lp.viewPager = vp
                    // sync provided values with our layout params, do not call `render` here
                    // not _new api_, kotlin version which has the same name
                    //noinspection NewApi
                    element.layoutParamsBlocks.forEach { it(lp) }
                    PagerItem(element, lp)
                }
                .partition { it.layoutParams.isDecor }

            // add decor views
            val decorListeners = decorItems
                .onEach { item ->
                    val decorView = item.element.init(context)
                    decorView.layoutParams = item.layoutParams
                    vp.addView(decorView)
                    item.element.render()

                    // view-pager instance is initialized with onLayout, which take
                    //  value from layout-params

                    // decor view cannot be selected as a page
                    item.layoutParams.onPageSelectedListener = null
                }
                .mapNotNull {
                    it.layoutParams.onPageChangeListener
                }

            val adapter = PagerElementAdapter(items)
            vp.adapter = adapter

            // trigger registered view page change listeners after adapter is initialized
            //noinspection NewApi
            decorListeners.forEach { listener ->
                listener
                    .also { it.onPageSelected(vp.currentItem) }
                    .also { vp.addOnPageChangeListener(it) }
            }
        }
    }.also { add(it) }
}

typealias ViewPagerOnPageSelectedListener = (selected: Boolean) -> Unit

/**
 * Special [ViewPager.OnPageChangeListener] that additionally holds ViewPager
 * which simplifies the retrieval of adapter and specifically total pages count
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
 * Extension function to obtain containing ViewPager.
 * NB! This method return [ViewPager] when element has went through
 * view creation
 * ```kotlin
 * Pager {
 *   Text()
 *     .pagerDecor()
 *     .also { element ->
 *       element.onView {
 *         val vp: ViewPager = element.viewPager
 *       }
 *     }
 * }
 * ```
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
) = onView {
    it.setCurrentItem(itemPosition, smoothScroll)
}

/**
 * @see ViewPager.setPageTransformer
 */
fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerPageTransformer(
    reverseDrawingOrder: Boolean = false,
    pageLayerType: Int = View.LAYER_TYPE_HARDWARE,
    transformer: ViewPager.PageTransformer?
) = onView {
    it.setPageTransformer(reverseDrawingOrder, transformer, pageLayerType)
}

/**
 * @see ViewPager.setOffscreenPageLimit
 */
fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerOffscreenPageLimit(
    limit: Int = 1
) = onView {
    it.offscreenPageLimit = limit
}

/**
 * @see ViewPager.setPageMargin
 * @see ViewPager.setPageMarginDrawable
 */
fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerPageMargin(
    margin: Int,
    marginDrawable: Drawable? = null
) = onView {
    it.pageMargin = margin.dip
    it.setPageMarginDrawable(marginDrawable)
}

/**
 * @see ViewPager.setPageMargin
 * @see ViewPager.setPageMarginDrawable
 */
fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerPageMargin(
    margin: Int,
    marginShape: Shape
) = pagerPageMargin(margin, marginShape.newDrawable())


@JvmName("pagerOnPageChangeListenerViewPager")
fun <V : ViewPager, LP : LayoutParams> ViewElement<V, LP>.pagerOnPageChangedListener(
    onPageChangeListener: ViewPagerOnPageChangeListener
) = onView {
    onPageChangeListener.viewPager = it
    // deliver initial result, if we have adapter
    if (it.adapter != null) {
        onPageChangeListener.onPageSelected(it.currentItem)
    }
    it.addOnPageChangeListener(onPageChangeListener)
}

/**
 * Marks a child of [ViewPager] as decor view (persist between pages)
 */
fun <V : View> ViewElement<V, ViewPagerLayoutParams>.pagerDecor(
    gravity: Gravity? = null
) = onLayoutParams { lp ->
    lp.isDecor = true
    gravity?.also { lp.gravity = it.value }
}

/**
 * Specifies with width ratio a page should take
 * @see [PagerAdapter.getPageWidth]
 */
fun <V : View> ViewElement<V, ViewPagerLayoutParams>.pagerPageWidthRatio(
    @FloatRange(from = 0.0, to = 1.0) ratio: Float?
) = onLayoutParams { lp ->
    ratio?.also { lp.pageWidthRatio = it }
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
) = onLayoutParams { lp ->
    lp.onPageChangeListener = onPageChangeListener?.also {
        it.viewPager = lp.viewPager
    }
}

fun <V : View> ViewElement<V, ViewPagerLayoutParams>.pagerOnPageSelectedListener(
    onPageSelectedListener: ViewPagerOnPageSelectedListener?
) = onLayoutParams {
    it.onPageSelectedListener = onPageSelectedListener
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

                item.layoutParams.onPageChangeListener?.also { vp.addOnPageChangeListener(it) }

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

        // should unregister on-page-changed-listener?
        //  as we do not recreate view, maybe keep it
    }

    // page width
    override fun getPageWidth(position: Int): Float {
        return items[position].layoutParams.pageWidthRatio ?: 1F
    }
}