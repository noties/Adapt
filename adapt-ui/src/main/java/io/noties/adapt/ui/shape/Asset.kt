package io.noties.adapt.ui.shape

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

// NB! it discards received paint (so, fill, nor stroke would function)
// TODO: test
class Asset(
    val drawable: Drawable,
    block: Asset.() -> Unit = {}
) : Shape() {

    init {
        block(this)
    }

    companion object {

        fun tinted(
            drawable: Drawable,
            @ColorInt tintColor: Int,
            block: Asset.() -> Unit = {}
        ): Asset {
            val resource = drawable.mutate().also { it.setTint(tintColor) }
            return Asset(resource, block)
        }

        fun tinted(
            drawable: Drawable,
            colorStateList: ColorStateList,
            block: Asset.() -> Unit = {}
        ): Asset {
            val resource = drawable.mutate().also { it.setTintList(colorStateList) }
            return Asset(resource, block)
        }
    }

    init {

        // we need fill value in order to trigger drawing
        fill(defaultFillColor)

        // we need to report size, let's see if bounds are empty
        val density = Resources.getSystem().displayMetrics.density

        fun value(intrinsic: Int?): Int? = intrinsic
            ?.takeIf { it > 0 }
            ?.let { (it / density).roundToInt() }

        val w = value(drawable.intrinsicWidth)
        val h = value(drawable.intrinsicHeight)

        if (w != null && h != null) {
            size(w, h)
        }
    }

    override fun clone(): Asset = Asset(
        // try to create a new instance of resource, or if fails use original
        drawable.constantState?.newDrawable()?.mutate() ?: drawable
    )

    override fun toStringProperties(): String = "drawable=$drawable"

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        drawable.bounds = bounds
        drawable.alpha = paint.alpha
        drawable.draw(canvas)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        drawable.bounds = bounds
        drawable.getOutline(outline)
    }
}