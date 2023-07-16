package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.util.dip
import io.noties.debug.Debug

object ExploreClipToOutline {
    class UIOutline(val outline: Outline) {

        enum class Mode {
            Empty,
            Rect,
            Path
        }

        var mode: Mode = Mode.Empty
            private set

        val rect: Rect get() = Rect(bounds)

        var path: Path? = null
            private set

        var alpha: Float? = null
            private set

        var radius: Float? = null
            private set

        private val bounds = Rect()

        fun setEmpty() {
            outline.setEmpty()
            setMode(Mode.Empty)
        }

        fun isEmpty() = outline.isEmpty

        fun canClip() = outline.canClip()

        fun setAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
            this.alpha = alpha
        }

        fun setRect(rect: Rect) {
            setRect(rect.left, rect.top, rect.right, rect.bottom)
        }

        fun setRect(left: Int, top: Int, right: Int, bottom: Int) {
            setRoundRect(left, top, right, bottom, 0F)
        }

        fun setRoundRect(rect: Rect, radius: Float) {
            setRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius)
        }

        fun setRoundRect(left: Int, top: Int, right: Int, bottom: Int, radius: Float) {
            outline.setRoundRect(left, top, right, bottom, radius)

            if (outline.isEmpty) {
                setMode(Mode.Empty)
                return
            }

            setMode(Mode.Rect)
            rect.set(left, top, right, bottom)
            this.radius = radius
        }

        fun setOval(rect: Rect) {
            setOval(rect.left, rect.top, rect.right, rect.bottom)
        }

        fun setOval(left: Int, top: Int, right: Int, bottom: Int) {
            outline.setOval(left, top, right, bottom)

            if (outline.isEmpty) {
                setMode(Mode.Empty)
                return
            }

            // now, we need to check if it's a circle and assign bounds with radius/2
            if ((bottom - top) == (right - left)) {
                // this would call wrapped outline..
                setMode(Mode.Rect)
                rect.set(left, top, right, bottom)
                radius = (bottom - top) / 2F
            } else {
                setMode(Mode.Path)
                this.path!!.addOval(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    Path.Direction.CW
                )
            }
        }

        fun setPath(path: Path) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                outline.setPath(path)
            } else {
                outline.setConvexPath(path)
            }

            if (outline.isEmpty) {
                setMode(Mode.Empty)
                return
            }

            setMode(Mode.Path)
            this.path!!.set(path)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
        fun offset(x: Int, y: Int) {
            outline.offset(x, y)
            when (mode) {
                Mode.Rect -> bounds.offset(x, y)
                Mode.Path -> path?.offset(x.toFloat(), y.toFloat())
                else -> Unit
            }
        }

        private fun setMode(mode: Mode) {
            path?.rewind()
            bounds.setEmpty()
            radius = null

            when (mode) {
                Mode.Empty -> {
                    alpha = null
                }
                Mode.Rect -> {
                    // no op
                }
                Mode.Path -> {
                    if (path == null) {
                        path = Path()
                    }
                }
            }
        }
    }

    class ClipToOutlineFrameLayout(context: Context) : FrameLayout(context) {

        var leadingTop: Int = 0
        var topTrailing: Int = 0
        var trailingBottom: Int = 0
        var bottomLeading: Int = 0

        // TODO: detect when outline changes? when provider is called?

        private var path: Path? = null
        private val rectF = RectF()
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = Color.WHITE
            it.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        }

        init {
            outlineProvider = SelfOutlineProvider()
            setLayerType(LAYER_TYPE_HARDWARE, null)

            addView(View(context).also {
                it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                it.setBackgroundColor(Color.BLACK)
            })

            var index = 0
            val values = PorterDuff.Mode.values()

            setOnClickListener {
                val next = values[(index + 1) % values.size]
                Debug.i("next:$next")
                index += 1
                paint.xfermode = PorterDuffXfermode(next)
                invalidate()
            }
        }

        inner class SelfOutlineProvider : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                Debug.i(view)
                if (view == this@ClipToOutlineFrameLayout) {
//                    val background = view.background
//                    if (background != null) {
//
//                    }

                    rectF.set(0F, 0F, width.toFloat(), height.toFloat())

                    val lp = leadingTop.dip.toFloat()
                    val tt = topTrailing.dip.toFloat()
                    val tb = trailingBottom.dip.toFloat()
                    val bl = bottomLeading.dip.toFloat()

                    val path =
                        this@ClipToOutlineFrameLayout.path?.also { it.rewind() } ?: kotlin.run {
                            Path().also { this@ClipToOutlineFrameLayout.path = it }
                        }

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

                    // NB! the return if we matched what we wanted
                    return
                }
                path = null
                BACKGROUND.getOutline(view, outline)
            }
        }

        // https://medium.com/appkode/clipping-in-android-quickly-qualitatively-cheap-3ccfd31d5d6b
        override fun dispatchDraw(canvas: Canvas) {
            Debug.i(path, rectF)
            val path = this.path ?: kotlin.run {
                super.dispatchDraw(canvas)
                return
            }

            val save = canvas.saveLayer(rectF, null)
            try {
                canvas.clipPath(path)
                super.dispatchDraw(canvas)
//                canvas.drawPath(path, paint)
            } finally {
                canvas.restoreToCount(save)
            }
        }
    }

