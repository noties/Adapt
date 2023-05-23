package io.noties.adapt.ui.shape

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.JustificationMode
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import kotlin.math.roundToInt

//@formatter:off
interface TextShapeDataBase {
    var text: CharSequence?
    var textSize: Int?
    var textColor: Int?
    var textGradient: Gradient?
    var textTypeface: Typeface?
    var textBold: Boolean?
    var textItalic: Boolean?
    var textUnderline: Boolean?
    var textStrikethrough: Boolean?
    var textGravity: Gravity?
    var textRotation: Shape.Rotation?
    var textShadow: Shape.Shadow?
    var textMaxLines: Int?
    var textEllipsize: TextUtils.TruncateAt?
    var textBreakStrategy: BreakStrategy?
    var textHyphenationFrequency: HyphenationFrequency?
    var textJustificationMode: JustificationMode?
    var textLineSpacingAdd: Int?
    var textLineSpacingMultiplier: Float?
    var textLetterSpacing: Float?

    fun text(text: CharSequence?): TextShapeDataBase
    fun textSize(textSize: Int?): TextShapeDataBase
    fun textColor(textColor: Int?): TextShapeDataBase
    fun textGradient(textGradient: Gradient?): TextShapeDataBase
    fun textTypeface(textTypeface: Typeface?): TextShapeDataBase
    fun textBold(textBold: Boolean? = true): TextShapeDataBase
    fun textItalic(textItalic: Boolean? = true): TextShapeDataBase
    fun textUnderline(textUnderline: Boolean? = true): TextShapeDataBase
    fun textStrikethrough(textStrikethrough: Boolean? = true): TextShapeDataBase

    /**
     * `textGravity` is used to position text vertically within parent bounds. Horizontally
     * text is going to be positioned via `alignment` property of a [TextPaint].
     */
    fun textGravity(textGravity: Gravity?): TextShapeDataBase

    /**
     * NB! Text rotation is always relative to actual text bounds.
     * By default center is used as the pivot point
     */
    fun textRotation(
        angle: Float,
        @FloatRange(from = 0.0, to = 1.0) centerX: Float? = null,
        @FloatRange(from = 0.0, to = 1.0) centerY: Float? = null
    ): TextShapeDataBase

    /**
     * By default uses text color as shadow color
     */
    fun textShadow(
        radius: Int,
        @ColorInt color: Int? = null,
        offsetX: Int? = null,
        offsetY: Int? = null
    ): TextShapeDataBase

    /**
     * Limits by lines resulting text.
     * NB! in most of the cases for text to be properly limited `TruncateAt` should be specified,
     * by default text shape would use `TruncateAt.END` if not specified explicitly
     */
    fun textMaxLines(textMaxLines: Int?, textEllipsize: TextUtils.TruncateAt? = null): TextShapeDataBase
    fun textBreakStrategy(textBreakStrategy: BreakStrategy?): TextShapeDataBase
    fun textHyphenationFrequency(textHyphenationFrequency: HyphenationFrequency?): TextShapeDataBase
    fun textJustificationMode(textJustificationMode: JustificationMode?): TextShapeDataBase
    fun textLineSpacingAdd(textLineSpacingAdd: Int?): TextShapeDataBase
    fun textLineSpacingMultiplier(textLineSpacingMultiplier: Float?): TextShapeDataBase

    /**
     * NB! this is `em` value, negative values shrink text
     * @see TextPaint.setLetterSpacing
     */
    fun textLetterSpacing(em: Float?): TextShapeDataBase
}
//@formatter:on

