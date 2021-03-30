package io.noties.adapt.sample.ui

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import io.noties.adapt.sample.R

object DividerOverlay {
    fun init(view: View) {
        if (view.getTag(R.id.divider_overlay_drawable) != null) return

        val r = view.resources
        DividerDrawable(
            r.getDimensionPixelSize(R.dimen._16),
            r.getDimensionPixelSize(R.dimen._1),
            ContextCompat.getColor(view.context, R.color.divider)
        ).also {
            ViewDimensionBoundsListener.init(view, it)
            view.setTag(R.id.divider_overlay_drawable, it)
            view.overlay.add(it)
        }
    }

    class ViewDimensionBoundsListener(val drawable: Drawable) : View.OnLayoutChangeListener {

        companion object {
            fun init(view: View, drawable: Drawable) {
                view.addOnLayoutChangeListener(ViewDimensionBoundsListener(drawable))
            }
        }

        override fun onLayoutChange(
            v: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            drawable.setBounds(0, 0, v.width, v.height)
        }
    }

    class DividerDrawable(
        @Px private val left: Int,
        @Px private val height: Int,
        @ColorInt color: Int
    ) : Drawable() {

        private val rect = Rect()
        private val paint = Paint().also {
            it.isAntiAlias = true
            it.color = color
            it.style = Paint.Style.FILL
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)

            rect.set(left, bounds.bottom - height, bounds.right, bounds.bottom)
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRect(rect, paint)
        }

        override fun setAlpha(alpha: Int) = Unit
        override fun setColorFilter(colorFilter: ColorFilter?) = Unit
        override fun getOpacity(): Int = PixelFormat.OPAQUE
    }
}