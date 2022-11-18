package io.noties.adapt.ui.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import io.noties.adapt.Item
import io.noties.adapt.ui.ViewFactory

/**
 * @since $UNRELEASED;
 */
abstract class ElementItem<R : Any>(
    id: Long,
    private val refFactory: () -> R
) : Item<ElementItem.Holder<R>>(id) {

    class Holder<R>(view: View, val ref: R) : Item.Holder(view)

    abstract fun ViewFactory<LayoutParams>.body(ref: R)

    // TODO: allow providing default layout params
    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder<R> {
        val ref: R = refFactory()
        val view = ViewFactory.createView(
            parent.context,
            ref
        ) { body(it) }
        return Holder(view, ref)
    }
}