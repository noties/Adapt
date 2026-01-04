package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.state.ViewState
import io.noties.adapt.ui.state.ViewStateBuilder
import kotlin.math.roundToInt

open class ShapeDrawable<R : Any> protected constructor(
    val shape: Shape,
    val ref: R
) : Drawable() {

    // TODO: rethink constructors, they seem a little weird (especially with additional import)
    //  and `createActual`...
    companion object {

        operator fun invoke(
            shape: Shape
        ): ShapeDrawable<Unit> = shape.newDrawable()

        operator fun invoke(
            builder: ShapeFactoryBuilder
        ): ShapeDrawable<Unit> = builder(ShapeFactory.NoOp).newDrawable()

        operator fun <R : Any> invoke(
            ref: R,
            shape: Shape
        ): ShapeDrawable<R> = shape.newDrawable(ref)

        operator fun <R : Any> invoke(
            ref: R,
            builder: ShapeFactoryRefBuilder<R>
        ): ShapeDrawable<R> = builder(ShapeFactory.NoOp, ref).newDrawable(ref)

        /**
         * Special function to _finally_ create actual [ShapeDrawable] without any
         * redirects.
         */
        fun <R : Any> createActual(shape: Shape, ref: R) = ShapeDrawable(shape, ref)
    }

    private var stateful: Stateful? = null
    private var hotspot: Hotspot? = null

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        shape.draw(canvas, bounds)
    }

    override fun getIntrinsicWidth(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.width
            ?.resolve(0)
            ?.takeIf { it != 0 }
            ?: super.getIntrinsicWidth()
    }

    override fun getIntrinsicHeight(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.height
            ?.resolve(0)
            ?.takeIf { it != 0 }
            ?: super.getIntrinsicHeight()
    }

    override fun getAlpha(): Int {
        return shape.alpha?.let { (it * 255).roundToInt() } ?: 255
    }

    override fun setAlpha(alpha: Int) {
        shape.alpha(alpha / 255F)
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun getOutline(outline: Outline) {
        shape.outline(outline, bounds)
    }

    fun invalidate(block: ShapeDrawable<R>.(R) -> Unit) {
        block(this, ref)
        invalidateSelf()
    }

    override fun setHotspot(x: Float, y: Float) {
        super.setHotspot(x, y)

        hotspot?.setHotspot(x = x, y = y)
    }

    /**
     * Please note that in most cases drawable must also be stateful to report `pressed` or `hovered`
     * states, otherwise this hotspot event won\'t be delivered
     */
    fun hotspot(onHotspotChanged: ShapeDrawable<R>.(x: Float, y: Float) -> Unit) = this.also {
        it.hotspot = Hotspot { x, y ->
            onHotspotChanged(this, x, y)
        }
        it.invalidateSelf()
    }

    fun clearHotspot() = this.also {
        it.hotspot = null
        it.invalidateSelf()
    }

    /**
     * ```kotlin
     * ShapeDrawable()
     *   .stateful(
     *     filter = { pressed.activated },
     *     onStateChanged = {
     *       this.shape.alpha = if (it.isPressed) 0.42F else 1F
     *     }
     *   )
     * ```
     */
    fun stateful(
        // will listen to all events by default
        filter: ViewStateBuilder? = null,
        onStateChanged: ShapeDrawable<R>.(ViewState) -> Unit = {}
    ) = this.also {
        val filter = filter?.invoke(ViewState)
        this.stateful = Stateful(
            filter = filter,
            onStatefulStateChange = {
                onStateChanged(this, it)
            }
        )
        invalidateSelf()
    }

    fun clearStateful() = this.also {
        it.stateful = null
        invalidateSelf()
    }

    override fun isStateful(): Boolean {
        return stateful != null
    }

    override fun onStateChange(state: IntArray): Boolean {
        return stateful?.onStateChanged(state) ?: false
    }

    internal class Stateful(
        val filter: ViewState?,
        val onStatefulStateChange: (ViewState) -> Unit = {}
    ) {
        private var previousState = setOf<Int>()

        fun onStateChanged(state: IntArray): Boolean {
            // if no filter -> all events, do still check against previous state
            val filteredState = if (filter != null) {

                if (filter.rawValues.isEmpty()) {
                    // no states will match
                    emptySet()
                } else {
                    // remove other states (keep only the ones present in the `filter`)
                    state.toMutableSet().also {
                        it.removeAll { state -> !filter.rawValues.contains(state) }
                    }
                }

            } else {
                // do not ignore, consume all events
                state.toSet()
            }

            val result = filteredState != previousState

            if (result) {
                this.previousState = filteredState
                this.onStatefulStateChange(ViewState(rawValues = filteredState))
            }

            return result
        }
    }

    private class Hotspot(private val onChanged: (x: Float, y: Float) -> Unit) {
        fun setHotspot(x: Float, y: Float) {
            onChanged(x, y)
        }
    }
}