package io.noties.adapt.ui.preview

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.util.children
import io.noties.adapt.ui.util.isInPreview
import io.noties.adapt.ui.util.onPreDrawOnce
import io.noties.adapt.ui.util.withAlphaComponent
import kotlin.math.max
import kotlin.math.roundToInt

class PreviewViewElement<V : View, LP : LayoutParams>(
    previewView: V
) : ViewElement<V, LP>(provider = {
    previewView
})

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.preview(
    block: (PreviewViewElement<V, LP>) -> Unit
) = this.also {
    if (isInPreview) {
        // hm, do we need to add it to the factory? it would be rendered otherwise
        //  would not receive a callback
        it.onView { view ->
            val element = PreviewViewElement<V, LP>(view).also { el -> el.init(view.context) }
            block(element)
            element.render()
        }
    }
}

fun <V : View, LP : LayoutParams> PreviewViewElement<V, LP>.previewBounds(
    applyToChildren: Boolean = true
) = onView { view ->
    previewBounds(view, 0F, 0, applyToChildren)
}

private fun previewBounds(
    view: View,
    colorHsvDegree: Float,
    nestedLevel: Int,
    applyToChildren: Boolean
) {

    val array = floatArrayOf(colorHsvDegree % 360F, max(0.1F, 1F - (nestedLevel * 0.1F)), 1F)
    val color = Color.HSVToColor(array)

    val drawables = listOf(
        PreviewBoundsDrawable(color),
        PreviewPaddingDrawable(color, view)
    )

    fun apply(view: View) {
        drawables.forEach { it.setBounds(0, 0, view.width, view.height) }
    }

    apply(view)
    view.onPreDrawOnce { apply(it) }

    drawables.forEach { view.overlay.add(it) }

    if (applyToChildren && view is ViewGroup) {
        var current = colorHsvDegree
        view.children.forEach {
            current += 60F
            previewBounds(it, current, nestedLevel + 1, true)
        }
    }
}

@Suppress("unused")
private abstract class PreviewDrawable(@ColorInt val color: Int) : Drawable() {
    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    final override fun getOpacity(): Int = PixelFormat.OPAQUE
}

private class PreviewBoundsDrawable(@ColorInt color: Int) : PreviewDrawable(color) {

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
    }
}

private class PreviewPaddingDrawable(@ColorInt color: Int, val view: View) :
    PreviewDrawable(color) {

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