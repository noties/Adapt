package io.noties.adapt.ui.element

import android.view.LayoutInflater
import android.view.View
import io.noties.adapt.Item
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

// TODO: can we distinguish the factory - when createView or addChildren are created?
//  so, when there is no view-group we would not be able to use Item
fun <LP : LayoutParams> ViewFactory<LP>.Item(
    item: Item<*>
): ViewElement<View, LP> {
    // root of view factory is our parent, but we have no use to it, item requires parent
    return ViewElement {
        val holder = item.createHolder(
            LayoutInflater.from(it),
            null
        )
        holder.itemView()
    }
}