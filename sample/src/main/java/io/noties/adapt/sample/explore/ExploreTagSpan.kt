package io.noties.adapt.sample.explore

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.SpannableStringBuilder
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.textSecondary
import io.noties.adapt.sample.ui.text.body
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout
import io.noties.adapt.ui.util.dip
import kotlin.math.roundToInt

object ExploreTagSpan {

    class TagSpan : ReplacementSpan() {
        companion object {
            val paddingHorizontal = 8.dip
            val paddingVertical = 4.dip
            val innerSpacing = 4.dip
            val cornerRadius = 6.dip
            const val hash = "#"
        }

        // rounded-rectangle with some internal padding
        // a hash sign | tag
        // height is the same as original

        private val rectF = RectF()
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.STROKE
            // TODO: change to setting
            it.color = Colors.black
            it.strokeWidth = 2.dip.toFloat()
        }

        private val hashTextColor = Colors.textSecondary

        override fun getSize(
            paint: Paint,
            text: CharSequence?,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
        ): Int {
            return getSizeInner(
                paint = paint,
                text = text,
                start = start,
                end = end,
                fm = fm
            ).width.roundToInt()
        }

        private data class SizeInner(
            val width: Float,
            val hashWidth: Float
        )

        private fun getSizeInner(
            paint: Paint,
            text: CharSequence?,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt? = null
        ): SizeInner {
            val hashWidth = paint.measureText(hash)
            val tagWidth = paint.measureText(text, start, end)
            val width = (
                    paddingHorizontal + hashWidth + innerSpacing + tagWidth + paddingHorizontal
                    )
            fm?.also {
                it.bottom = (it.bottom ?: 0) + (paddingVertical)
            }
            return SizeInner(width, hashWidth)
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val (width, hashWidth) = getSizeInner(paint, text, start, end)
            val half = this.paint.strokeWidth / 2F
            rectF.set(
                x,
                top.toFloat(),
                x + width,
                bottom.toFloat()
            )
            rectF.inset(half, half)

            canvas.drawRoundRect(
                rectF,
                cornerRadius.toFloat(),
                cornerRadius.toFloat(),
                this.paint
            )

            val hashY = run {
                val h = bottom - top
                val d = paint.fontMetricsInt.descent
                val a = paint.fontMetricsInt.ascent
                ((h / 2) - ((d + a) / 2))
            }

            val color = paint.color
            paint.color = hashTextColor
            try {
                canvas.drawText(
                    hash,
                    x + paddingHorizontal,
                    hashY.toFloat(),
                    paint
                )
            } finally {
                paint.color = color
            }

            canvas.drawText(
                text,
                start,
                end,
                x + paddingHorizontal + hashWidth + innerSpacing,
                y.toFloat(),
                paint
            )
        }
    }
}

private class PreviewTagSpan(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text()
                .padding(16)
                .textSize { body }
                .textColor { text }
                .onView {
                    val ssb = SpannableStringBuilder().apply {
                        append("hello", ExploreTagSpan.TagSpan(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                        append(" ")
                        append("another-one", ExploreTagSpan.TagSpan(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                        append(" ")
                        append("some text")
                    }
                    it.text = ssb
                }

        }.indent()
            .layoutFill()
            .noClip()
    }
}