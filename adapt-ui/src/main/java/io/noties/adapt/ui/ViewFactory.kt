package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.element.ElementViewFactory
import io.noties.adapt.ui.util.onAttachedOnce

typealias LayoutParams = ViewGroup.LayoutParams

class ViewFactory<out LP : LayoutParams>(
    context: Context,
    viewGroup: ViewGroup? = null
) : ViewFactoryConstants {
    constructor(viewGroup: ViewGroup) : this(viewGroup.context, viewGroup)

    val context: Context = ElementViewFactory.contextWrapper(context)

    private val _viewGroup: ViewGroup? = viewGroup

    val hasViewGroup: Boolean get() = _viewGroup != null

    val viewGroup: ViewGroup
        get() = _viewGroup ?: error("ViewFactory does not contain ViewGroup, this:$this")

    var areElementsConsumed = false
        private set

    private val _elements = mutableListOf<ViewElement<out View, *>>()

    /**
     * Returns elements without marking them as _used_
     */
    fun inspectElements() = _elements.toList()

    fun consumeElements() = _elements.toList().also {
        areElementsConsumed = true
        _elements.clear()
    }

    fun add(element: ViewElement<*, *>) {
        if (areElementsConsumed) {
            // init view for proper error reporting
            val view = element.init(context)
            throw IllegalStateException(
                "ViewFactory has elements consumed, cannot add more elements, " +
                        "viewGroup:$_viewGroup, element.view:$view"
            )
        }
        _elements.add(element)
    }

    companion object {

        fun createView(
            context: Context,
            children: ViewFactory<LayoutParams>.(Unit) -> Unit
        ): View = newView(context).create(children)

        fun <R : Any> createView(
            context: Context,
            ref: R,
            children: ViewFactory<LayoutParams>.(R) -> Unit
        ): View = newView(context).create(ref, children)

        fun newView(
            context: Context,
            viewGroup: ViewGroup? = null
        ) = ViewCreator(context, viewGroup)

        fun newView(viewGroup: ViewGroup) = newView(viewGroup.context, viewGroup)

        @JvmName("addChildrenViewGroup")
        fun addChildren(
            group: ViewGroup,
            children: ViewFactory<LayoutParams>.() -> Unit
        ) = addChildren<ViewGroup, LayoutParams>(group, children)

        @JvmName("addChildrenGeneric")
        fun <G : ViewGroup, LP : LayoutParams> addChildren(
            g: G,
            children: ViewFactory<LP>.() -> Unit
        ) {

            val factory = ViewFactory<LP>(g)
            val context = factory.context

            children(factory)

            //noinspection NewApi
            factory.consumeElements()
                .forEach { el ->
                    @Suppress("UNCHECKED_CAST")
                    el as ViewElement<View, LP>

                    val view = el.init(context)

                    // now layoutParams are generated
                    g.addView(view)

                    el.render()
                }
        }
    }

    class ViewCreator<LP : LayoutParams> internal constructor(
        val context: Context,
        val viewGroup: ViewGroup?,
        val layoutParams: LP,
        // can request to render on first attach
        val renderOnAttach: Boolean = false
    ) {

        companion object {
            val defaultLayoutParams: LayoutParams
                get() = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )

            operator fun invoke(context: Context, viewGroup: ViewGroup?) = ViewCreator(
                context,
                viewGroup,
                defaultLayoutParams
            )
        }

        fun <T : LayoutParams> layoutParams(layoutParams: T): ViewCreator<T> = ViewCreator(
            context, viewGroup, layoutParams
        )

        fun renderOnAttach(): ViewCreator<LP> = ViewCreator(
            context, viewGroup, layoutParams, true
        )

        // TODO: find a way to not specify Unit as argument (done so so it is easier
        //  to call version with `ref`... as `() -> Unit` won't be generified,
        //  so calling it with Unit as argument won't be possible
        fun create(
            children: ViewFactory<LP>.(Unit) -> Unit
        ): View = create(Unit, children)

        fun <R : Any> create(
            ref: R,
            children: ViewFactory<LP>.(R) -> Unit
        ): View {

            val factory = ViewFactory<LP>(context, viewGroup)
            children(factory, ref)

            val elements = factory.consumeElements()

            // ensure single element
            if (elements.size != 1) {
                throw IllegalStateException("Unexpected state, view must contain exactly one root element")
            }

            @Suppress("UNCHECKED_CAST")
            val root = elements[0] as ViewElement<View, LP>

            val view = root.init(factory.context)

            // NB! apply layout params only if original view does not contain them
            //  if it does, use them
            if (view.layoutParams == null) {
                view.layoutParams = layoutParams
            }

            if (renderOnAttach) {
                // we assume that layout params would be already processed by parent
                view.onAttachedOnce { root.render() }
            } else {
                // trigger render now
                root.render()
            }

            return view
        }
    }
}