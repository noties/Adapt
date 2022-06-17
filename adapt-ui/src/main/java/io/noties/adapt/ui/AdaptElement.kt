package io.noties.adapt.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.Adapt
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewpager.AdaptViewPager

abstract class AdaptElement<A : Adapt> {
    abstract val adapt: A

    val callbacks: MutableList<(A) -> Unit> = mutableListOf()
}

/**
 * AdaptViewGroup
 */
class AdaptViewGroupElement(
    private val configurator: (AdaptViewGroup.Configuration) -> Unit
) : AdaptElement<AdaptViewGroup>() {
    override lateinit var adapt: AdaptViewGroup

    val onView: (ViewGroup) -> Unit
        get() = {
            adapt = AdaptViewGroup.init(it, configurator)
            callbacks.onEach { it(adapt) }
        }
}

fun <V : ViewGroup, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptViewGroup(
    configurator: (AdaptViewGroup.Configuration) -> Unit = {}
): AdaptElement<AdaptViewGroup> {
    val element = AdaptViewGroupElement(configurator)
    onView(element.onView)
    return element
}

/**
 * AdaptViewPager
 */
class AdaptViewPagerElement(
    private val configurator: (AdaptViewPager.Configuration) -> Unit
) : AdaptElement<AdaptViewPager>() {
    override lateinit var adapt: AdaptViewPager

    val onView: (ViewPager) -> Unit
        get() = {
            adapt = AdaptViewPager.init(it, configurator)
            callbacks.onEach { it(adapt) }
        }
}

fun <V : ViewPager, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptViewPager(
    configurator: (AdaptViewPager.Configuration) -> Unit = {}
): AdaptElement<AdaptViewPager> {
    val element = AdaptViewPagerElement(configurator)
    onView(element.onView)
    return element
}

/**
 * AdaptRecyclerView
 */
class AdaptRecyclerViewElement(
    private val configurator: (AdaptRecyclerView.Configuration) -> Unit
) : AdaptElement<AdaptRecyclerView>() {
    override lateinit var adapt: AdaptRecyclerView

    val onView: (RecyclerView) -> Unit
        get() = {
            adapt = AdaptRecyclerView.init(it, configurator)
            callbacks.onEach { it(adapt) }
        }
}

fun <V : RecyclerView, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptRecyclerView(
    configurator: (AdaptRecyclerView.Configuration) -> Unit = {}
): AdaptElement<AdaptRecyclerView> {
    val element = AdaptRecyclerViewElement(configurator)
    onView(element.onView)
    return element
}

/**
 * AdaptRecyclerView for ViewPager2
 */
class AdaptViewPager2Element(
    private val configurator: (AdaptRecyclerView.Configuration) -> Unit
) : AdaptElement<AdaptRecyclerView>() {
    override lateinit var adapt: AdaptRecyclerView

    val onView: (ViewPager2) -> Unit
        get() = {
            adapt = AdaptRecyclerView.create(configurator)
            it.adapter = adapt.adapter()

            callbacks.onEach { it(adapt) }
        }
}

// What if they change it to be non-final
@Suppress("FINAL_UPPER_BOUND")
fun <V : ViewPager2, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptViewPager2(
    configurator: (AdaptRecyclerView.Configuration) -> Unit = {}
): AdaptElement<AdaptRecyclerView> {
    val element = AdaptViewPager2Element(configurator)
    onView(element.onView)
    return element
}