package io.noties.adapt.ui.shape

import android.graphics.Typeface
import android.text.TextPaint
import androidx.annotation.ColorInt
import io.noties.adapt.ui.util.dip

interface CommonTextPaintData {
    var textSize: Int?
    var textColor: Int?
    var textTypeface: Typeface?
    var textBold: Boolean?
    var textItalic: Boolean?
    var textUnderline: Boolean?
    var textStrikethrough: Boolean?
    var textShadow: Shape.Shadow?
    var textLetterSpacing: Float?

    /**
     * Apply properties to supplied [TextPaint]
     */
    fun applyTo(textPaint: TextPaint, density: Float) {
        textSize?.dip(density)?.toFloat()?.also { textPaint.textSize = it }
        textColor?.also { textPaint.color = it }

        val typeface = textTypeface
        if (typeface != null) {

            val bold = textBold ?: false
            val italic = textItalic ?: false

            val style = when {
                bold && italic -> Typeface.BOLD_ITALIC
                bold -> Typeface.BOLD
                italic -> Typeface.ITALIC
                else -> null
            }

            textPaint.isFakeBoldText = false
            textPaint.textSkewX = 0F
            textPaint.typeface = if (style != null) {
                Typeface.create(typeface, style)
            } else {
                typeface
            }
        } else {
            // emulate bold
            textPaint.isFakeBoldText = textBold ?: false
            // emulate italic
            textPaint.textSkewX = textItalic?.let { -0.25F } ?: 0F
            textPaint.typeface = null
        }

        textPaint.isUnderlineText = textUnderline ?: false
        textPaint.isStrikeThruText = textStrikethrough ?: false

        // resolve(0) is a way to ensure only Exact dimensions
        textShadow
            ?.let { it to (it.radius?.resolve(0, density) ?: 0) }
            ?.takeIf { it.second > 0 }
            ?.also { (shadow, radius) ->
                val x = shadow.offsetX?.resolve(0, density) ?: 0
                val y = shadow.offsetY?.resolve(0, density) ?: 0
                textPaint.setShadowLayer(
                    radius.toFloat(),
                    x.toFloat(),
                    y.toFloat(),
                    shadow.color ?: textPaint.color
                )
            }
        // else clear shadow layer
            ?: kotlin.run { textPaint.clearShadowLayer() }

        textPaint.letterSpacing = textLetterSpacing ?: 0F
    }
}

@Suppress("UNCHECKED_CAST")
interface CommonTextPaintDataSetter<THIS : CommonTextPaintData> : CommonTextPaintData {
    /**
     * Set text size in SP
     * @see TextPaint.setTextSize
     */
    fun textSize(
        textSize: Int?
    ) = (this as THIS).also { it.textSize = textSize }

    /**
     * Set text color
     * @see TextPaint.setColor
     */
    fun textColor(
        @ColorInt textColor: Int?
    ) = (this as THIS).also { it.textColor = textColor }

    /**
     * Set text Typeface
     * @see TextPaint.setTypeface
     */
    fun textTypeface(
        textTypeface: Typeface?
    ) = (this as THIS).also { it.textTypeface = textTypeface }

    /**
     * Apply bold style. If no typeface is available, then [TextPaint.setFakeBoldText] is going to be used
     * @see TextPaint.setFakeBoldText
     */
    fun textBold(
        textBold: Boolean? = true
    ) = (this as THIS).also { it.textBold = textBold }

    /**
     * Apply italic style. If no typeface is available, then [TextPaint.setTextSkewX] is going to be used
     * @see TextPaint.setTextSkewX
     */
    fun textItalic(
        textItalic: Boolean? = true
    ) = (this as THIS).also { it.textItalic = textItalic }

    /**
     * Apply underline
     * @see TextPaint.setUnderlineText
     */
    fun textUnderline(
        textUnderline: Boolean? = true
    ) = (this as THIS).also {
        it.textUnderline = textUnderline
    }

    /**
     * Apply strike-through
     * @see TextPaint.setStrikeThruText
     */
    fun textStrikethrough(
        textStrikethrough: Boolean? = true
    ) = (this as THIS).also { it.textStrikethrough = textStrikethrough }

    /**
     * By default uses text color as shadow color.
     * NB! only uses exact dimensions
     * @see TextPaint.setShadowLayer
     * @see Dimension.Exact
     */
    fun textShadow(
        radius: Int,
        @ColorInt color: Int? = null,
        offsetX: Int? = null,
        offsetY: Int? = null
    ) = (this as THIS).also {
        it.textShadow = Shape.Shadow(
            Dimension.Exact(radius),
            color,
            offsetX?.let { p -> Dimension.Exact(p) },
            offsetY?.let { p -> Dimension.Exact(p) }
        )
    }

    /**
     * Apply letter spacing in EM, negative values shrink letters
     * @see TextPaint.setLetterSpacing
     */
    fun textLetterSpacing(
        em: Float?
    ) = (this as THIS).also {
        it.textLetterSpacing = em
    }
}