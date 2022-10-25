package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect

/**
 * Creates a circle shape that could be fit fully in bounds. By design will be less than supplied
 * bounds. For example, if bounds are `Rect(0, 0, 100, 20)` then Circle would take
 * `Rect(0, 0, 20, 20)`. If [Shape.gravity] is specified Circle would follow it. So,
 * if `gravity = Gravity.center` Circle would take `Rect(40, 0, 60, 20)` (centered
 * inside supplied bounds)
 */
class Circle(block: Circle.() -> Unit = {}) : Shape() {

    init {
        block(this)
    }

    private val rect = Rect()

    override fun clone(): Circle = Circle()
    override fun toStringDedicatedProperties(): String = ""

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        val radius = radius(bounds)
        val rect = buildRect(bounds, radius)
        canvas.drawCircle(
            rect.centerX().toFloat(),
            rect.centerY().toFloat(),
            radius.toFloat(),
            paint
        )
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        val radius = radius(bounds)
        val rect = buildRect(bounds, radius)
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        outline.setOval(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    // TODO: does it make sense to make gravity by default is center?
    private fun buildRect(bounds: Rect, radius: Int): Rect {
        // MARK! Layout direction
        return gravity?.let {
            val side = radius * 2
            android.view.Gravity.apply(
                it.value,
                side,
                side,
                bounds,
                rect
            )
            rect
        } ?: bounds
    }

    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
    private fun radius(bounds: Rect): Int = Math.min(bounds.width(), bounds.height()) / 2
}