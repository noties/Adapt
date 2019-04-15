package io.noties.adapt.sample

import android.graphics.*
import android.graphics.drawable.Drawable

class IconDrawable : Drawable() {

    private var color: Int = 0
    private lateinit var shape: Shape

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val path: Path by lazy { Path() }
    private val rectF: RectF by lazy { RectF() }

    fun update(color: Int, shape: Shape) {
        this.color = color
        this.shape = shape
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {

        paint.color = color

        canvas.withSave {
            when (shape) {
                Shape.SQUARE -> drawRect(bounds, paint)
                Shape.CIRCLE -> {
                    rectF.set(bounds)
                    val r = rectF.width() / 2
                    drawRoundRect(rectF, r, r, paint)
                }
                Shape.TRIANGLE -> {
                    val w = bounds.width().toFloat()
                    val h = bounds.height().toFloat()
                    path.apply {
                        reset()
                        moveTo(w / 2, 0.0F)
                        lineTo(w, h)
                        lineTo(0.0F, h)
                        close()
                    }
                    drawPath(path, paint)
                }
            }
        }
    }

    override fun getOpacity() = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // no op
    }

    override fun setAlpha(alpha: Int) {
        // no op
    }

    private inline fun Canvas.withSave(draw: Canvas.() -> Unit) {
        val save = this.save()
        try {
            draw(this)
        } finally {
            this.restoreToCount(save)
        }
    }
}