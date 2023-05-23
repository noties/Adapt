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

// NB! it discards received paint (so, no fill, nor stroke would function)
class AssetShape(
    drawable: Drawable,
    block: AssetShape.() -> Unit = {}
) : Shape() {

    var drawable: Drawable
        private set

    // empty companion object in case an extension for Asset would be created
    companion object {}

    init {
        this.drawable = drawable

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

        // NB! important to call after our initialization in order
        //  to customize asset (for example size)
        block(this)
    }

    fun tint(@ColorInt color: Int) = this.apply {
        drawable = drawable.mutate().also { it.setTint(color) }
    }

    fun tint(colorStateList: ColorStateList) = this.apply {
        drawable = drawable.mutate().also { it.setTintList(colorStateList) }
    }

    override fun clone(): AssetShape = AssetShape(
        // try to create a new instance of resource, or if fails use original
        drawable.constantState?.newDrawable()?.mutate() ?: drawable
    )

    override fun toStringDedicatedProperties(): String = "drawable=$drawable"

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