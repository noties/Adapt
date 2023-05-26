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
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.JustificationMode
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import kotlin.math.roundToInt

interface BaseTextShapeData : CommonTextPaintData {
    var text: CharSequence?
    var textGradient: Gradient?
    var textGravity: Gravity?
    var textRotation: Shape.Rotation?
    var textMaxLines: Int?
    var textEllipsize: TextUtils.TruncateAt?
    var textBreakStrategy: BreakStrategy?
    var textHyphenationFrequency: HyphenationFrequency?
    var textJustificationMode: JustificationMode?
    var textLineSpacingAdd: Int?
    var textLineSpacingMultiplier: Float?

    /**
     * Set text to be drawn. If text is null, this shape is not going to be drawn
     */
    fun text(text: CharSequence?) = this.also { this.text = text }

    /**
     * Set text gradient, based on actual text bounds
     * @see TextPaint.setShader
     */
    fun textGradient(textGradient: Gradient?) = this.also { it.textGradient = textGradient }

    /**
     * `textGravity` is used to position text vertically within parent bounds. Horizontally
     * text is going to be positioned via `alignment` property of a [TextPaint].
     * @see StaticLayout.Builder.setAlignment
     */
    fun textGravity(textGravity: Gravity?) = this.also {
        it.textGravity = textGravity
    }

    /**
     * Rotate text relative to its bounds (bounds that actual text content takes). Accepts
     * relative values - 0F..1F where x=0F is left-most position y=1F is bottom-most position.
     * By default 0.5F-0.5F is used (center of text bounds)
     */
    fun textRotation(angle: Float, centerX: Float? = null, centerY: Float? = null) = this.also {
        it.textRotation = Shape.Rotation(
            angle,
            centerX?.let { p -> Dimension.Relative(p) },
            centerY?.let { p -> Dimension.Relative(p) }
        )
    }

    /**
     * Limits text to `maxLines` specified. If no `textEllipsize` END is used by default
     * @see StaticLayout.Builder.setMaxLines
     * @see StaticLayout.Builder.setEllipsize
     */
    fun textMaxLines(textMaxLines: Int?, textEllipsize: TextUtils.TruncateAt? = null) = this.also {
        it.textMaxLines = textMaxLines
        it.textEllipsize = textEllipsize
    }

    /**
     * Set BreakStrategy for [StaticLayout]
     * @see BreakStrategy
     * @see StaticLayout.Builder.setBreakStrategy
     */
    fun textBreakStrategy(textBreakStrategy: BreakStrategy?) = this.also {
        it.textBreakStrategy = textBreakStrategy
    }

    /**
     * Set Hyphenation frequency for [StaticLayout]
     * @see HyphenationFrequency
     * @see StaticLayout.Builder.setHyphenationFrequency
     */
    fun textHyphenationFrequency(textHyphenationFrequency: HyphenationFrequency?) = this.also {
        it.textHyphenationFrequency = textHyphenationFrequency
    }

    /**
     * Set Justification mode for [StaticLayout]
     * @see JustificationMode
     * @see StaticLayout.Builder.setJustificationMode
     */
    fun textJustificationMode(textJustificationMode: JustificationMode?) = this.also {
        it.textJustificationMode = textJustificationMode
    }

    /**
     * Set additional line spacing for [StaticLayout], by default add=0, mult=1.0F
     * @see StaticLayout.Builder.setLineSpacing
     */
    fun textLineSpacing(add: Int? = null, mult: Float? = null) = this.also {
        it.textLineSpacingAdd = add
        it.textLineSpacingMultiplier = mult
    }
}

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
) : BaseTextShapeData

@RequiresApi(Build.VERSION_CODES.O)
class TextShape(
    text: CharSequence? = null,
    private val data: TextShapeData = TextShapeData(text = text),
    block: TextShape.() -> Unit = {}
) : RectangleShape(), BaseTextShapeData by data {

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

        // NB! children are drawn before actual text (so would be displayed under text)
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

                textData.applyTo(textPaint)

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
                            textGravity?.also { builder.setAlignment(horizontalAlignment(it)) }
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
                        // additionally, when gravity is applied here only to vertical axis
                        //  as horizontal gravity should be taken by the cache (which constructs alignment),
                        //  here, text is always the width of container, but with proper gravity it can be
                        //  positioned freely on the canvas
                        textData.textGravity?.also { gravity ->
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

                // NB! this called at the end (after already sending textPaint to layout,
                //  because we need text bounds to create gradient)
                shaderCache.shader(textData.textGradient, contentBounds, textPaint)
            }

            return layout
        }

        @Suppress("MoveVariableDeclarationIntoWhen")
        @SuppressLint("RtlHardcoded")
        internal fun horizontalAlignment(gravity: Gravity): Layout.Alignment {
            return when {
                gravity.hasLeading() -> Layout.Alignment.ALIGN_NORMAL
                gravity.hasTrailing() -> Layout.Alignment.ALIGN_OPPOSITE
                else -> Layout.Alignment.ALIGN_CENTER
            }
        }
    }
}