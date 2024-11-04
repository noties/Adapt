package io.noties.adapt.ui.widget.grid

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import io.noties.adapt.ui.preview.PreviewBoundsDrawable
import io.noties.adapt.ui.preview.PreviewDrawable
import io.noties.adapt.ui.preview.PreviewPaddingDrawable
import io.noties.adapt.ui.preview.PreviewViewCustomization
import io.noties.adapt.ui.util.AbsDrawable
import io.noties.adapt.ui.util.dip

class GridSpacer : FrameLayout, PreviewViewCustomization {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun preview(color: Int, view: View): PreviewViewCustomization.Result {
        return PreviewViewCustomization.Preview(
            PreviewDrawable(
                listOf(
                    PreviewPaddingDrawable(color, view),
                    PreviewBoundsDrawable(color, true),
                    PreviewGridSpacerDrawable(color, view)
                )
            )
        )
    }

    private class PreviewGridSpacerDrawable(
        @ColorInt color: Int,
        private val view: View
    ) : AbsDrawable() {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.FILL
            it.color = color
            it.strokeWidth = 2.dip.toFloat()
            it.pathEffect = DashPathEffect(
                floatArrayOf(4.dip.toFloat(), 8.dip.toFloat()),
                0F
            )
        }

        override fun draw(canvas: Canvas) {
            val spans = (view.layoutParams as? GridRowLayout.LayoutParams)
                ?.spanColumns
                ?.takeIf { it > 1 } ?: return

            val w = bounds.width()
            val h = bounds.height()

            // horizontal
            run {
                val step = w / spans
                repeat(spans) {
                    val x = ((it + 1) * step).toFloat()
                    canvas.drawLine(
                        x,
                        0F,
                        x,
                        h.toFloat(),
                        paint
                    )
                }
            }
        }
    }
}