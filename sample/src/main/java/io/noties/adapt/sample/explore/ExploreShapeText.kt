package io.noties.adapt.sample.explore

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.Layout.Alignment
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.view.Gravity
//import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import io.noties.adapt.sample.util.dip
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.JustificationMode
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.Shape
import io.noties.debug.Debug
import kotlin.math.roundToInt

object ExploreShapeText {

    // TODO: rename to something more nice
    // TODO: make setters (to align with other shapes configuration)
    interface TextDataBase {
        data class TextShadow(
            val radius: Int,
            val x: Int? = null,
            val y: Int? = null,
            val color: Int? = null
        )

        var text: CharSequence?
        var textSize: Int?

        @get:ColorInt
        var textColor: Int?
        var textGradient: Gradient?
        var textTypeface: Typeface?
        var textAlignment: Alignment?
        var textMaxLines: Int?
        var textShadow: TextShadow?
        var textBreakStrategy: BreakStrategy?
        var textHyphenationFrequency: HyphenationFrequency?
        var textJustificationMode: JustificationMode?
        var textEllipsize: TextUtils.TruncateAt?
        var textLineSpacingAdd: Int?
        var textLineSpacingMultiplier: Float?
    }

    // TODO: typeface style... maybe add bold, italic, strikethrough and underline

    data class TextData(
        override var text: CharSequence? = null,
        override var textSize: Int? = null,
        @ColorInt override var textColor: Int? = null,
        override var textGradient: Gradient? = null,
        override var textTypeface: Typeface? = null,
        override var textAlignment: Alignment? = null,
        override var textMaxLines: Int? = null,
        override var textShadow: TextDataBase.TextShadow? = null,
        override var textBreakStrategy: BreakStrategy? = null,
        override var textHyphenationFrequency: HyphenationFrequency? = null,
        override var textJustificationMode: JustificationMode? = null,
        override var textEllipsize: TextUtils.TruncateAt? = null,
        override var textLineSpacingAdd: Int? = null,
        override var textLineSpacingMultiplier: Float? = null,
    ) : TextDataBase {
        override fun toString(): String {
            return "text=$text, textSize=$textSize, textColor=$textColor, textGradient=$textGradient, textTypeface=$textTypeface, textAlignment=$textAlignment, textMaxLines=$textMaxLines, textShadow=$textShadow, textBreakStrategy=$textBreakStrategy, textHyphenationFrequency=$textHyphenationFrequency, textJustificationMode=$textJustificationMode, textEllipsize=$textEllipsize, textLineSpacingAdd=$textLineSpacingAdd, textLineSpacingMultiplier=$textLineSpacingMultiplier"
        }
    }

