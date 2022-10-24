package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

typealias ShapeDrawableNoRef = ShapeDrawable<Unit>

open class ShapeDrawable<R : Any> private constructor(
    val shape: Shape,
    val ref: R
) : Drawable() {

    companion object {

        operator fun invoke(
            shape: Shape
        ): ShapeDrawableNoRef = ShapeDrawable(shape, Unit)

        operator fun <S : Shape, T : Any> invoke(
            shape: S,
            ref: T,
            block: S.(T) -> Unit
        ): ShapeDrawable<T> = ShapeDrawable(shape, ref).also {
            block(shape, ref)
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

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun getOutline(outline: Outline) {
        shape.outline(outline, bounds)
    }
}