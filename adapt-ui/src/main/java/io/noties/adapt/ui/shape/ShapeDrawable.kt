package io.noties.adapt.ui.shape

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.util.DrawableState
import io.noties.adapt.ui.util.DrawableStateSet
import kotlin.math.roundToInt

open class ShapeDrawable<R : Any> protected constructor(
    val shape: Shape,
    val ref: R
) : Drawable(), DensityAwareDrawable {

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

    override var density: Float = Resources.getSystem().displayMetrics.density

    private var stateful: Stateful<R>? = null
    private var hotspot: Hotspot<R>? = null

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        shape.draw(canvas, bounds, density)
    }

    override fun getIntrinsicWidth(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.width
            ?.resolve(0, density)
            ?.takeIf { it != 0 }
            ?: super.getIntrinsicWidth()
    }

    override fun getIntrinsicHeight(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.height
            ?.resolve(0, density)
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
        shape.outline(outline, bounds, density)
    }

    fun invalidate(block: ShapeDrawable<R>.(R) -> Unit) {
        block(this, ref)
        invalidateSelf()
    }

    override fun setHotspot(x: Float, y: Float) {
        super.setHotspot(x, y)

        hotspot?.onHotspotChanged?.invoke(this, x, y)
    }

    /**
     * Please note that in most cases drawable must also be stateful to report `pressed` or `hovered`
     * states, otherwise this hotspot event won\'t be delivered
     */
    fun hotspot(onHotspotChanged: ShapeDrawable<R>.(x: Float, y: Float) -> Unit) = this.also {
        it.hotspot = Hotspot(onHotspotChanged)
        it.invalidateSelf()
    }

    fun clearHotspot() = this.also {
        it.hotspot = null
        it.invalidateSelf()
    }

    fun stateful(
        states: Set<DrawableState> = emptySet(),
        onStateChange: ShapeDrawable<R>.(DrawableStateSet) -> Unit = {}
    ) = this.also {
        it.stateful = Stateful(states, onStateChange)
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
        val stateful = this.stateful ?: return false
        // if empty, then track all states
        val result = stateful.states.isEmpty() || kotlin.run {
            // important to check current state AND PREVIOUS, in case there was a change
            //  so, for example, previous contained `pressed`, but current does not ->
            //  this means the state of `pressed` has changed (going from pressed to not pressed)
            DrawableStateSet.containsAny(state, stateful.states) || DrawableStateSet.containsAny(
                stateful.previousState,
                stateful.states
            )
        }
        stateful.persistState(state)
        if (result) {
            stateful.onStateChange.invoke(this, DrawableStateSet(state))
            invalidateSelf()
        }
        return result
    }

    private class Stateful<R : Any>(
        val states: Set<DrawableState>,
        val onStateChange: ShapeDrawable<R>.(DrawableStateSet) -> Unit = {}
    ) {
        var previousState: IntArray = intArrayOf()
            private set

        fun persistState(state: IntArray) {
            // if out states is empty, we do not need to persist previous state
            if (states.isNotEmpty()) {
                previousState = state.copyOf()
            }
        }
    }

    private class Hotspot<R : Any>(val onHotspotChanged: ShapeDrawable<R>.(x: Float, y: Float) -> Unit)
}