package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

class Capsule(block: Capsule.() -> Unit = {}) : Shape() {

    init {
        block(this)
    }

    private val rectF = RectF()

    override fun clone(): Capsule = Capsule()
    override fun toStringProperties(): String = ""

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        val radius = radius(bounds)

        rectF.set(bounds)

        canvas.drawRoundRect(
            rectF,
            radius,
            radius,
            paint
        )
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setRoundRect(bounds, radius(bounds))
    }

    private fun radius(bounds: Rect): Float = Math.min(bounds.width(), bounds.height()) / 2F
}