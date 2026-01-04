package io.noties.adapt.sample.explore

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.shape.Shape
import io.noties.adapt.ui.shape.ShapeFactory
import io.noties.adapt.ui.shape.ShapeFactoryBuilder
import io.noties.adapt.ui.state.ViewState
import io.noties.adapt.ui.util.AbsDrawable

object ExploreDynamicShapeState {

    // so, we need to cache previous one to check if it is the same
    //  and if changed, then trigger callback.
    fun create(
        block: ShapeFactory.(ViewState) -> Shape
    ) = create(filter = null, block = block)

    fun create(
        filter: Set<ViewState>?,
        block: ShapeFactory.(ViewState) -> Shape
    ): Drawable {
        TODO()
    }

    class StateChangedDrawable(
        val filter: Set<Int>?,
        val shapeProvider: ShapeFactory.(ViewState) -> Shape
    ): AbsDrawable() {

//        private var previousShape: Shape? = null
        private var shape: Shape? = null
        private var state: Set<Int>? = null
        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            shape?.draw(canvas, bounds)
        }

        override fun isStateful(): Boolean {
            // if filters is:
            // - null - true (no filters - just all)
            // - empty filter - false (no state specified)
            // - non-empty filter - true
            return filter == null || filter.isNotEmpty()
        }

        override fun onStateChange(state: IntArray): Boolean {
            // if empty, then no results
            if (true == filter?.isEmpty()) {
                return false
            }

            val shouldIgnore = if (filter != null) {
                // so, if there is a change in previous and now between states we are filtering
                // then should NOT ignore, else yes

                // first remove all non-interesting elements
                val diff = this.state?.toMutableSet()
                    ?.also {
                        it.removeAll(filter)
                    }

                // then check if they are different with previous one
                if (diff != this.state) {
                    // a change detected, persist state and trigger invalidate
                    this.state = diff
                    true
                } else {
                    false
                }

            } else {
                // do not ignore
                false
            }

            if (shouldIgnore) {
                return false
            }

            val shape = shapeProvider(ShapeFactory.NoOp, ViewState(state))
            val hasChanged = this.shape != shape

            // TODO: bounds?
            if (hasChanged) {
                this.shape = shape
                this.drawable = shape.newDrawable()
                invalidateSelf()
            }

            return hasChanged
        }
    }
}