//    class CornersShape(
//        var leadingTop: Int = 0,
//        var topTrailing: Int = 0,
//        var trailingBottom: Int = 0,
//        var bottomLeading: Int = 0,
//        block: CornersShape.() -> Unit = {}
//    ) : Shape() {
//
//        init {
//            block(this)
//        }
//
//        fun corners(
//            leadingTop: Int? = null,
//            topTrailing: Int? = null,
//            trailingBottom: Int? = null,
//            bottomLeading: Int? = null
//        ) = this.also {
//            leadingTop?.also { this.leadingTop = it }
//            topTrailing?.also { this.topTrailing = it }
//            trailingBottom?.also { this.trailingBottom = it }
//            bottomLeading?.also { this.bottomLeading = it }
//        }
//
//        private val cache = Cache()
//
//        override fun clone(): io.noties.adapt.ui.shape.CornersShape =
//            CornersShape(leadingTop, topTrailing, trailingBottom, bottomLeading)
//
//        override fun toStringDedicatedProperties(): String {
//            return "leadingTop=$leadingTop, topTrailing=$topTrailing, trailingBottom=$trailingBottom, bottomLeading=$bottomLeading"
//        }
//
//        override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
//            canvas.drawPath(buildPath(bounds), paint)
//        }
//
//        override fun outlineShape(outline: Outline, bounds: Rect) {
//            val path = buildPath(bounds)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                outline.setPath(path)
//            } else {
//                @Suppress("DEPRECATION")
//                outline.setConvexPath(path)
//            }
//        }
//
//        private fun buildPath(bounds: Rect): Path {
//            return cache.path(this, bounds)
//        }
//
//        private class Cache {
//            private val path = Path()
//
//            private var leadingTop: Int = 0
//            private var topTrailing: Int = 0
//            private var trailingBottom: Int = 0
//            private var bottomLeading: Int = 0
//
//            private val rect = Rect()
//            private val rectF = RectF()
//
//            fun path(corners: CornersShape, bounds: Rect): Path {
//                if (!path.isEmpty
//                    && rect == bounds
//                    && leadingTop == corners.leadingTop
//                    && topTrailing == corners.topTrailing
//                    && trailingBottom == corners.trailingBottom
//                    && bottomLeading == corners.bottomLeading
//                ) {
//                    return path
//                }
//
//                rect.set(bounds)
//                rectF.set(bounds)
//
//                leadingTop = corners.leadingTop
//                topTrailing = corners.topTrailing
//                trailingBottom = corners.trailingBottom
//                bottomLeading = corners.bottomLeading
//
//                path.rewind()
//
//                val lp = leadingTop.dip.toFloat()
//                val tt = topTrailing.dip.toFloat()
//                val tb = trailingBottom.dip.toFloat()
//                val bl = bottomLeading.dip.toFloat()
//
//                // TODO: we could manually do this:
//                //  1. see if radius is the same, if so -> rounded-rect
//                //  2. then... check the axis, and if the same add additional padding?
//                path.addRoundRect(
//                    rectF,
//                    floatArrayOf(
//                        lp, lp,
//                        tt, tt,
//                        tb, tb,
//                        bl, bl
//                    ),
//                    Path.Direction.CW
//                )
//
//                return path
//            }
//        }
//    }
}