package io.noties.adapt.sample.ui

import android.graphics.*
import android.graphics.drawable.Drawable
import kotlin.math.min
import kotlin.properties.Delegates

class CircleDrawable(color: Int) : Drawable() {

    var color: Int by Delegates.observable(color) { _, _, value ->
        paint.color = value
        invalidateSelf()
    }

    private val rectF = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var radius: Float = 0F
    private var top: Float = 0F
    private var left: Float = 0F

    init {
        paint.color = color
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        val w = bounds.width()
        val h = bounds.height()

        val side = min(w.toFloat(), h.toFloat())

        radius = side / 2F
        top = (h - side) / 2F
        left = (w - side) / 2F

        rectF.set(0F, 0F, side, side)
    }

    override fun draw(canvas: Canvas) {
        val save = canvas.save()
        try {
            canvas.translate(left, top)
            canvas.drawRoundRect(rectF, radius, radius, paint)
        } finally {
            canvas.restoreToCount(save)
        }
    }

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    override fun getOpacity(): Int = PixelFormat.OPAQUE

}