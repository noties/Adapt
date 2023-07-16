package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup.MarginLayoutParams
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onLongClick
import io.noties.adapt.ui.onViewPreDraw
import io.noties.adapt.ui.overlay
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.pivot
import io.noties.adapt.ui.pivotRelative
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.hex

@AdaptSample(
    id = "20230715160110",
    title = "Pivot",
    description = "Handling of pivot point (x,y) for scale and rotate",
    tags = ["adapt-ui"]
)
class AdaptUIPivotSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack(Gravity.center.horizontal) {

            Text("Click to rotate around pivot point.\nLong click to scale up or down")
                .textGravity(Gravity.center.horizontal)
                .textSize(16)
                .textColor(Colors.black)
                .padding(16)

            // Default pivot values (should be at view center)
            PivotElement()

            // Explicit pivot point at 16.dp x 16.dp
            PivotElement()
                .pivot(16, 16)

            // relative to view bounds equal pivot values for both x and y
            PivotElement()
                .pivotRelative(0.75F)

            // relative to view bounds pivot values
            PivotElement()
                .pivotRelative(0.25F, 0.75F)

            // Negative values are fine
            PivotElement()
                .pivotRelative(-1F, 0.5F)

        }.layoutFill()
            .noClip()
    }

    @Suppress("FunctionName")
    private fun <LP : MarginLayoutParams> ViewFactory<LP>.PivotElement() = Text("PIVOT")
        .textColor(Colors.black)
        .textSize(16)
        .layoutWrap()
        .background(Colors.yellow)
        .layoutMargin(16)
        .padding(16)
        .also { el ->

            val overlay = PivotIndicatorDrawable()

            el.overlay(overlay)
            el.onViewPreDraw {
                overlay.update(it.pivotX, it.pivotY)
            }
        }
        .also { el ->
            // on click let us rotate
            el.onClick {
                el.view.clearAnimation()
                el.view.animate()
                    .rotation(359F)
                    .setDuration(3600L)
                    .withEndAction { el.view.rotation = 0F }
                    .start()
            }
        }
        .also { el ->
            // on long click let's scale it up and then, after delay, down
            el.onLongClick {
                val (x, y) = if (el.view.scaleX != 1F) {
                    (1F to 1F)
                } else {
                    (2F to 2F)
                }
                el.view.clearAnimation()
                el.view.animate()
                    .scaleX(x)
                    .scaleY(y)
                    .setDuration(450L)
                    .start()
            }
        }

    private class PivotIndicatorDrawable : Drawable() {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.FILL
            it.color = Colors.pivot
        }

        fun update(x: Float, y: Float) {
            if (pivotX != x || pivotY != y) {
                this.pivotX = x
                this.pivotY = y
                invalidateSelf()
            }
        }

        private var pivotX: Float = 0F
        private var pivotY: Float = 0F

        override fun draw(canvas: Canvas) {
            canvas.drawCircle(pivotX, pivotY, 4.dip.toFloat(), paint)
        }

        override fun setAlpha(alpha: Int) = Unit
        override fun setColorFilter(colorFilter: ColorFilter?) = Unit

        @Suppress("OVERRIDE_DEPRECATION")
        override fun getOpacity(): Int = PixelFormat.OPAQUE
    }
}

@Suppress("unused")
private val Colors.pivot: Int
    get() = hex("#f00")

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIPivotSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIPivotSample()
}