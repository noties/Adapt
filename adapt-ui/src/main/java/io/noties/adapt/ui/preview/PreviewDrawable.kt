package io.noties.adapt.ui.preview

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import io.noties.adapt.ui.util.AbsDrawable
import io.noties.adapt.ui.util.withAlphaComponent
import kotlin.math.roundToInt

class PreviewDrawable(
    val drawables: List<Drawable>
) : AbsDrawable() {

    companion object {
        fun default(view: View, @ColorInt color: Int): PreviewDrawable {
            return PreviewDrawable(
                drawables = listOf(
                    PreviewBoundsDrawable(color),
                    PreviewPaddingDrawable(color, view)
                )
            )
        }
    }

    override fun draw(canvas: Canvas) {
        drawables.forEach { it.draw(canvas) }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        drawables.forEach { it.bounds = bounds }
    }
}

class PreviewBoundsDrawable(
    @ColorInt val color: Int,
    // adds an X (cross inside)
    private val isStrokeInside: Boolean = false
) : AbsDrawable() {

    private val rect = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.STROKE
        it.color = color
        it.strokeWidth = Resources.getSystem().displayMetrics.density
    }

    override fun draw(canvas: Canvas) {

        val half = (paint.strokeWidth / 2F).roundToInt()
        rect.set(bounds)
        rect.inset(half, half)

        canvas.drawRect(rect, paint)

        if (isStrokeInside) {
            val w = rect.width().toFloat()
            val h = rect.height().toFloat()
            canvas.drawLine(
                0F,
                0F,
                w,
                h,
                paint
            )
            canvas.drawLine(
                0F,
                h,
                w,
                0F,
                paint
            )
        }
    }
}

class PreviewPaddingDrawable(@ColorInt val color: Int, val view: View) : AbsDrawable() {

    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.FILL
        it.color = color.withAlphaComponent(0.2F)
    }

    override fun draw(canvas: Canvas) {
        if (path.isEmpty) return

        canvas.drawPath(path, paint)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        path.reset()

        val (left, top, right, bottom) = listOf(
            view.paddingLeft.toFloat(),
            view.paddingTop.toFloat(),
            view.paddingRight.toFloat(),
            view.paddingBottom.toFloat()
        )

        val (w, h) = bounds.width().toFloat() to bounds.height().toFloat()

        if (left != 0F) {
            path.addRect(0F, 0F, left, h, Path.Direction.CW)
        }

        if (top != 0F) {
            path.addRect(0F, 0F, w, top, Path.Direction.CW)
        }

        if (right != 0F) {
            path.addRect(w - right, 0F, w, h, Path.Direction.CW)
        }

        if (bottom != 0F) {
            path.addRect(0F, h - bottom, w, h, Path.Direction.CW)
        }

        path.close()
    }
}