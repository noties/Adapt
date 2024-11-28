package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.util.onAttachedOnce

object ExploreViewFactory {

    // hm, cannot mix-match with raw dimensions
//    @JvmInline
//    value class LayoutDimension(val rawValue: Int) {
//        companion object {
//            val fill: LayoutDimension get() = LayoutDimension(LayoutParams.MATCH_PARENT)
//            val wrap: KLa
//        }
//    }

    interface ViewFactoryConstants {
        val fill: Int
            get() {
                return LayoutParams.MATCH_PARENT
            }

        val wrap: Int
            get() {
                return LayoutParams.WRAP_CONTENT
            }

        companion object : ViewFactoryConstants
    }

//    typealias ViewBuilder<LP> = ViewFactory<LP>.() -> ViewElement<out View, LP>

    interface Factory<T> {
        fun create(): T
    }

    interface ViewFactory<LP : LayoutParams> : Factory<View>, ViewFactoryConstants {
        val context: Context
        val viewGroup: ViewGroup?

        fun viewFactoryAdd(element: ViewElement<out View, LP>)

        /**
         * Protection against accidentally (and involuntary) calling wrong functions
         * when inside nested builder/factory blocks, so function has visibility of other context
         * and assumes that it is applicable to it children, which results
         * in runtime crashes due to type casting
         */
        val viewFactoryAreElementsConsumed: Boolean

        fun viewFactoryInspectElements(): List<ViewElement<out View, LP>>
        fun viewFactoryConsumeElements(): List<ViewElement<out View, LP>>

        // creates single view (if underlying collection has a single element only)
        override fun create(): View {
            val elements = viewFactoryConsumeElements()
            val elementsSize = elements.size

            return elements
                .takeIf { elementsSize == 1 }
                ?.first()
                ?.let { el ->
                    el.init(context).also { el.render() }
                }
                ?: error("ViewFactory should contain exactly 1 element in order to create view, factory:$this")
        }

        fun createAll(): List<View> {
            return viewFactoryConsumeElements()
                .map { el ->
                    val view = el.init(context)
                    el.renderPreAttach()
                    view.onAttachedOnce { el.render() }
                    view
                }
        }

        fun addToViewGroup(viewGroup: ViewGroup? = null) {
            val group = (viewGroup ?: this.viewGroup) ?: error("ViewGroup is missing")
            viewFactoryConsumeElements()
                .forEach { el ->

                    val view = el.init(context)

                    // TODO: item createLayoutParams.. deprecate and just set it before being used
                    // LP must be set before adding to view, but otherwise OnLayout must happen after view is added
                    el.renderPreAttach()
                    // check for layout-params?
                    // here we must render layout (before adding)
                    group.addView(view)
                    // render everything again, layout should be empty anyway
                    el.render()
                }
        }

        companion object {
            fun <V : View, LP : LayoutParams> create(
                context: Context,
                block: ViewFactory<LP>.() -> ViewElement<V, LP>
            ) = create(Base(context), block)

            fun <V : View, LP : LayoutParams> create(
                viewGroup: ViewGroup,
                block: ViewFactory<LP>.() -> ViewElement<V, LP>
            ) = create(Base(viewGroup), block)

            fun <V : View, LP : LayoutParams> create(
                factory: ViewFactory<LP>,
                block: ViewFactory<LP>.() -> ViewElement<V, LP>
            ): V {
                val element = block(factory)
                @Suppress("UNCHECKED_CAST")
                return factory.create() as V
            }

            @JvmName("addChildrenLayoutParams")
            fun addChildren(
                viewGroup: ViewGroup,
                block: ViewFactory<LayoutParams>.() -> Unit
            ) = addChildren<LayoutParams>(viewGroup, block)

            fun <LP : LayoutParams> addChildren(
                viewGroup: ViewGroup,
                block: ViewFactory<LP>.() -> Unit
            ) {
                val factory = Base<LP>(viewGroup)
                block(factory)
                factory.addToViewGroup(viewGroup)
            }
        }

        open class Base<LP : LayoutParams>(
            override val context: Context,
            override val viewGroup: ViewGroup? = null
        ) : ViewFactory<LP> {
            constructor(viewGroup: ViewGroup) : this(viewGroup.context, viewGroup)

            private val elements = mutableListOf<ViewElement<out View, LP>>()

            override fun viewFactoryAdd(element: ViewElement<out View, LP>) {
                if (viewFactoryAreElementsConsumed) {
                    // init view for proper error reporting
                    val view = element.init(context)
                    throw IllegalStateException(
                        "ViewFactory has elements consumed, cannot add more elements, " +
                                "viewGroup:$viewGroup, element.view:$view factory:$this"
                    )
                }
                elements.add(element)
            }

            override var viewFactoryAreElementsConsumed: Boolean = false

            override fun viewFactoryInspectElements(): List<ViewElement<out View, LP>> {
                return elements.toList()
            }

            override fun viewFactoryConsumeElements(): List<ViewElement<out View, LP>> {
                // do not throw
                return elements.toList().also {
                    viewFactoryAreElementsConsumed = true
                    elements.clear()
                }
            }

            override fun toString(): String {
                return "ViewFactory.Base(context=$context, viewGroup=$viewGroup, areElementsConsumed=$viewFactoryAreElementsConsumed, elements=$elements)"
            }
        }
    }

    fun hey(context: Context, frame: FrameLayout) {
        val view = ViewFactory.create(context) {
            ViewElement<View, LayoutParams> { View(it) }.also { viewFactoryAdd(it) }
        }
        val view2 = ViewFactory.create(frame) {
            ViewElement<View, LayoutParams> { View(it) }.also { viewFactoryAdd(it) }
        }
        val view3 = ViewFactory.create(ViewFactory.Base(context)) {
            ViewElement<View, LayoutParams> { View(it) }.also { viewFactoryAdd(it) }
        }

        ViewFactory.addChildren(frame) {

        }

        ViewFactory.addChildren<FrameLayout.LayoutParams>(frame) {

        }
    }
}