package io.noties.adapt.sample.explore

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import io.noties.adapt.ui.shape.Dimension
import io.noties.adapt.ui.shape.Shape

object ExploreShapePath {
    class Path(block: Path.() -> Unit = {}) : Shape() {

        private val steps = mutableListOf<Step>()

        init {
            block(this)
        }

        fun move(x: Int, y: Int) = this.also {
            steps.add(Step.Move(Dimension.Exact(x), Dimension.Exact(y)))
        }

        fun line(x: Int, y: Int) = this.also {
            steps.add(Step.Line(Dimension.Exact(x), Dimension.Exact(y)))
        }

        // here we can add just center between start (previous) and end (x, y)
        // TODO: it is maybe better to supply ratio for xy, for example 0.2F and 0.7F
        //  what does this ratio represent? ratio/difference between start/end?
        fun quad(
            x: Pair<Int, Int>,
            y: Pair<Int, Int>,
        ) = this.also {
            steps.add(
                Step.Quad(
                    Dimension.Exact(x.first) to Dimension.Exact(x.second),
                    Dimension.Exact(y.first) to Dimension.Exact(y.second)
                )
            )
        }

        fun close() {
            steps.add(Step.Close)
        }

        // TODO: it is maybe better to supply ratio for xy, for example 0.2F and 0.7F
        // TODO: can we have a default values for control values?
//        fun cubic(
//            x: Int,
//            y: Int,
//            controlStart: Pair<Float, Float>,
//            controlEnd: Pair<Float, Float>
//        ) = this.also {
//
//        }

        private val lastRect = Rect()
        private val path = android.graphics.Path()

        override fun clone(): Shape {
            TODO("Not yet implemented")
        }

        override fun toStringDedicatedProperties(): String {
            TODO("Not yet implemented")
        }

        override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
            if (bounds != lastRect || path.isEmpty) {
                buildPath(bounds)
            }
            canvas.drawPath(path, paint)
        }

        private fun buildPath(bounds: Rect) {
            path.rewind()

            lastRect.set(bounds)

            val leading = bounds.left
            val top = bounds.top
            val width = bounds.width()
            val height = bounds.height()

            steps.forEach { step ->
                step.contribute(path, leading, top, width, height)
            }
        }

        // TODO: do all steps have x&y? can it be a shape? would we be able to extract last point?
        private sealed class Step() {
            abstract fun contribute(
                path: android.graphics.Path,
                leading: Int,
                top: Int,
                width: Int,
                height: Int
            )

//            object Zero : Step() {
//                override fun contribute(
//                    path: android.graphics.Path,
//                    leading: Int,
//                    top: Int,
//                    width: Int,
//                    height: Int
//                ) {
//                    // no op
//                }
//            }

            class Move(val x: Dimension, val y: Dimension) : Step() {
                override fun contribute(
                    path: android.graphics.Path,
                    leading: Int,
                    top: Int,
                    width: Int,
                    height: Int
                ) {
                    path.moveTo(
                        leading + x.resolve(width).toFloat(),
                        top + y.resolve(height).toFloat()
                    )
                }
            }

            class Line(val x: Dimension, val y: Dimension) : Step() {
                override fun contribute(
                    path: android.graphics.Path,
                    leading: Int,
                    top: Int,
                    width: Int,
                    height: Int
                ) {
                    path.lineTo(
                        leading + x.resolve(width).toFloat(),
                        top + y.resolve(height).toFloat()
                    )
                }
            }

            class Quad(
                val x: Pair<Dimension, Dimension>,
                val y: Pair<Dimension, Dimension>
            ) : Step() {
                override fun contribute(
                    path: android.graphics.Path,
                    leading: Int,
                    top: Int,
                    width: Int,
                    height: Int
                ) {
                    val endX = x.second.resolve(width)
                    val endY = y.second.resolve(height)
                    // first is control point
                    path.quadTo(
                        leading + x.first.resolve(width).toFloat(),
                        top + y.first.resolve(height).toFloat(),
                        leading + endX.toFloat(),
                        top + endY.toFloat()
                    )
                }
            }

            object Close : Step() {
                override fun contribute(
                    path: android.graphics.Path,
                    leading: Int,
                    top: Int,
                    width: Int,
                    height: Int
                ) {
                    path.close()
                }
            }
        }
    }
}