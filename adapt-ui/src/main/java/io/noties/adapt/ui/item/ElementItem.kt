package io.noties.adapt.ui.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import io.noties.adapt.Item
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.createView

/**
 * @since $UNRELEASED;
 */
abstract class ElementItem<R>(
    id: Long,
    private val referencesFactory: () -> R
) : Item<ElementItem.Holder<R>>(id) {

    class Holder<R>(view: View, val references: R) : Item.Holder(view)

    abstract fun ViewFactory<LayoutParams>.body(references: R)

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder<R> {
        val references: R = referencesFactory()
        val view = ViewFactory.createView(
            parent.context,
            references
        ) { body(it) }
        return Holder(view, references)
    }
}