//@formatter:off
data class TextShapeData(
    override var text: CharSequence? = null,
    override var textSize: Int? = null,
    override var textColor: Int? = null,
    override var textGradient: Gradient? = null,
    override var textTypeface: Typeface? = null,
    override var textBold: Boolean? = null,
    override var textItalic: Boolean? = null,
    override var textUnderline: Boolean? = null,
    override var textStrikethrough: Boolean? = null,
    override var textGravity: Gravity? = null,
    override var textRotation: Shape.Rotation? = null,
    override var textShadow: Shape.Shadow? = null,
    override var textMaxLines: Int? = null,
    override var textEllipsize: TextUtils.TruncateAt? = null,
    override var textBreakStrategy: BreakStrategy? = null,
    override var textHyphenationFrequency: HyphenationFrequency? = null,
    override var textJustificationMode: JustificationMode? = null,
    override var textLineSpacingAdd: Int? = null,
    override var textLineSpacingMultiplier: Float? = null,
    override var textLetterSpacing: Float? = null
): TextShapeDataBase {
    override fun text(text: CharSequence?) = this.also { it.text = text }
    override fun textSize(textSize: Int?) = this.also { it.textSize = textSize }
    override fun textColor(textColor: Int?) = this.also { it.textColor = textColor }
    override fun textGradient(textGradient: Gradient?) = this.also { it.textGradient = textGradient }
    override fun textTypeface(textTypeface: Typeface?) = this.also { it.textTypeface = textTypeface }
    override fun textBold(textBold: Boolean?) = this.also { it.textBold = textBold }
    override fun textItalic(textItalic: Boolean?) = this.also { it.textItalic = textItalic }
    override fun textUnderline(textUnderline: Boolean?) = this.also { it.textUnderline = textUnderline }
    override fun textStrikethrough(textStrikethrough: Boolean?) = this.also { it.textStrikethrough = textStrikethrough }
    override fun textGravity(textGravity: Gravity?) = this.also { it.textGravity = textGravity }
    override fun textRotation(angle: Float, centerX: Float?, centerY: Float?) = this.also {
        it.textRotation = Shape.Rotation(
            angle,
            centerX?.let { p -> Dimension.Relative(p) },
            centerY?.let { p -> Dimension.Relative(p) }
        )
    }
    override fun textShadow(radius: Int, @ColorInt color: Int?, offsetX: Int?, offsetY: Int?) = this.also {
        it.textShadow = Shape.Shadow(
            color,
            Dimension.Exact(radius),
            offsetX?.let { p -> Dimension.Exact(p) },
            offsetY?.let { p -> Dimension.Exact(p) }
        )
    }
    override fun textMaxLines(textMaxLines: Int?, textEllipsize: TextUtils.TruncateAt?) = this.also {
        it.textMaxLines = textMaxLines
        it.textEllipsize = textEllipsize
    }
    override fun textBreakStrategy(textBreakStrategy: BreakStrategy?) = this.also { it.textBreakStrategy = textBreakStrategy }
    override fun textHyphenationFrequency(textHyphenationFrequency: HyphenationFrequency?) = this.also { it.textHyphenationFrequency = textHyphenationFrequency }
    override fun textJustificationMode(textJustificationMode: JustificationMode?) = this.also { it.textJustificationMode = textJustificationMode }
    override fun textLineSpacingAdd(textLineSpacingAdd: Int?) = this.also { it.textLineSpacingAdd = textLineSpacingAdd }
    override fun textLineSpacingMultiplier(textLineSpacingMultiplier: Float?) = this.also { it.textLineSpacingMultiplier = textLineSpacingMultiplier }
    override fun textLetterSpacing(em: Float?) = this.also { it.textLetterSpacing = em }
}
//@formatter:on

