package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect

open class RectangleShape(block: RectangleShape.() -> Unit = {}) : Shape() {

    init {
        block(this)
    }

    override fun clone(): RectangleShape = RectangleShape()
    override fun toStringDedicatedProperties(): String = ""

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint, density: Float) {
        canvas.drawRect(bounds, paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect, density: Float) {
        outline.setRect(bounds)
    }
}