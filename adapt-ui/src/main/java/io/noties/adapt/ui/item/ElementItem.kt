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

        val view = ViewFactory.createView(parent) {
            body(ref)
        }

        // okay, now, if layout-params are empty we fallback to default ones
        //  this should be very rarely used if at all, provide layout-params during view initialization
        // This must not happen ever, as factory would assign default LP if none was
        //  defined by view itself. Right now UI allows specifying LP which would be applied
        //  before all other layout blocks and assign specified LP to view before attachment
        //@Deprecated("Once createLayoutParams are removed, remove this one also")
        // block cannot be deprecated in kotlin :'(
        if (view.layoutParams == null) {
            view.layoutParams = createLayoutParams(parent)
        }

        return Holder(view, ref)
            .also { onRefReady(ref) }
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
    @Deprecated(
        "ViewFactory by default assigns MATCH|WRAP on view creation, " +
                "LP should be provided during viw creation"
    )
    protected open fun createLayoutParams(parent: ViewGroup): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    protected open fun onRefReady(ref: R) {
        // no op by default, but could be used to process ref after it has been fully created
        //  (after create method returns)
    }
}