@RequiresApi(Build.VERSION_CODES.O)
class TextShape(
    text: CharSequence? = null,
    private val data: TextShapeData = TextShapeData(text = text),
    block: TextShape.() -> Unit = {}
) : RectangleShape(), TextShapeDataBase by data {

    init {
        block(this)
    }

    private val cache = LayoutCache()
    private val contentBounds = Rect()

    override fun clone() = TextShape(data = data.copy())

    override fun toStringDedicatedProperties(): String {
        return data.toString()
    }

    override fun drawSelf(canvas: Canvas, bounds: Rect) {
        // cannot draw for less than 1 lines (0, -1, -2, etc)
        data.textMaxLines?.also {
            if (it < 1) return
        }

        val layout = cache.layout(data, bounds, contentBounds) ?: return

        val textTranslationX = bounds.left

        val drawRect = drawRect().also { it.set(contentBounds) }

        // apply text rotation to content (and children)
        textRotation?.draw(canvas, drawRect)

        super.drawChildren(canvas, drawRect)

        val save = canvas.save()
        try {
            // NB! we apply all vertical axis translation, horizontal should match parent
            val textTranslationY = drawRect.top
            if (textTranslationX != 0 || textTranslationY != 0) {
                canvas.translate(textTranslationX.toFloat(), textTranslationY.toFloat())
            }

            layout.draw(canvas)
        } finally {
            canvas.restoreToCount(save)
        }
    }

    // NB! overridden, so we can draw children before text (otherwise it would overlay it)
    override fun drawChildren(canvas: Canvas, bounds: Rect) = Unit

    internal class LayoutCache {
        private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        private var layout: StaticLayout? = null
        private val shaderCache = ShaderCache()

        private var previousTextData: TextShapeData? = null
        private var previousWidth: Int = 0

        // NB! contentBounds would be modified to indicate real text content size
        fun layout(
            textData: TextShapeData?,
            bounds: Rect,
            contentBounds: Rect
        ): Layout? {
            val width = bounds.width()

            if (previousTextData == null ||
                textData != previousTextData ||
                layout == null ||
                width != previousWidth
            ) {
                previousTextData = textData?.copy()
                previousWidth = width

                val text = textData?.text

                // cannot draw anything without text
                if (text.isNullOrEmpty()) {
                    layout = null
                    return null
                }

                with(textData) {
                    textSize?.dip?.toFloat()?.also { textPaint.textSize = it }
                    textColor?.also { textPaint.color = it }
                    textTypeface?.also { textPaint.typeface = it }

                    val typeface = textTypeface
                    if (typeface != null) {
                        val bold = textBold ?: false
                        val italic = textItalic ?: false
                        val style = when {
                            bold && italic -> Typeface.BOLD_ITALIC
                            bold -> Typeface.BOLD
                            italic -> Typeface.ITALIC
                            else -> Typeface.NORMAL
                        }
                        textPaint.typeface = Typeface.create(typeface, style)
                    } else {
                        // emulate bold
                        if (true == textBold) {
                            textPaint.isFakeBoldText = true
                        }
                        // emulate italic
                        if (true == textItalic) {
                            textPaint.textSkewX = -0.25F
                        }
                    }
                    textUnderline?.also { textPaint.isUnderlineText = it }
                    textStrikethrough?.also { textPaint.isStrikeThruText = it }

                    textPaint.letterSpacing = textLetterSpacing ?: 0F

                    // resolve(0) is a way to ensure only Exact dimensions
                    textShadow
                        ?.let { it to (it.radius?.resolve(0) ?: 0) }
                        ?.takeIf { it.second > 0 }
                        ?.also { (shadow, radius) ->
                            val x = shadow.offsetX?.resolve(0) ?: 0
                            val y = shadow.offsetY?.resolve(0) ?: 0
                            textPaint.setShadowLayer(
                                radius.toFloat(),
                                x.toFloat(),
                                y.toFloat(),
                                shadow.color ?: textPaint.color
                            )
                        }
                    // else clear shadow layer
                        ?: kotlin.run { textPaint.clearShadowLayer() }
                }

                layout = StaticLayout.Builder
                    .obtain(
                        text,
                        0,
                        text.length,
                        textPaint,
                        width
                    )
                    .also { builder ->
                        with(textData) {
                            textGravity?.also { builder.setAlignment(alignment(it)) }
                            textBreakStrategy?.also { builder.setBreakStrategy(it.value) }
                            textHyphenationFrequency?.also {
                                builder.setHyphenationFrequency(
                                    it.value
                                )
                            }
                            textJustificationMode?.also { builder.setJustificationMode(it.value) }

                            // NB! ellipsize is only applied when there are maxLines
                            textMaxLines?.also {
                                builder.setMaxLines(it)
                                builder.setEllipsize(textEllipsize ?: TextUtils.TruncateAt.END)
                            }

                            val add = textLineSpacingAdd
                            val mult = textLineSpacingMultiplier
                            if (add != null || mult != null) {
                                builder.setLineSpacing(
                                    add?.dip?.toFloat() ?: 0F,
                                    mult ?: 1F
                                )
                            }
                        }
                    }
                    .build()
                    .also { layout ->
                        val contentWidth = (0 until layout.lineCount)
                            .maxOf { layout.getLineWidth(it).roundToInt() }
                        val contentHeight = layout.height

                        // we update drawRect according to the text gravity
                        // additionally, we gravity is applied here only to vertical axis
                        //  as horizontal gravity should be taken by the cache (which constructs alignment),
                        //  here, text is always the width of container, but with proper gravity it can be
                        //  positioned freely on the canvas
                        textData.textGravity?.also { gravity ->
                            // we update drawRect according to the text gravity
                            // additionally, we gravity is applied here only to vertical axis
                            //  as horizontal gravity should be taken by the cache (which constructs alignment),
                            //  here, text is always the width of container, but with proper gravity it can be
                            //  positioned freely on the canvas
                            android.view.Gravity.apply(
                                gravity.value,
                                contentWidth,
                                contentHeight,
                                bounds,
                                contentBounds
                            )
                        } ?: kotlin.run {
                            contentBounds.set(
                                bounds.left,
                                bounds.top,
                                bounds.left + contentWidth,
                                bounds.top + contentHeight
                            )
                        }
                    }

                shaderCache.shader(textData.textGradient, contentBounds, textPaint)
            }

            return layout
        }

        @Suppress("MoveVariableDeclarationIntoWhen")
        @SuppressLint("RtlHardcoded")
        internal fun alignment(gravity: Gravity): Layout.Alignment {
            val value = gravity.value.and(android.view.Gravity.HORIZONTAL_GRAVITY_MASK)
            return when (value) {
                android.view.Gravity.START, android.view.Gravity.LEFT -> Layout.Alignment.ALIGN_NORMAL
                android.view.Gravity.END, android.view.Gravity.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
                else -> Layout.Alignment.ALIGN_CENTER
            }
        }
    }
}