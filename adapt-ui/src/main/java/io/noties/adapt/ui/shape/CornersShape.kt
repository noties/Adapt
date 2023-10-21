package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import io.noties.adapt.ui.util.dip

/**
 *
 */
class CornersShape(
    var leadingTop: Int = 0,
    var topTrailing: Int = 0,
    var trailingBottom: Int = 0,
    var bottomLeading: Int = 0,
    block: CornersShape.() -> Unit = {}
) : Shape() {

    init {
        block(this)
    }

    fun corners(
        leadingTop: Int? = null,
        topTrailing: Int? = null,
        trailingBottom: Int? = null,
        bottomLeading: Int? = null
    ) = this.also {
        leadingTop?.also { this.leadingTop = it }
        topTrailing?.also { this.topTrailing = it }
        trailingBottom?.also { this.trailingBottom = it }
        bottomLeading?.also { this.bottomLeading = it }
    }

    private val cache = Cache()

    override fun clone(): CornersShape =
        CornersShape(leadingTop, topTrailing, trailingBottom, bottomLeading)

    override fun toStringDedicatedProperties(): String {
        return "leadingTop=$leadingTop, topTrailing=$topTrailing, trailingBottom=$trailingBottom, bottomLeading=$bottomLeading"
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint, density: Float) {
        canvas.drawPath(buildPath(bounds, density), paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect, density: Float) {
        val path = buildPath(bounds, density)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path)
        } else {
            @Suppress("DEPRECATION")
            outline.setConvexPath(path)
        }
    }

    private fun buildPath(bounds: Rect, density: Float): Path {
        return cache.path(this, bounds, density)
    }

    private class Cache {
        private val path = Path()

        private var density: Float = 0F
        private var leadingTop: Int = 0
        private var topTrailing: Int = 0
        private var trailingBottom: Int = 0
        private var bottomLeading: Int = 0

        private val rect = Rect()
        private val rectF = RectF()

        fun path(corners: CornersShape, bounds: Rect, density: Float): Path {
            if (!path.isEmpty
                && rect == bounds
                && leadingTop == corners.leadingTop
                && topTrailing == corners.topTrailing
                && trailingBottom == corners.trailingBottom
                && bottomLeading == corners.bottomLeading
                && density == density
            ) {
                return path
            }

            rect.set(bounds)
            rectF.set(bounds)

            this.density = density
            leadingTop = corners.leadingTop
            topTrailing = corners.topTrailing
            trailingBottom = corners.trailingBottom
            bottomLeading = corners.bottomLeading

            path.rewind()

            val lp = leadingTop.dip(density).toFloat()
            val tt = topTrailing.dip(density).toFloat()
            val tb = trailingBottom.dip(density).toFloat()
            val bl = bottomLeading.dip(density).toFloat()

            path.addRoundRect(
                rectF,
                floatArrayOf(
                    lp, lp,
                    tt, tt,
                    tb, tb,
                    bl, bl
                ),
                Path.Direction.CW
            )

            return path
        }
    }
}