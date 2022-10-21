package io.noties.adapt.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.view.AdaptView
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewgroup.TransitionChangeHandler
import io.noties.adapt.viewpager.AdaptViewPager

abstract class AdaptElement<A : Adapt> {
    lateinit var adapt: A

    val callbacks: MutableList<(A) -> Unit> = mutableListOf()

    fun init(adapt: A) {
        this.adapt = adapt
        callbacks.forEach { it(adapt) }
        callbacks.clear()
    }
}

/**
 * AdaptView
 */
class AdaptViewElement(
    private val configurator: (AdaptView.Configuration) -> Unit
) : AdaptElement<AdaptView>() {
    val onView: (ViewGroup) -> Unit
        get() = {
            init(AdaptView.init(it, configurator))
        }
}

fun <V : ViewGroup, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptView(
    configurator: (AdaptView.Configuration) -> Unit = {}
): AdaptElement<AdaptView> {
    val element = AdaptViewElement(configurator)
    onView(element.onView)
    return element
}

fun <V : ViewGroup, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptView(
    item: Item<*>
): AdaptElement<AdaptView> = adaptView { it.item(item) }

/**
 * AdaptViewGroup
 */
class AdaptViewGroupElement(
    private val configurator: (AdaptViewGroup.Configuration) -> Unit
) : AdaptElement<AdaptViewGroup>() {
    val onView: (ViewGroup) -> Unit
        get() = {
            init(AdaptViewGroup.init(it, configurator))
        }
}

fun <V : ViewGroup, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptViewGroup(
    configurator: (AdaptViewGroup.Configuration) -> Unit = {}
): AdaptElement<AdaptViewGroup> {
    val element = AdaptViewGroupElement(configurator)
    onView(element.onView)
    return element
}

fun <V : ViewGroup, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adaptViewGroup(
    changeHandler: TransitionChangeHandler
): AdaptElement<AdaptViewGroup> = adaptViewGroup {
    it.changeHandler(changeHandler)
}

/**
 * AdaptViewPager
 */
class AdaptViewPagerElement(
    private val configurator: (AdaptViewPager.Configuration) -> Unit
) : AdaptElement<AdaptViewPager>() {
    val onView: (ViewPager) -> Unit
        get() = {
            init(AdaptViewPager.init(it, configurator))
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
    val onView: (RecyclerView) -> Unit
        get() = {
            init(AdaptRecyclerView.init(it, configurator))
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
    val onView: (ViewPager2) -> Unit
        get() = {
            val adapt = AdaptRecyclerView.create(configurator)
            it.adapter = adapt.adapter()
            init(adapt)
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