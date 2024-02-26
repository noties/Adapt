package io.noties.adapt.ui

import android.content.Context
import android.view.View
import io.noties.adapt.ui.util.DynamicIterator.Companion.dynamicIterator

open class ViewElement<V : View, LP : LayoutParams>(
    private val provider: (Context) -> V
) {
    companion object {
        internal const val renderingMaxDifferenceDuringSinglePass = 42
        internal const val renderingMaxLoopsLayoutView = 5

        /**
         * A special factory method to initialize [ViewElement] with already created [View],
         * for example, was inflated from XML, was received as an argument. Allows customizing
         * regular views with fluent AdaptUI extensions:
         * ```kotlin
         * val textView: TextView = findViewById(R.id.my_text_view)
         *
         * ViewElement.create(textView)
         *   .textSize(16)
         *   .textShadow(8)
         *   .background {
         *     RoundedRectangle(12).fill(Color.RED)
         *   }
         *   // if specific layout params are required, then
         *   //     `.castLayout` and `.ifCastLayout` extensions can be used
         *   .castLayout(MarginLayoutParams::class)
         *   // NB! rendering would automatically happen with `view.post` queue. If you want
         *   //     to render _now_, then explicit `render` call can be used
         *   .render()
         * ```
         * If created `ViewElement` needs also layout parameters, then regular extensions:
         * - `castLayout` - which casts received layout params and fails when mismatched
         * - `ifCastLayout` - which allows selectively apply certain layout configuration
         *  if received parameters would be of the same type only (and without affecting the rest)
         */
        fun <V : View> create(
            view: V
        ): ViewElement<V, LayoutParams> = ViewElement<V, LayoutParams> { view }
            .also { it.init(view.context) }
    }


    lateinit var view: V

    val isInitialized: Boolean get() = this::view.isInitialized

    var isRendering: Boolean = false
        private set

    internal val layoutParamsBlocks: MutableList<(LP) -> Unit> = mutableListOf()
    internal val viewBlocks: MutableList<(V) -> Unit> = mutableListOf()

    fun init(context: Context): V {
        view = provider(context)
        return view
    }

    fun onView(
        block: (V) -> Unit
    ): ViewElement<V, LP> = this.also {
        it.viewBlocks.add(block)
        scheduleRendering()
    }

    fun onLayoutParams(
        block: (LP) -> Unit
    ): ViewElement<V, LP> = this.also {
        it.layoutParamsBlocks.add(block)
        scheduleRendering()
    }

    fun render() {
        // if element is not yet initialized, ignore the call. When view would become available
        //  it will automatically schedule rendering
        if (!isInitialized) return

        // if we are already rendering, no need to launch it again - blocks would be added and invoked
        //  automatically
        if (isRendering) return

        // if rendering has already started no need to trigger it again
        view.removeCallbacks(renderRunnable)

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

    private fun scheduleRendering() {
        if (!isInitialized) return

        val view = this.view
        view.removeCallbacks(renderRunnable)
        view.post(renderRunnable)
    }

    private val renderRunnable: Runnable = Runnable {
        render()
    }
}