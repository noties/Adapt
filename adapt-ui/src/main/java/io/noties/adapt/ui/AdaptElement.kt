package io.noties.adapt.ui

import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.Adapt
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewpager.AdaptViewPager

abstract class AdaptElement<A : Adapt> {
    abstract val adapt: A

    val callbacks: MutableList<(A) -> Unit> = mutableListOf()
}

/**
 * AdaptViewGroupElement
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

@JvmName("adaptViewGroup")
fun <V : ViewGroup, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adapt(
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