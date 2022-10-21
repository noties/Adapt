package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

class Oval(block: Oval.() -> Unit = {}) : Shape() {

    init {
        block(this)
    }

    private val rectF = RectF()

    override fun clone(): Oval = Oval()
    override fun toStringProperties(): String = ""

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        rectF.set(bounds)
        canvas.drawOval(rectF, paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setOval(bounds)
    }
}