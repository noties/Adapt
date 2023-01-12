package io.noties.adapt.ui.element

import android.view.View
import io.noties.adapt.Item
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.view.AdaptView

// TODO: can we distinguish the factory - when createView or addChildren are created?
//  so, when there is no view-group we would not be able to use Item
// TODO: can we just make group optional in ViewFactory? seems better
fun <LP : LayoutParams> ViewFactory<LP>.Item(
    item: Item<*>
): ViewElement<View, LP> {
    val vf = this
    // TODO: this does not give abiltity to update it, render once when created and that's it
    // TODO: how can we expose underlying AdaptView here? So, it can be updated?
    return ViewElement {
        AdaptView.init(vf.viewGroup, item).view()
    }
}