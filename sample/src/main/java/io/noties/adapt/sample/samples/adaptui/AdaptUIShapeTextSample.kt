package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewUILayout
import io.noties.adapt.sample.util.withAlphaComponent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.JustificationMode
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230515172204",
    title = "[Explore] Text Shape",
    "Shape that draws text",
    tags = ["shape"]
)
class AdaptUIShapeTextSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            View()
                .layout(FILL, 256)
                .ifAvailable(Build.VERSION_CODES.O) {
                    it.background(createTextShape())
                }

            val drawable = ExploreGradientAngleDrawable()
            View()
                .layout(FILL, 256)
                .background(drawable)
                .onClick {
                    if (drawable.isRunning) {
                        drawable.stop()
                    } else {
                        drawable.start()
                    }
                }

//            View()
//                .layout(FILL, 256)
//                .background(object : Drawable() {
//
//                    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
//                        it.style = Paint.Style.STROKE
//                        it.strokeWidth = 1.dip.toFloat()
//                    }
//
//                    private val handler = Handler(Looper.getMainLooper())
//                    private var angle: Float = 180F
//
//                    fun radius(bounds: Rect): Int {
//                        val w = bounds.width().toFloat()
//                        val h = bounds.height().toFloat()
//                        return (sqrt(w * w + h * h) / 2F).roundToInt()
//                    }
//
//                    override fun draw(canvas: Canvas) {
//                        val bounds = Rect()
//
//                        paint.color = Colors.black
//                        paint.style = Paint.Style.STROKE
//
//                        android.view.Gravity.apply(
//                            android.view.Gravity.CENTER,
//                            (this.bounds.width() * 0.5F).roundToInt(),
//                            (this.bounds.height() * 0.5F).roundToInt(),
//                            this.bounds,
//                            bounds
//                        )
//
//                        val centerX = bounds.centerX().toFloat()
//                        val centerY = bounds.centerY().toFloat()
//
//                        canvas.drawRect(bounds, paint)
//
//                        val radius = radius(bounds)
//                        canvas.drawCircle(
//                            centerX,
//                            centerY,
//                            radius.toFloat(),
//                            paint
//                        )
//
//                        Debug.e("angle:$angle")
//
//                        fun intersections(angle: Float): List<Pair<Float, Float>> {
//                            if (true) {
//                                fun point(angle: Float): Pair<Float, Float> {
//                                    val rad = Math.toRadians((270 + angle) % 360.0)
//                                    return (radius * Math.cos(rad)).toFloat() to (radius * Math.sin(
//                                        rad
//                                    )).toFloat()
//                                }
//                                return listOf(
//                                    point(angle).let { bounds.centerX() + it.first to bounds.centerY() + it.second },
//                                    point(angle + 180F).let { bounds.centerX() + it.first to bounds.centerY() + it.second }
//                                )
//                            }
//
//                            // we need to handle 0, 90, 180 and 270 separately
//                            val slope = Math.tan(Math.toRadians(angle.toDouble()))
//                            return listOf(
//                                centerX - radius to (centerY + (radius * slope)).toFloat(),
//                                centerX + radius to (centerY - (radius * slope)).toFloat()
//                            ).let { if (angle > 180F) it.reversed() else it }
//                        }
//
//                        // check if inside circle...
//
//                        fun lineIntersection(
//                            circle: List<Pair<Float, Float>>,
//                            line: List<Pair<Float, Float>>
//                        ): Pair<Float, Float> {
//                            data class Eq(val A: Float, val B: Float, val C: Float)
//
//                            fun create(line: List<Pair<Float, Float>>): Eq {
//                                val A = line[1].second - line[0].second
//                                val B = line[0].first - line[1].first
//                                val C = (A * line[0].first) + (B * line[0].second)
//                                return Eq(A, B, C)
//                            }
//
//                            val eq1 = create(circle)
//                            val eq2 = create(line)
//
//                            /*
//                            double det = A1 * B2 - A2 * B1
//                            if (det == 0) {
//                              //Lines are parallel
//                            } else {
//                              double x = (B2 * C1 - B1 * C2) / det
//                              double y = (A1 * C2 - A2 * C1) / det
//                            }
//                             */
//                            val det = eq1.A * eq2.B - eq2.A * eq1.B
//                            if (det == 0F) {
//                                return Float.NaN to Float.NaN
//                            }
////                            Debug.i("det:$det eq1:$eq1 eq2:$eq2")
//
//                            val x = (eq2.B * eq1.C - eq1.B * eq2.C) / det
//                            val y = (eq1.A * eq2.C - eq2.A * eq1.C) / det
//
//                            return x to y
//                        }
//
//                        // TODO: order, > 180 should go from bottom (now the same)
////                        fun rectangleIntersections(
////                            intersections: List<Pair<Float, Float>>
////                        ): List<Pair<Float, Float>> {
////                            /*
////                            double m1 = (lineEnd.y - lineStart.y) / (lineEnd.x - lineStart.x); // Slope of the line
////                            double m2 = (rectangleVertex2.y - rectangleVertex1.y) / (rectangleVertex2.x - rectangleVertex1.x); // Slope of the rectangle side
////
////                            double x = (rectangleVertex1.y - lineStart.y + (m1 * lineStart.x) - (m2 * rectangleVertex1.x)) / (m1 - m2);
////                            double y = lineStart.y + (m1 * (x - lineStart.x));
////                             */
////                            // slope of the line
////                            val m1 =
////                                (intersections[1].second - intersections[0].second) / (intersections[1].first - intersections[0].first)
////                            // slopes of rectangle sides
////                            val m2 = (bounds.bottom - bounds.top) / (bounds.right - bounds.left)
////                            val m3 = (bounds.top - bounds.bottom) / (bounds.left - bounds.right)
////
////                            val x2 =
////                                (bounds.top - intersections[0].second + (m1 * intersections[0].first) - (m2 * bounds.left)) / (m1 - m2)
////                            val y2 = intersections[0].second + (m1 * (x2 - intersections[0].first))
////
////                            val x3 =
////                                (bounds.bottom - intersections[0].second + (m1 * intersections[0].first) - (m3 * bounds.right)) / (m1 - m3)
////                            val y3 = intersections[0].second + (m1 * (x3 - intersections[0].first))
////
////                            return listOf(
////                                x2 to y2,
////                                x3 to y3
////                            )
////                        }
//
////                        val angle: Float = 245F
//
////                        if (false) {
////                            val rad = Math.toRadians(abs(270 + angle) % 360.0)
////                            val x = (radius * Math.cos(rad)).toFloat()
////                            val y = (radius * Math.sin(rad)).toFloat()
////
////                            canvas.drawCircle(
////                                bounds.centerX() + x,
////                                bounds.centerY() + y,
////                                3.dip.toFloat(),
////                                paint.also {
////                                    it.style = Paint.Style.FILL
////                                    it.color = Colors.orange
////                                }
////                            )
////
////                            return
////                        }
//
//                        val intersections = intersections(angle)
//                        Debug.i("intersections:$intersections")
//                        canvas.drawLine(
//                            intersections[0].first,
//                            intersections[0].second,
//                            intersections[1].first,
//                            intersections[1].second,
//                            paint
//                        )
//                        for ((i, ri) in intersections.withIndex()) {
//                            canvas.drawCircle(
//                                ri.first,
//                                ri.second,
//                                2.dip.toFloat(),
//                                paint.also {
//                                    it.style = Paint.Style.FILL
//                                    it.color =
//                                        if (i % 2 == 0) Color.parseColor("#ff0000") else Color.parseColor(
//                                            "#00ff00"
//                                        )
//                                }
//                            )
//                        }
//
//                        fun RectF.contains(pair: Pair<Float, Float>): Boolean {
//                            return pair.first in left..right
//                                    && pair.second in top..bottom
//                        }
//
//                        val lineIntersections = run {
//                            val boundsF = RectF(bounds)
//                            // how to pick closest to intersection?
//                            val lines = listOf(
//                                listOf(
//                                    boundsF.left to boundsF.top,
//                                    boundsF.right to boundsF.top
//                                ),
//                                listOf(
//                                    boundsF.right to boundsF.top,
//                                    boundsF.right to boundsF.bottom
//                                ),
//                                listOf(
//                                    boundsF.left to boundsF.bottom,
//                                    boundsF.right to boundsF.bottom
//                                ),
//                                listOf(
//                                    boundsF.left to boundsF.top,
//                                    boundsF.left to boundsF.bottom
//                                )
//                            )
//                            lines
//                                .map { lineIntersection(intersections, it) }
//                                .also {
//                                    // adjust for floating point operations
//                                    boundsF.inset(-0.1F, -0.1F)
//                                    it.forEach {
//                                        Debug.i("coord:$it bounds.contains:${boundsF.contains(it)} bounds:${boundsF.toShortString()}")
//                                    }
//                                }
//                                .filter { boundsF.contains(it) }
//                                .take(2)
//                                .sortedWith { lhs, rhs ->
//                                    val lx = abs(intersections[0].first - lhs.first)
//                                    val rx = abs(intersections[0].first - rhs.first)
//                                    val cx = lx.compareTo(rx)
//                                    if (cx != 0) {
//                                        Debug.i("compare.x($cx) [0]:${intersections[0]} lx:$lx rx:$rx lhs:$lhs rhs:$rhs")
//                                        cx
//                                    } else {
//                                        val ly = abs(intersections[0].second - lhs.second)
//                                        val ry = abs(intersections[0].second - rhs.second)
//                                        val cy = ly.compareTo(ry)
//                                        Debug.i("compare.y($cy) [0]:${intersections[0]}  ly:$ly ry:$ry lhs:$lhs rhs:$rhs")
//                                        ly.compareTo(ry)
//                                    }
//                                }
//                        }
//
////                        val lineIntersections = lineIntersection(
////                            intersections,
////                            listOf(
////                                bounds.left.toFloat() to bounds.top.toFloat(),
////                                bounds.right.toFloat() to bounds.top.toFloat()
////                            )
////                        )
////                        if (true) {
////                            canvas.drawCircle(
////                                lineIntersections.first,
////                                lineIntersections.second,
////                                3.dip.toFloat(),
////                                paint.also {
////                                    it.style = Paint.Style.FILL
////                                    it.color = Color.parseColor("#0000ff")
////                                }
////                            )
////                        }
//
//                        val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
//                            it.style = Paint.Style.FILL
//                            it.shader = android.graphics.LinearGradient(
//                                lineIntersections[0].first,
//                                lineIntersections[0].second,
//                                lineIntersections[1].first,
//                                lineIntersections[1].second,
//                                Colors.orange,
//                                Colors.accent,
//                                Shader.TileMode.CLAMP
//                            )
//                        }
//                        canvas.drawRect(bounds, gradientPaint)
//
//                        for ((i, ri) in lineIntersections.withIndex()) {
//                            Debug.i("i:$i ri:$ri")
//                            canvas.drawCircle(
//                                ri.first,
//                                ri.second,
//                                4.dip.toFloat(),
//                                paint.also {
//                                    it.style = Paint.Style.FILL
//                                    it.color =
//                                        if (i % 2 == 0) Color.parseColor("#0000ff") else Color.parseColor(
//                                            "#00ffff"
//                                        )
//                                }
//                            )
//                        }
//
////                        val rectangleIntersections = rectangleIntersections(intersections)
////                        for ((i, ri) in rectangleIntersections.withIndex()) {
////                            canvas.drawCircle(
////                                ri.first,
////                                ri.second,
////                                2.dip.toFloat(),
////                                paint.also {
////                                    it.style = Paint.Style.STROKE
////                                    it.color =
////                                        if (i % 2 == 0) Color.parseColor("#ff0000") else Color.parseColor(
////                                            "#00ff00"
////                                        )
////                                }
////                            )
////                        }
//
////                        Debug.i("intersections=${intersections}")
////                        Debug.i("rectangleIntersections=${rectangleIntersections}")
////
////                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                            ExploreShapeText.Text {
////                                text =
////                                    "intersections=${intersections}\nrectangleIntersections=${rectangleIntersections}"
////                                textSize = 14
////                                textColor = Colors.black
////                            }.draw(canvas, this.bounds)
////                        }
//
//                        if (angle < 360F) {
//                            handler.postDelayed({
//                                angle += 0.5F
//                                invalidateSelf()
//                            }, 1000L)
//                        }
//                    }
//
//                    override fun setAlpha(alpha: Int) = Unit
//                    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
//                    override fun getOpacity(): Int = 0
//                })

            View()
                .layout(FILL, 256)
                .ifAvailable(Build.VERSION_CODES.O) {
                    it.background(Rectangle {

                        add(ExploreShapeText.Text {
                            text = "NEW!"
                            textSize = 18
                            textColor = Colors.black
                            // TODO: without it... text is not properly placed
                            textAlignment = Layout.Alignment.ALIGN_CENTER
                            textShadow = ExploreShapeText.TextDataBase.TextShadow(
                                8,
                                0,
                                0,
                                Colors.white
                            )

                            fill(Colors.orange)
                            gravity(Gravity.top.trailing)
                            padding(48, 12)
                            rotate(45F)
//                            rotateRelative(45F, 1F, 0F)
                            translate(x = 48)
                        })

                        add(Rectangle {
                            sizeRelative(0.5F, 0.5F)
                            gravity(Gravity.center)
                            fill(Colors.accent.withAlphaComponent(0.2F))

                            add(ExploreShapeText.Text {
                                text = "I'm limited to parent, but would it be OK? ".repeat(10)
                                textSize = 16
                                textColor = Colors.black
                                textGradient = LinearGradient.edges { leading to trailing }
                                    .setColors(Colors.primary, Colors.accent)
                                textAlignment = Layout.Alignment.ALIGN_CENTER
                                textJustificationMode = JustificationMode.interWord
                                textHyphenationFrequency = HyphenationFrequency.full
                                textBreakStrategy = BreakStrategy.highQuality
                                textShadow = ExploreShapeText.TextDataBase.TextShadow(
                                    8,
                                    0,
                                    0,
                                    Colors.black
                                )

//                                fill(Colors.orange.withAlphaComponent(0.2F))
                                gravity(Gravity.center)
                                padding(4)
                            })
                        })
                    })
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTextShape() = Rectangle {
        fill(Colors.orange.withAlphaComponent(0.2F))

        add(ExploreShapeText.Text {
            text = SpannableStringBuilder().apply {
//                append("Hello there!\uD83D\uDC23\n")
//                append("How ")
//                append("are", StyleSpan(Typeface.BOLD), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
//                append(" you?\nWhat if it is this hard?")
                append("Hello ".repeat(5))
            }
            textSize = 24
            textColor = Colors.white
            textAlignment = Layout.Alignment.ALIGN_CENTER
//            size(128)
            textMaxLines = 1
            textEllipsize = TextUtils.TruncateAt.END

            background = RoundedRectangle(12)

//            size(56, 56)
            // fill/stroke does not work... because it uses paint to draw shape..
            stroke(Colors.white, 1, 4, 1)
            fill(Colors.primary)
//            size(height = 128)
            sizeRelative(0.5F, 0.75F)
            gravity(Gravity.center)
            shadow(Colors.black, 12)
            padding(12) // padding is not applied to target rect...
//            translate(x = -16, y = -16)
//            rotate(90F)
//            rotateRelative(90F, 1F, 1F)
//            padding(24)
        })
    }
}

private class PreviewAdaptUIShapeTextSample(context: Context, attrs: AttributeSet?) :
    PreviewUILayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        with(AdaptUIShapeTextSample()) {
            body()
        }
    }
}