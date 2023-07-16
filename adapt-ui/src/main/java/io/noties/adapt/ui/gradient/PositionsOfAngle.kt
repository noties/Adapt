package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.CheckResult
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object PositionsOfAngle {
    fun positionsOfAngle(angle: Float, bounds: Rect): Pair<PointF, PointF> {

        val radius = radius(bounds)

        val rectF = RectF(bounds)

        val circleIntersections = circleIntersections(angle, bounds, radius)
        val circleEquation = Equation(circleIntersections)

        fun rectangleIntersections(
            circleEquation: Equation,
            line: Pair<PointF, PointF>
        ): PointF? {
            val eq1 = circleEquation
            val eq2 = Equation(line)

            val det = eq1.a * eq2.b - eq2.a * eq1.b
            if (det == 0F) {
                return null
            }

            val x = (eq2.b * eq1.c - eq1.b * eq2.c) / det
            val y = (eq1.a * eq2.c - eq2.a * eq1.c) / det

            return PointF(x, y)
        }

        val rectangleLines = listOf(
            PointF(rectF.left, rectF.top) to PointF(rectF.right, rectF.top),
            PointF(rectF.right, rectF.top) to PointF(rectF.right, rectF.bottom),
            PointF(rectF.left, rectF.bottom) to PointF(rectF.right, rectF.bottom),
            PointF(rectF.left, rectF.top) to PointF(rectF.left, rectF.bottom)
        )

        // NB! inset the rectF a little to adjust to floating point operators
        rectF.inset(-0.01F, -0.01F)

        val list = rectangleLines
            .mapNotNull { rectangleIntersections(circleEquation, it) }
            .filter { rectF.contains(it) }
            .take(2)
            .sortedWith { lhs, rhs ->
                val point = circleIntersections.first
                val lx = abs(point.x - lhs.x)
                val rx = abs(point.x - rhs.x)
                val compare = lx.compareTo(rx)
                if (compare != 0) {
                    compare
                } else {
                    val ly = abs(point.y - lhs.y)
                    val ry = abs(point.y - rhs.y)
                    ly.compareTo(ry)
                }
            }

        require(list.size >= 2) {
            "Cannot find intersection points for angle:$angle bounds:${bounds.toShortString()} result:$list"
        }

        return list[0] to list[1]
    }

    // Radius of a circle with a inscribed rectangle (bounds)
    @CheckResult
    fun radius(bounds: Rect): Int {
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()
        return (sqrt(w * w + h * h) / 2F).roundToInt()
    }

    fun circleIntersections(
        angle: Float,
        bounds: Rect,
        radius: Int = radius(bounds)
    ): Pair<PointF, PointF> {
        fun point(angle: Float): PointF {
            val rad = Math.toRadians((270 + angle) % 360.0)
            return PointF(
                bounds.centerX() + (radius * cos(rad)).toFloat(),
                bounds.centerY() + (radius * sin(rad)).toFloat()
            )
        }
        return point(angle) to point(angle + 180F)
    }

    data class Equation(
        val a: Float,
        val b: Float,
        val c: Float
    ) {
        companion object {
            @CheckResult
            operator fun invoke(intersections: Pair<PointF, PointF>): Equation {
                val a = intersections.second.y - intersections.first.y
                val b = intersections.first.x - intersections.second.x
                val c = (a * intersections.first.x) + (b * intersections.first.y)
                return Equation(a, b, c)
            }
        }
    }

    internal fun RectF.contains(point: PointF): Boolean {
        return point.x in left..right && point.y in top..bottom
    }
}