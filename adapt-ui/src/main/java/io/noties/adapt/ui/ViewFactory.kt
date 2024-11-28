package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.util.onAttachedOnce

typealias ViewBuilder<V, LP> = ViewFactory<LP>.() -> ViewElement<V, LP>

// TODO: change to interface (the same as colors, ect)
//  provide helper to subclass and create final view
open class ViewFactory<out LP : LayoutParams>(
    val context: Context,
    viewGroup: ViewGroup? = null
) : ViewFactoryConstants {
    constructor(viewGroup: ViewGroup) : this(viewGroup.context, viewGroup)

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
            children: ViewFactory<LayoutParams>.() -> Unit
        ): View = newView(context).create(children)

        fun createView(
            viewGroup: ViewGroup,
            children: ViewFactory<LayoutParams>.() -> Unit
        ): View = newView(viewGroup.context, viewGroup).create(children)

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
            addChildren(
                ViewFactory(g),
                g,
                children
            )
        }

        @JvmName("addChildrenViewFactory")
        fun <VF : ViewFactory<LP>, G : ViewGroup, LP : LayoutParams> addChildren(
            factory: VF,
            g: G,
            children: VF.() -> Unit
        ) {

            val context = factory.context

            children(factory)

            //noinspection NewApi
            factory.consumeElements()
                .forEach { el ->
                    @Suppress("UNCHECKED_CAST")
                    el as ViewElement<View, LP>

                    val view = el.init(context)
                    el.renderPreAttach()

                    // now layoutParams are generated
                    g.addView(view)

                    el.render()
                }
        }
    }

    class ViewCreator(
        val context: Context,
        val viewGroup: ViewGroup?,
        val layoutParams: LayoutParams? = defaultLayoutParams,
    ) {

        companion object {
            private val defaultLayoutParams: LayoutParams
                get() = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
        }

        fun <LP: LayoutParams> create(
            children: ViewFactory<LP>.() -> Unit
        ): View = create(Unit) {
            children()
        }

        fun <LP: LayoutParams, R : Any> create(
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

            val view = root.init(context)
            root.renderPreAttach()

            // NB! apply layout params only if original view does not contain them
            //  if it does, use them
            if (view.layoutParams == null) {
                view.layoutParams = layoutParams
            }

            // trigger render now
            root.render()

            return view
        }
    }
}