package io.noties.adapt.ui

import android.content.Context
import android.view.View
import io.noties.adapt.ui.util.DynamicIterator.Companion.dynamicIterator

class ViewElement<V : View, LP : LayoutParams>(
    private val provider: (Context) -> V
) {

    lateinit var view: V

    val isInitialized: Boolean get() = this::view.isInitialized

    var isRendering: Boolean = false
        private set

    internal val layoutParamsBlocks: MutableList<LP.() -> Unit> = mutableListOf()
    internal val viewBlocks: MutableList<V.() -> Unit> = mutableListOf()

    fun init(context: Context): V {
        view = provider(context)
        return view
    }

    // TODO: does it make sense to call render if view is already present?
    fun onView(
        block: V.() -> Unit
    ): ViewElement<V, LP> = this.also {
        it.viewBlocks.add(block)
    }

    // TODO: does it make sense to call render if view is already present?
    fun onLayoutParams(
        block: LP.() -> Unit
    ): ViewElement<V, LP> = this.also {
        it.layoutParamsBlocks.add(block)
    }

    fun render() {
        // if we are already rendering, no need to launch it again - blocks would be added and invoked
        //  automatically
        if (isRendering) return

        isRendering = true

        var renderCount = 0

        // if any of callbacks contribute to the rendering during rendering
        //  they should be invoked during this phase
        while (true) {

            // process layout
            layoutParamsBlocks.also {
                @Suppress("UNCHECKED_CAST")
                if (it.isNotEmpty()) {
                    // obtain layout params
                    val lp = view.layoutParams as LP

                    // special iterator that allows adding new elements whilst iterating
                    //  so, if a layoutBlock adds another onLayoutParams, it would be invoked here
                    val iterator = it.dynamicIterator()

                    val startSize = it.size

                    while (iterator.hasNext()) {
                        iterator.next()(lp)

                        if (it.size - startSize > renderingMaxDifferenceDuringSinglePass) {
                            throw IllegalStateException(
                                "It seems one of the layout-blocks " +
                                        "might be endlessly contributing to the rendering"
                            )
                        }
                    }

                    // trigger layout
                    view.requestLayout()

                    // clear all blocks
                    it.clear()
                }
            }

            // process view
            viewBlocks.also {
                if (it.isNotEmpty()) {

                    val view = this.view
                    val iterator = it.dynamicIterator()

                    val startSize = it.size

                    while (iterator.hasNext()) {
                        iterator.next()(view)

                        // as we allow contributing to the rendering during the rendering itself
                        //  so, a if a new callback would be added during rendering, it would be
                        //  triggered in this same phase. But, to put a safeguard that this does not
                        //  cause an endless loop (and thus stackoverflow or memory exception)
                        if (it.size - startSize > renderingMaxDifferenceDuringSinglePass) {
                            throw IllegalStateException(
                                "It seems one of the view-blocks " +
                                        "might be endlessly contributing to the rendering"
                            )
                        }
                    }

                    // trigger view invalidation
                    view.invalidate()

                    // clear all view blocks
                    it.clear()
                }
            }

            // exit the while loop if there are no blocks left to process
            if (layoutParamsBlocks.isEmpty() && viewBlocks.isEmpty()) break

            if (++renderCount == renderingMaxLoopsLayoutView) {
                // if there were already 5 renders, we assume something contributes
                //  endlessly to the iteration, try exit the loop with posting to the view?
                //  Hm, but then... if it is still endless, it would still be contributing,
                //  thus even if we post to view, it would be endlessly posting..

                // Actually, here we would be if one of the view blocks contributes
                // to layout-blocks on each iteration, and layout-block in turn contributes
                //  to view-blocks, this is a very specific loop.
                // As we allow adding blocks during iteration, if a view-block adds another view-block,
                //  then iteration won't exit, how can we handle that?
                throw IllegalStateException(
                    "It seems, one of the view-blocks contributes to " +
                            "the layout-blocks, which in turn adds another view-block, " +
                            "which might result in an endless loop"
                )
            }
        }

        isRendering = false
    }

    fun render(block: (ViewElement<V, LP>) -> Unit) {
        block(this)
        render()
    }

    companion object {
        internal const val renderingMaxDifferenceDuringSinglePass = 42
        internal const val renderingMaxLoopsLayoutView = 5
    }
}