package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.PositionsOfAngle
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import io.noties.debug.Debug
import kotlin.math.roundToInt

@AdaptSample(
    id = "20230518004635",
    title = "UI, LinearGradient interactive",
    tags = ["adapt-ui", "shape", "gradient", "interactive"]
)
class AdaptUILinearGradientSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        ZStack {
            val drawable = GradientAngleDrawable()
            View()
                .layout(FILL, 400)
                .layoutGravity(Gravity.center)
                .background(drawable)
                .onClick {
                    if (drawable.isRunning) {
                        drawable.stop()
                    } else {
                        drawable.start()
                    }
                }
        }
    }

    class GradientAngleDrawable(
        startAngle: Float = 0F,
        val width: Float = 0.5F,
        val height: Float = 0.5F
    ) : Drawable(), Animatable {
        override fun setAlpha(alpha: Int) = Unit
        override fun setColorFilter(colorFilter: ColorFilter?) = Unit

        @Suppress("OVERRIDE_DEPRECATION")
        override fun getOpacity(): Int = PixelFormat.OPAQUE

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.STROKE
            it.strokeWidth = 1.dip.toFloat()
        }

        private val handler = Handler(Looper.getMainLooper())

        private var angle: Float = startAngle % 360F
        private var isAnimating = false

        override fun draw(canvas: Canvas) {
            Debug.e("angle:$angle")

            val bounds = Rect()

            paint.color = Colors.black
            paint.style = Paint.Style.STROKE

            android.view.Gravity.apply(
                android.view.Gravity.CENTER,
                (this.bounds.width() * width).roundToInt(),
                (this.bounds.height() * height).roundToInt(),
                this.bounds,
                bounds
            )

            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()

            val radius = PositionsOfAngle.radius(bounds)

            fun drawCircle(point: PointF, color: Int) {
                paint.style = Paint.Style.FILL
                paint.color = color
                canvas.drawCircle(
                    point.x,
                    point.y,
                    3.dip.toFloat(),
                    paint
                )
            }

            val circleIntersections = PositionsOfAngle.circleIntersections(
                angle,
                bounds,
                radius
            )

            val intersections = PositionsOfAngle.positionsOfAngle(angle, bounds)

            paint.style = Paint.Style.FILL
            paint.shader = LinearGradient.angle(angle)
                .setColors(Colors.orange, Colors.accent)
                .createShader(bounds)
            canvas.drawRect(bounds, paint)

            paint.shader = null
            paint.color = Colors.black
            paint.style = Paint.Style.STROKE

            canvas.drawRect(bounds, paint)
            canvas.drawCircle(
                centerX,
                centerY,
                radius.toFloat(),
                paint
            )

            canvas.drawLine(
                intersections.first.x,
                intersections.first.y,
                intersections.second.x,
                intersections.second.y,
                paint
            )

            drawCircle(intersections.first, Color.RED)
            drawCircle(intersections.second, Color.GREEN)
            drawCircle(circleIntersections.first, Color.BLUE)
            drawCircle(circleIntersections.second, Color.MAGENTA)
        }

        override fun start() {
            isAnimating = true
            handler.removeCallbacksAndMessages(null)
            handler.post(runnable)
        }

        override fun stop() {
            isAnimating = false
            handler.removeCallbacksAndMessages(null)
        }

        override fun isRunning(): Boolean = isAnimating

        private val runnable = object : Runnable {
            override fun run() {
                angle = (angle + 0.5F) % 360F
                invalidateSelf()
                handler.postDelayed(this, 1000L / 60)
            }
        }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUILinearGradientSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUILinearGradientSample()
}