package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.toStringProperties

/**
 *
 */
class Corners(
    var leadingTop: Int = 0,
    var topTrailing: Int = 0,
    var trailingBottom: Int = 0,
    var bottomLeading: Int = 0,
    block: Corners.() -> Unit = {}
) : Shape() {

    init {
        block(this)
    }

    private val cache = Cache()

    override fun clone(): Corners = Corners(leadingTop, topTrailing, trailingBottom, bottomLeading)
    override fun toStringDedicatedProperties(): String = toStringProperties {
        it(::leadingTop, ::topTrailing, ::trailingBottom, ::bottomLeading)
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        canvas.drawPath(buildPath(bounds), paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        val path = buildPath(bounds)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path)
        } else {
            @Suppress("DEPRECATION")
            outline.setConvexPath(path)
        }
    }

    private fun buildPath(bounds: Rect): Path {
        return cache.path(this, bounds)
    }

    private class Cache {
        private val path = Path()

        private var leadingTop: Int = 0
        private var topTrailing: Int = 0
        private var trailingBottom: Int = 0
        private var bottomLeading: Int = 0

        private val rect = Rect()
        private val rectF = RectF()

        fun path(corners: Corners, bounds: Rect): Path {
            if (!path.isEmpty
                && rect == bounds
                && leadingTop == corners.leadingTop
                && topTrailing == corners.topTrailing
                && trailingBottom == corners.trailingBottom
                && bottomLeading == corners.bottomLeading
            ) {
                return path
            }

            rect.set(bounds)
            rectF.set(bounds)

            leadingTop = corners.leadingTop
            topTrailing = corners.topTrailing
            trailingBottom = corners.trailingBottom
            bottomLeading = corners.bottomLeading

            path.rewind()

            val lp = leadingTop.dip.toFloat()
            val tt = topTrailing.dip.toFloat()
            val tb = trailingBottom.dip.toFloat()
            val bl = bottomLeading.dip.toFloat()

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