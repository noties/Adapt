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

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder<R> {
        val ref: R = refFactory()
        val view = ViewFactory.createViewWithParams(
            parent.context,
            createLayoutParams(parent),
            ref
        ) { body(it) }
        return Holder(view, ref)
    }

    /**
     * Returned layout-params, even though being generic, work for most of the cases.
     * A view-group would check if it is matching own layout-params and generate a
     * new instance based on that. When an [ElementItem] is known to be used in certain
     * context, a specific instance of layout-params can be returned here.
     *
     * There is a ViewGroup extension `createLayoutParams` that allows generating
     * proper layout-params. It performs slightly worse than using a simple generic layout params,
     * but allows creating specific to parent LayoutParams, which would be available
     * for customization right away
     */
    protected open fun createLayoutParams(parent: ViewGroup): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}