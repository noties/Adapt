package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect

class Rectangle(block: Rectangle.() -> Unit = {}) : Shape() {

    init {
        block(this)
    }

    override fun clone(): Rectangle = Rectangle()
    override fun toStringDedicatedProperties(): String = ""

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        canvas.drawRect(bounds, paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setRect(bounds)
    }
}