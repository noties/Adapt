package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup

typealias LayoutParams = ViewGroup.LayoutParams

class ViewFactory<out LP : LayoutParams>(
    val context: Context,
    viewGroup: ViewGroup? = null
) {
    constructor(viewGroup: ViewGroup) : this(viewGroup.context, viewGroup)

    private val _viewGroup: ViewGroup? = viewGroup

    val hasViewGroup: Boolean get() = _viewGroup != null

    val viewGroup: ViewGroup
        get() = _viewGroup ?: error("ViewFactory does not contain ViewGroup, this:$this")

    @Suppress("PropertyName")
    val FILL: Int
        get() {
            return LayoutParams.MATCH_PARENT
        }

    @Suppress("PropertyName")
    val WRAP: Int
        get() {
            return LayoutParams.WRAP_CONTENT
        }

    var isUsed = false
        private set

    private val _elements = mutableListOf<ViewElement<out View, *>>()

    /**
     * Returns elements without marking them as _used_
     */
    fun inspectElements() = _elements.toList()

    fun useElements() = _elements.toList().also {
        isUsed = true
        _elements.clear()
    }

    fun add(element: ViewElement<*, *>) {
        if (isUsed) {
            // init view for proper error reporting
            val view = element.init(context)
            throw IllegalStateException(
                "ViewFactory has been already used, cannot add more elements, " +
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
            factory.useElements()
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
        val layoutParams: LP
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

        fun create(
            children: ViewFactory<LP>.(Unit) -> Unit
        ): View = create(Unit, children)

        fun <R : Any> create(
            ref: R,
            children: ViewFactory<LP>.(R) -> Unit
        ): View {

            val factory = ViewFactory<LP>(context, viewGroup)
            children(factory, ref)

            val elements = factory.useElements()

            // ensure single element
            if (elements.size != 1) {
                throw IllegalStateException("Unexpected state, view must contain exactly one root element")
            }

            @Suppress("UNCHECKED_CAST")
            val root = elements[0] as ViewElement<View, LP>

            val view = root.init(context)

            view.layoutParams = layoutParams

            root.render()

            return view
        }
    }
}