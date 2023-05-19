package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.state.DrawableState
import io.noties.adapt.ui.state.DrawableStateSet
import kotlin.math.roundToInt

typealias ShapeDrawableNoRef = ShapeDrawable<Unit>

open class ShapeDrawable<R : Any> private constructor(
    val shape: Shape,
    val ref: R
) : Drawable() {

    companion object {

        operator fun invoke(
            shape: Shape
        ): ShapeDrawableNoRef = ShapeDrawable(shape, Unit)

        operator fun <S : Shape> invoke(block: () -> S) = ShapeDrawable(Unit) { block() }

        operator fun <S : Shape, R : Any> invoke(ref: R, block: (R) -> S): ShapeDrawable<R> {
            return ShapeDrawable(block(ref), ref)
        }
    }

    private var stateful: Stateful<R>? = null

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
    }

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun getOutline(outline: Outline) {
        shape.outline(outline, bounds)
    }

    fun invalidate(block: (R) -> Unit) {
        block(ref)
        invalidateSelf()
    }

    fun setStateless() = this.also {
        it.stateful = null
        invalidateSelf()
    }

    fun stateful(
        states: Set<DrawableState> = emptySet(),
        onStateChange: ShapeDrawable<R>.(DrawableStateSet) -> Unit = {}
    ) = this.also {
        it.stateful = Stateful(states, onStateChange)
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
        stateful.previousState = state.copyOf()
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
    }
}