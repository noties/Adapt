package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import io.noties.adapt.ui.util.Gravity

interface BaseLabelShapeData : CommonTextPaintData {
    var text: String?
    var textGravity: Gravity?
    var textRotation: Shape.Rotation?

    fun text(text: String?) = this.also {
        it.text = text
    }

    fun textGravity(textGravity: Gravity?) = this.also {
        it.textGravity = textGravity
    }

    fun textRotation(angle: Float, centerX: Float? = null, centerY: Float? = null) = this.also {
        it.textRotation = Shape.Rotation(
            angle,
            Dimension.Relative(centerX ?: 0.5F),
            Dimension.Relative(centerY ?: 0.5F)
        )
    }
}

data class LabelShapeData(
    override var textSize: Int? = null,
    override var textColor: Int? = null,
    override var textTypeface: Typeface? = null,
    override var textBold: Boolean? = null,
    override var textItalic: Boolean? = null,
    override var textUnderline: Boolean? = null,
    override var textStrikethrough: Boolean? = null,
    override var textShadow: Shape.Shadow? = null,
    override var textLetterSpacing: Float? = null,
    override var text: String? = null,
    override var textGravity: Gravity? = null,
    override var textRotation: Shape.Rotation? = null,
) : BaseLabelShapeData

// TODO: override all setter functions, as they start returning parent
// .also { add(it) } - won't work as data file is passed around

/**
 * A shape to draw _simple_ single-line text.
 * @see TextShape
 */
class LabelShape(
    text: String? = null,
    val data: LabelShapeData = LabelShapeData(text = text),
    block: LabelShape.() -> Unit = {}
) : RectangleShape(), BaseLabelShapeData by data {

    init {
        block(this)
    }

    private val cache = TextPaintCache()
    private val rect = Rect()

    override fun clone(): LabelShape {
        return LabelShape(data = data.copy())
    }

    override fun toStringDedicatedProperties(): String {
        return data.toString()
    }

    override fun drawSelf(canvas: Canvas, bounds: Rect) {
        super.drawSelf(canvas, bounds)

        val text = data.text ?: return

        val textPaint = cache.textPaint(data)

        // normal alignment, we would locate the whole text based on gravity
        textPaint.textAlign = Paint.Align.LEFT

        // measure text
        textPaint.getTextBounds(text, 0, text.length, rect)

        val textLeft = -rect.left
        val textTop = -rect.top

        val width = rect.width()
        val height = rect.height()

        (textGravity ?: Gravity.leading.top).also {
            android.view.Gravity.apply(
                it.value,
                width,
                height,
                bounds,
                rect
            )
        }

        drawRect().set(rect)

        textRotation?.also {
            it.draw(canvas, rect)
        }

        super.drawChildren(canvas, rect)

        canvas.drawText(
            text,
            rect.left.toFloat() + textLeft,
            rect.top.toFloat() + textTop,
            textPaint
        )
    }

    override fun drawChildren(canvas: Canvas, bounds: Rect) {
        // no op, children are drawn from drawSelf (before text content)
    }

    private class TextPaintCache {
        private val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)

        private var previousData: LabelShapeData? = null

        fun textPaint(data: LabelShapeData): TextPaint {
            if (previousData != data) {
                previousData = data.copy()
                data.applyTo(paint)
            }
            return paint
        }
    }
}