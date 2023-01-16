package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
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

        operator fun <S : Shape, R : Any> invoke(ref: R, block: (R) -> S): ShapeDrawable<R> {
            return ShapeDrawable(block(ref), ref)
        }
    }

    override fun draw(canvas: Canvas) {
        shape.draw(canvas, bounds)
    }

    override fun getIntrinsicWidth(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.width?.resolve(0) ?: super.getIntrinsicWidth()
    }

    override fun getIntrinsicHeight(): Int {
        // NB! relative dimension would not report intrinsic value (we have no reference)
        return shape.height?.resolve(0) ?: super.getIntrinsicHeight()
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
}