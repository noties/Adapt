package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import io.noties.adapt.ui.util.dip

class RoundedRectangleShape(
    var radius: Int = 0,
    block: RoundedRectangleShape.() -> Unit = {}
) : Shape() {

    init {
        block(this)
    }

    private val rectF = RectF()
    private val cornerRadius: Float get() = radius.dip.toFloat()

    override fun clone(): RoundedRectangleShape = RoundedRectangleShape(radius)
    override fun toStringDedicatedProperties(): String = "radius=$radius"

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        rectF.set(bounds)
        cornerRadius.also {
            canvas.drawRoundRect(rectF, it, it, paint)
        }
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setRoundRect(bounds, cornerRadius)
    }
}