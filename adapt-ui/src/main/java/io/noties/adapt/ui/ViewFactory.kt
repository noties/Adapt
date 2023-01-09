package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup

typealias LayoutParams = ViewGroup.LayoutParams

open class ViewFactory<out LP : LayoutParams>(val context: Context) {

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

    val elements: MutableList<ViewElement<out View, *>> = mutableListOf()

    fun add(element: ViewElement<*, *>) {
        elements.add(element)
    }

    companion object {

        fun createView(
            context: Context,
            children: ViewFactory<LayoutParams>.() -> Unit
        ): View = createView(context, Unit) { children() }

        fun <R : Any> createView(
            context: Context,
            references: R,
            children: ViewFactory<LayoutParams>.(R) -> Unit
        ): View = createViewWithParams(
            context,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
            references,
            children
        )

        fun <LP : LayoutParams> createViewWithParams(
            context: Context,
            layoutParams: LP,
            children: ViewFactory<LP>.() -> Unit
        ): View = createViewWithParams(context, layoutParams, Unit) { children() }

        fun <LP : LayoutParams, R : Any> createViewWithParams(
            context: Context,
            layoutParams: LP,
            references: R,
            children: ViewFactory<LP>.(R) -> Unit
        ): View {

            val factory = ViewFactory<LP>(context)
            children(factory, references)

            // ensure single element
            if (factory.elements.size != 1) {
                throw IllegalStateException("Unexpected state, view must contain exactly one root element")
            }

            @Suppress("UNCHECKED_CAST")
            val root = factory.elements[0] as ViewElement<View, LP>

            val view = root.init(context)

            view.layoutParams = layoutParams

            root.render()

            return view
        }

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

            val context = g.context
            val factory = ViewFactory<LP>(context)

            children(factory)

            //noinspection NewApi
            factory.elements.forEach { el ->
                @Suppress("UNCHECKED_CAST")
                el as ViewElement<View, LP>

                val view = el.init(context)

                // now layoutParams are generated
                g.addView(view)

                el.render()
            }
        }
    }
}

open class ViewFactoryViewGroup<V : ViewGroup, LP : LayoutParams>(
    context: Context,
    val viewGroup: V
) : ViewFactory<LP>(context) {
    constructor(viewGroup: V) : this(viewGroup.context, viewGroup)
}