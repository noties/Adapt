package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import io.noties.adapt.ui.util.dip

class RoundedRectangle(
    var radius: Int = 0,
    block: RoundedRectangle.() -> Unit = {}
) : Shape() {

    init {
        block(this)
    }

    private val rectF = RectF()
    private val cornerRadius: Float get() = radius.dip.toFloat()

    override fun clone(): RoundedRectangle = RoundedRectangle(radius)
    override fun toStringProperties(): String {
        return ::radius
            .let { it.name to it.get() }
            .takeIf { it.second != 0 }
            ?.let { "${it.first}=${it.second}" } ?: ""
    }

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