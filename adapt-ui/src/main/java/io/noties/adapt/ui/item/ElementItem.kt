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
        val view = ViewFactory.newView(parent)
            .layoutParams(createLayoutParams(parent))
            // NB! we might create layout-params, but they might not be the ones that
            //  parent uses, but we could have some customization rely on certain layouts,
            //  this is why it is better to postpone rendering until view is properly attached
            // UPD, we actually cannot do it like this. Items assumed to be ready after this
            //  function exits, so caller might immediately call `bind`. For example,
            //  when there is a reference with lateinit properties, they would not be initialized
            //  and this would cause a crash (as reference assigns view in onView callback
            //  which would not be triggered until view is attached)
//            .renderOnAttach()
            .create(ref) { body(it) }
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