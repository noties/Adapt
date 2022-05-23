package io.noties.adapt.ui.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import io.noties.adapt.Item
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

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
        val factory = ViewFactory<LayoutParams>()
        val references: R = referencesFactory()
        factory.body(references)

        // ensure single element
        if (factory.elements.size != 1) {
            throw IllegalStateException("Unexpected state, `body` must contain exactly one root element")
        }

        @Suppress("UNCHECKED_CAST")
        val root = factory.elements[0] as ViewElement<View, LayoutParams>

        val view = root.provider(parent.context)
        val lp = generateDefaultLayoutParams(parent)

        view.layoutParams = lp

        root.layoutBlocks.forEach { it(lp) }
        root.viewBlocks.forEach { it(view) }

        return Holder(view, references)
    }

    protected open fun generateDefaultLayoutParams(parent: ViewGroup): LayoutParams {
        return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }
}