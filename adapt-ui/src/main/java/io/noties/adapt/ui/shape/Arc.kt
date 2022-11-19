package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

class Arc(
    var startAngle: Float,
    var sweepAngle: Float,
    var useCenter: Boolean? = null,
    block: Arc.() -> Unit = {}
) : Shape() {

    init {
        block(this)
    }

    fun arc(
        startAngle: Float? = null,
        sweepAngle: Float? = null,
        useCenter: Boolean? = null
    ) = this.apply {
        startAngle?.also { this.startAngle = it }
        sweepAngle?.also { this.sweepAngle = it }
        useCenter?.also { this.useCenter = it }
    }

    private val rectF = RectF()

    override fun clone(): Arc = Arc(startAngle, sweepAngle, useCenter)

    override fun toStringDedicatedProperties(): String {
        return "startAngle=$startAngle, sweepAngle=$sweepAngle, useCenter=$useCenter"
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        rectF.set(bounds)
        canvas.drawArc(
            rectF,
            startAngle,
            sweepAngle,
            useCenter ?: true,
            paint
        )
    }
}