    // TODO: as staticlayout before O cannot specify max lines, break strategy, hypenation, justify
    //  it is better to make this class available since O only
    // TODO: equals and hashcode
    @RequiresApi(Build.VERSION_CODES.O)
    class Text(
        private val textData: TextData = TextData(),
        block: Text.() -> Unit = {}
    ) : Shape(), TextDataBase by textData {

        var background: Shape? = Rectangle()

        private val cache = LayoutCache()

        init {
            block(this)
        }

        // TODO: can we deprecate or disable size and sizeRelative?

        override fun clone(): Shape = Text(textData.copy())

        override fun toStringDedicatedProperties(): String {
            return textData.toString()
        }

        private val textBounds = Rect()

        // unfortunately, text is super special, we need to override fully
        override fun draw(canvas: Canvas, bounds: Rect) {
            // if hidden
            if (true == hidden) return

            // if bounds are empty
            if (bounds.isEmpty) return

            // if dimension is not specified, then we do the wrap_content...
            // TODO: how to draw HORIZONATLLY?
            //  okay, layout report full width we give to it, even though line width might be less
            //  should we translate? how to calcualte translation? in different gravities it would be
            //  different

            // TODO: what if we always make text wrap_content? makes sense, as it is hard to
            //  properly calculate size for text
            // TODO: as we would have always wrap_content, we do not need text alignment? hm..
            //  no, in case of multiline-text, it is still useful

            // TODO: padding... we receive one bounds, apply padding to it, then we create layout...
            //  and layout still needs to have padding applied... but it should? ah...
            //  we can take a different approach - and grow padding around text (regular shapes
            //  do it differently - reduce available size)

//            val boundsWidth = bounds.width()
//            val boundsHeight = bounds.height()

            // okay, if we have dimension specified, then use it, else wrap_content
//            val width = this.width?.resolve(boundsWidth)
//            val height = this.height?.resolve(boundsHeight)

            // shadow these variables, so we do not accidentally use it
//            val width by lazy { error("") }
//            val height by lazy { error("") }

            // okay, text is always wrap-content, this would simplify things a lot
            textBounds.set(bounds)

//            if (width != null) {
//                textBounds.set(
//                    bounds.left,
//                    0,
//                    bounds.left + width,
//                    0
//                )
//                // apply padding if we have dimension
//                padding?.also { padding ->
//                    padding.leading?.resolve(boundsWidth)?.also { textBounds.left += it }
//                    padding.trailing?.resolve(boundsWidth)?.also { textBounds.right -= it }
//                }
//            } else {
//                textBounds.set(
//                    bounds.left,
//                    0,
//                    bounds.right,
//                    0
//                )
//            }

//            if (height != null) {
//                textBounds.set(
//                    textBounds.left,
//                    bounds.top,
//                    textBounds.right,
//                    bounds.top + height
//                )
//                padding?.also { padding ->
//                    padding.top?.resolve(boundsHeight)?.also { textBounds.top += it }
//                    padding.bottom?.resolve(boundsHeight)?.also { textBounds.bottom -= it }
//                }
//            } else {
//                textBounds.set(
//                    textBounds.left,
//                    bounds.top,
//                    textBounds.right,
//                    bounds.bottom
//                )
//            }

//            val hasKnownDimensions = width != null && height != null

            // if both are present, then just apply gravity now
//            if (hasKnownDimensions) {
//                gravity?.also { gravity ->
//                    Gravity.apply(
//                        gravity.value,
//                        textBounds.width(),
//                        textBounds.height(),
//                        bounds,
//                        textBounds
//                    )
//                }
//            }

            Debug.i("textBounds:${textBounds.toShortString()}")

            if (textBounds.isEmpty) return

            val save = canvas.save()
            try {

                // TODO: alpha handling

                // TODO: return is fine here? do not draw anything
                // TODO: we must additionally take into account padding and
                //  reduce the text bounds size, so it can fit (without exiting max bounds)

                // padding is a dimension.. let's use original bounds?
                //  the things is.. we do not want to recalculate layout, if we would base
                //  calculation on the bounds of the text, so exact values are OK and relative
                //  values are based on supplied bounds
                val boundsWidth = bounds.width()
                val boundsHeight = bounds.height()

                // TODO: no, remove it. If explicit sizes are required, then create a shape with that sizes
                //  and then add text to it (would allow center vertically in given bounds)
//                val explicitWidth = width?.resolve(boundsWidth)
//                val explicitHeight = height?.resolve(boundsHeight)

                // height => take value (and manually clip at it)
                //  or check max lines, and take last allowed line bottom and also clip

                val paddingLeading = padding?.leading?.resolve(boundsWidth)
                val paddingTrailing = padding?.trailing?.resolve(bounds.height())
                val paddingHorizontal = (paddingLeading ?: 0) + (paddingTrailing ?: 0)

                // okay, we created it, but how do we manipulate the textBounds?
                val textWidth = if (textBounds.width() + paddingHorizontal > boundsWidth) {
                    // do we update textBounds with original bounds values? but at this point they already equal
                    boundsWidth - paddingHorizontal
                } else {
                    textBounds.width()
                }

                val layout = cache.layout(textData, textWidth) ?: return

                val (left, top) = if (true) {
                    // TODO: also limit by number of lines? hm... it would affect the
                    //  overall layout (some lines might be greater)
                    val layoutWidth = (0 until layout.lineCount)
                        .map { layout.getLineWidth(it) }
                        .max()
                        .roundToInt()

                    val paddingTop = padding?.top?.resolve(bounds.height())
                    val paddingBottom = padding?.bottom?.resolve(bounds.height())
                    val paddingVertical = (paddingTop ?: 0) + (paddingBottom ?: 0)

                    val layoutHeight = layout.height

                    Debug.i("layoutWidth=${layoutWidth}, layoutHeight=${layoutHeight}")

                    // TODO: which dimensions we use for this padding? layout? or original bounds?
                    //  it makes sense to use layout, as it is wrap content
//                    val paddingLeading = padding?.leading?.resolve(layoutWidth)
//                    val paddingTrailing = padding?.trailing?.resolve(layoutWidth)
//                    val paddingHorizontal = if (width == null) {
//                        (paddingLeading ?: 0) + (paddingTrailing ?: 0)
//                    } else 0

                    // apply gravity
                    // TODO: isn't it adding 2 padding? first when calculating and now?
                    (gravity ?: io.noties.adapt.ui.util.Gravity.top.leading).also { gravity ->
                        Gravity.apply(
                            gravity.value,
                            layoutWidth + paddingHorizontal,
                            layoutHeight + paddingVertical,
                            bounds,
                            textBounds
                        )
                    } ?: kotlin.run {
//                        textBounds.set(
//                            (paddingLeading ?: bounds.left),
//                            (paddingTop ?: bounds.top),
//                            layoutWidth + paddingHorizontal,
//                            layoutHeight + paddingVertical
//                        )
                        textBounds.set(
                            bounds.left,
                            bounds.top,
                            bounds.left + layoutWidth + (paddingTrailing ?: 0),
                            bounds.top + layoutHeight + (paddingBottom ?: 0)
                        )
                    }

                    // TODO: applied padding should not make final size exceed bounds

                    Debug.i("textBounds.afterGravity.noDimensions:${textBounds.toShortString()}")

                    // TODO: this one... how can we properly calculate
                    // TODO: hm, if width is present, no translation (the same for height)
                    val left = (textBounds.left + (paddingLeading ?: 0)).toFloat()
                    val top = (textBounds.top + (paddingTop ?: 0)).toFloat()
//                    canvas.translate(
//                        left,
//                        top
//                    )
                    left to top
                } else {
//                    canvas.translate(
//                        textBounds.left.toFloat(),
//                        textBounds.top.toFloat()
//                    )
                    textBounds.left.toFloat() to textBounds.top.toFloat()
                }

                translation?.draw(canvas, textBounds)
                rotation?.draw(canvas, textBounds)

                // TODO: shapes properly takes values from bounds, so we do not need to translate them
                //  but we need to translate layout
                background?.also { background ->
                    shadow?.draw(canvas, background, textBounds)
                    fill?.draw(canvas, background, textBounds)
                    stroke?.draw(canvas, background, textBounds)
                }

                // TODO: do we need also textGravity? when dimensions are specified,
                //  text is placed at default gravity (left top)
                canvas.translate(left, top)

                // TODO: reuse this variable
                val layoutWidth = (0 until layout.lineCount)
                    .map { layout.getLineWidth(it) }
                    .max()
                    .roundToInt()
                Debug.i("bounds.width()=${bounds.width()}, textBounds.width()=${textBounds.width()}, layoutWidth=${layoutWidth}")
                // NB! this only applied when width == null AND ONLY TO LAYOUT!
                if (layout.width != layoutWidth) {
                    // how, we need to take into account the gravity? take into account text align
                    textData.textAlignment?.also {
                        if (it == Alignment.ALIGN_CENTER) {
                            val diff = (layoutWidth - layout.width) / 2
                            canvas.translate(diff.toFloat(), 0F)
                        } else if (it == Alignment.ALIGN_OPPOSITE) {
                            // TODO: this is what we need to find how to calculate
                        }
                    }
//                    val diff = (layoutWidth - layout.width) / 2
//                    canvas.translate(diff.toFloat(), 0F)
                }
//
//                if (height == null) {
//                    canvas.translate(0F, textBounds.top.toFloat())
//                }

                // TODO: maxLines.... is ignored by layout
                //  must manually clip canvas.. but with truncate end it seems to be working
                // TODO: Text shadow
//                canvas.clipRect()
                layout.draw(canvas)

            } finally {
                canvas.restoreToCount(save)
            }
        }

        override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {

            // STOPSHIP:  
            if (true) {
                return
            }

//            // TODO: padding should be applied before, right? in case it affects dimensions
//
//            val layout = cache.layout(textData, bounds)
//            // TODO: layout would take all available dimensions... if we want to center it,
//            //  then we have to apply gravity manually (but only if no explicit size is specified?)
//            // TODO: paint alpha
//            if (layout != null) {
//                // We receive proper bounds, but layout discards it and always draws at top-left
//                Debug.i("layout.width=${layout.width}, layout.height=${layout.height} bounds:$bounds")
//                val save = canvas.save()
//                try {
//
////                    val left = bounds.left
////                    val top = bounds.top
////                    if (left != 0 || top != 0) {
////                        canvas.translate(left.toFloat(), top.toFloat())
////                    }
//
//                    val (l, r) = if (width == null) {
//                        // if not specified explicitly, then use layout bounds
//                        // reports full width of initial bounds
//                        val max = (0 until layout.lineCount)
//                            .map { layout.getLineWidth(it) }
//                            .max()
//                        0 to max.roundToInt()
//                    } else {
//                        // else just use original
//                        0 to bounds.width()
//                    }
//
////                    // STOPSHIP:
////                    if (true) {
////                        (0 until layout.lineCount)
////                            .forEach { Debug.i("line:$it width:${layout.getLineWidth(it)}") }
////                    }
//
//                    val (t, b) = if (height == null) {
//                        0 to layout.height
//                    } else {
//                        0 to bounds.height()
//                    }
//
//                    gravity?.also {
//                        Gravity.apply(
//                            it.value,
//                            r,
//                            b,
//                            bounds,
//                            textBounds
//                        )
//                    }
//
//                    Debug.i("gravity.applied:${textBounds.toShortString()}")
//
//                    padding?.set(textBounds)
//
//                    shadow?.draw(canvas, Rectangle(), textBounds)
//                    fill?.draw(canvas, Rectangle(), textBounds)
//                    stroke?.draw(canvas, Rectangle(), textBounds)
//
//                    // check if we have gravity (it does not make any adjustments
//                    //  if there are no dimensions... width is actually is taken fully by layout...
//                    //  even though content might be less, it anyway takes full width (and applied alignment)....
////                    val left = textBounds.left
//                    val top = textBounds.top
////                    if (left != 0 || top != 0) {
////                        canvas.translate(left.toFloat(), top.toFloat())
////                    }
//                    if (top != 0) {
//                        canvas.translate(0F, top.toFloat())
//                    }
//
//                    layout.draw(canvas)
//                } finally {
//                    canvas.restoreToCount(save)
//                }
//            }
        }

        private class LayoutCache {
            private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            private var layout: StaticLayout? = null
            private val shaderCache = ShaderCache()

            private var previousTextData: TextData? = null

            //            private val previousBounds: Rect = Rect()
            private var previousWidth: Int = 0

            fun layout(
                textData: TextData?,
//                paint: Paint,
                width: Int
            ): Layout? {
                if (previousTextData == null ||
                    textData != previousTextData ||
                    layout == null ||
                    width != previousWidth
                ) {
                    previousTextData = textData?.copy()
                    previousWidth = width
//                    previousBounds.set(bounds)

                    val text = textData?.text

                    // cannot draw anything without text
                    if (text.isNullOrEmpty()) {
                        layout = null
                        return null
                    }

//                    textPaint.set(paint)

                    with(textData) {
                        textSize?.dip?.toFloat()?.also { textPaint.textSize = it }
                        textColor?.also { textPaint.color = it }
                        textTypeface?.also { textPaint.typeface = it }

                        textShadow?.also {
                            textPaint.setShadowLayer(
                                it.radius.dip.toFloat(),
                                it.x?.dip?.toFloat() ?: 0F,
                                it.y?.dip?.toFloat() ?: 0F,
                                it.color ?: textPaint.color
                            )
                        } ?: kotlin.run {
                            textPaint.clearShadowLayer()
                        }

                        // TODO: additional styling
//                        textPaint.isUnderlineText
//                        textPaint.isStrikeThruText
//                        textPaint.isFakeBoldText
//                        textPaint.textSkewX
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
                                textMaxLines?.also { builder.setMaxLines(it) }
                                textAlignment?.also { builder.setAlignment(it) }
                                textBreakStrategy?.also { builder.setBreakStrategy(it.value) }
                                textHyphenationFrequency?.also {
                                    builder.setHyphenationFrequency(
                                        it.value
                                    )
                                }
                                textJustificationMode?.also { builder.setJustificationMode(it.value) }
                                textEllipsize?.also { builder.setEllipsize(it) }

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

                    // TODO: we have no bounds...
                    val rect = Rect(0, 0, layout!!.width, layout!!.height)
                    shaderCache.shader(textData.textGradient, rect, layout!!.paint)
                }

                return layout
            }
        }
    }
}