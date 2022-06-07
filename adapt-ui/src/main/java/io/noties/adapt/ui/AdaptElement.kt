package io.noties.adapt.ui

import android.view.ViewGroup
import io.noties.adapt.Adapt
import io.noties.adapt.viewgroup.AdaptViewGroup

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

fun <V : ViewGroup, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.adapt(
    configurator: (AdaptViewGroup.Configuration) -> Unit = {}
): AdaptElement<AdaptViewGroup> {
    val element = AdaptViewGroupElement(configurator)
    onView(element.onView)
    return element
}