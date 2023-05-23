package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect

class LineShape(
    block: LineShape.() -> Unit = {}
) : Shape() {

    init {
        block(this)
    }

    var fromX: Dimension? = null
    var fromY: Dimension? = null

    var toX: Dimension? = null
    var toY: Dimension? = null

    private val pathCache = PathCache()

    fun from(x: Int, y: Int): LineShape = this.also {
        fromX = Dimension.Exact(x)
        fromY = Dimension.Exact(y)
    }

    fun fromRelative(x: Float, y: Float): LineShape = this.also {
        fromX = Dimension.Relative(x)
        fromY = Dimension.Relative(y)
    }

    fun to(x: Int, y: Int): LineShape = this.also {
        toX = Dimension.Exact(x)
        toY = Dimension.Exact(y)
    }

    fun toRelative(x: Float, y: Float): LineShape = this.also {
        toX = Dimension.Relative(x)
        toY = Dimension.Relative(y)
    }

    override fun clone(): LineShape = LineShape().also {
        it.fromX = fromX
        it.fromY = fromY
        it.toX = toX
        it.toY = toY
    }

    override fun toStringDedicatedProperties(): String {
        return "fromX=$fromX, fromY=$fromY, toX=$toX, toY=$toY"
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        // only draw stroke style (fill would be ignored)
        if (paint.style != Paint.Style.STROKE) {
            return
        }

        // we must have dimensions
        val fromX = this.fromX ?: return
        val fromY = this.fromY ?: return
        val toX = this.toX ?: return
        val toY = this.toY ?: return

        val w = bounds.width()
        val h = bounds.height()

        val l = bounds.left.toFloat()
        val t = bounds.top.toFloat()

        // we use path, as regular drawLine in some cases (non obvious)
        //  ignored dash effect
        val path = pathCache.set(
            l + fromX.resolve(w), t + fromY.resolve(h),
            l + toX.resolve(w), t + toY.resolve(h)
        )

        canvas.drawPath(path, paint)
    }

    private class PathCache(
        private val path: Path = Path(),
        private var fromX: Float = 0F,
        private var fromY: Float = 0F,
        private var toX: Float = 0F,
        private var toY: Float = 0F
    ) {
        fun set(fromX: Float, fromY: Float, toX: Float, toY: Float): Path {
            // the same values, no need to rebuild the path
            if (!path.isEmpty
                && this.fromX == fromX
                && this.fromY == fromY
                && this.toX == toX
                && this.toY == toY
            ) {
                return path
            }

            this.fromX = fromX
            this.fromY = fromY
            this.toX = toX
            this.toY = toY

            path.rewind()

            path.moveTo(fromX, fromY)
            path.lineTo(toX, toY)

            return path
        }
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setEmpty()
    }
}