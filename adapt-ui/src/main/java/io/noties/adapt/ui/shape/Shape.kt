package io.noties.adapt.ui.shape

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.MaskFilter
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.toHexString
import kotlin.math.roundToInt

// TODO: add a new method: .statful { base ->
//  which receives this shape and allows creating a statful version
//}
abstract class Shape {

    companion object {
        // default color, black with 255 (1F) alpha
        const val defaultFillColor: Int = 0xFF000000.toInt()
    }

    /**
     * `clone` creates a new instance of a shape. In order to create a customized
     * copy the [copy] function should be used
     */
    abstract fun clone(): Shape

    // a subclass should emit dedicated properties
    abstract fun toStringDedicatedProperties(): String

    fun copyTo(to: Shape) {
        to.hidden = this.hidden
        to.width = this.width
        to.height = this.height
        to.gravity = this.gravity
        to.rotation = this.rotation?.copy()
        to.translation = this.translation?.copy()
        to.padding = this.padding?.copy()
        to.alpha = this.alpha
        to.shadow = this.shadow?.copy()
        to.fill = this.fill?.copy()
        to.stroke = this.stroke?.copy()
        to.children.addAll(this.children.map { it.copy() })
    }

    /**
     * Creates new drawable with this shape as root
     */
    fun newDrawable(): ShapeDrawableNoRef = ShapeDrawable(this)

    fun hidden(hidden: Boolean = true): Shape = this.also {
        it.hidden = hidden.takeIf { b -> b }
    }

    // if null, then use bounds value (if null is stored property, if null is passed to the function,
    //  this argument is ignored)
    fun size(
        width: Int? = null,
        height: Int? = null,
        gravity: Gravity? = null
    ): Shape {
        width?.also { this.width = Dimension.Exact(it) }
        height?.also { this.height = Dimension.Exact(it) }
        gravity?.also { this.gravity = it }
        return this
    }

    fun sizeRelative(
        @FloatRange(from = 0.0, to = 1.0) width: Float? = null,
        @FloatRange(from = 0.0, to = 1.0) height: Float? = null,
        gravity: Gravity? = null
    ): Shape = this.apply {
        width?.also { this.width = Dimension.Relative(it) }
        height?.also { this.height = Dimension.Relative(it) }
        gravity?.also { this.gravity = it }
    }

    fun gravity(gravity: Gravity) = this.also {
        this.gravity = gravity
    }

    /**
     * Rotation.
     * By default rotates around bounds centerX and centerY
     * @see rotateRelative
     */
    fun rotate(
        degrees: Float?,
        centerX: Int? = null,
        centerY: Int? = null
    ): Shape = this.also {
        this.rotation = (rotation ?: Rotation()).apply {
            this.degrees = degrees
            centerX?.also { this.centerX = Dimension.Exact(it) }
            centerY?.also { this.centerY = Dimension.Exact(it) }
        }
    }

    fun rotateRelative(
        degrees: Float?,
        centerX: Float? = null,
        centerY: Float? = null
    ): Shape = this.also {
        this.rotation = (rotation ?: Rotation()).apply {
            this.degrees = degrees
            centerX?.also { this.centerX = Dimension.Relative(it) }
            centerY?.also { this.centerY = Dimension.Relative(it) }
        }
    }

    fun padding(all: Int): Shape = padding(all, all)

    fun padding(
        horizontal: Int? = null,
        vertical: Int? = null
    ): Shape = padding(horizontal, vertical, horizontal, vertical)

    fun padding(
        leading: Int? = null,
        top: Int? = null,
        trailing: Int? = null,
        bottom: Int? = null
    ): Shape {
        padding = (padding ?: Padding()).apply {
            leading?.also { this.leading = Dimension.Exact(it) }
            top?.also { this.top = Dimension.Exact(it) }
            trailing?.also { this.trailing = Dimension.Exact(it) }
            bottom?.also { this.bottom = Dimension.Exact(it) }
        }
        return this
    }

    fun paddingRelative(
        @FloatRange(from = 0.0, to = 1.0) all: Float
    ): Shape = paddingRelative(all, all)

    fun paddingRelative(
        @FloatRange(from = 0.0, to = 1.0) horizontal: Float? = null,
        @FloatRange(from = 0.0, to = 1.0) vertical: Float? = null
    ): Shape = paddingRelative(horizontal, vertical, horizontal, vertical)

    fun paddingRelative(
        @FloatRange(from = 0.0, to = 1.0) leading: Float? = null,
        @FloatRange(from = 0.0, to = 1.0) top: Float? = null,
        @FloatRange(from = 0.0, to = 1.0) trailing: Float? = null,
        @FloatRange(from = 0.0, to = 1.0) bottom: Float? = null
    ): Shape {
        padding = (padding ?: Padding()).apply {
            leading?.also { this.leading = Dimension.Relative(it) }
            top?.also { this.top = Dimension.Relative(it) }
            trailing?.also { this.trailing = Dimension.Relative(it) }
            bottom?.also { this.bottom = Dimension.Relative(it) }
        }
        return this
    }

    fun translate(x: Int? = null, y: Int? = null): Shape = this.also {
        this.translation = (translation ?: Translation()).apply {
            x?.also { this.x = Dimension.Exact(it) }
            y?.also { this.y = Dimension.Exact(it) }
        }
    }

    fun translateRelative(
        @FloatRange(from = -1.0, to = 1.0) x: Float? = null,
        @FloatRange(from = -1.0, to = 1.0) y: Float? = null
    ): Shape = this.also {
        this.translation = (translation ?: Translation()).apply {
            x?.also { this.x = Dimension.Relative(it) }
            y?.also { this.y = Dimension.Relative(it) }
        }
    }

    fun shadow(
        @ColorInt color: Int? = null,
        radius: Int? = null,
        offsetX: Int? = null,
        offsetY: Int? = null
    ) = this.also {
        this.shadow = (shadow ?: Shadow()).apply {
            color?.also { this.color = it }
            radius?.also { this.radius = Dimension.Exact(it) }
            offsetX?.also { this.offsetX = Dimension.Exact(it) }
            offsetY?.also { this.offsetY = Dimension.Exact(it) }
        }
    }

    fun shadowRelative(
        @ColorInt color: Int? = null,
        @FloatRange(from = 0.0, to = 1.0) radius: Float? = null,
        @FloatRange(from = -1.0, to = 1.0) offsetX: Float? = null,
        @FloatRange(from = -1.0, to = 1.0) offsetY: Float? = null
    ) = this.also {
        this.shadow = (shadow ?: Shadow()).apply {
            color?.also { this.color = it }
            radius?.also { this.radius = Dimension.Relative(it) }
            offsetX?.also { this.offsetX = Dimension.Relative(it) }
            offsetY?.also { this.offsetY = Dimension.Relative(it) }
        }
    }

    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
    fun alpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): Shape = this.also {
        this.alpha = Math.max(0F, Math.min(1F, alpha))
    }

    fun fill(@ColorInt color: Int): Shape = this.also {
        this.fill = (fill ?: Fill()).apply {
            this.color = color
            this.gradient = null
        }
    }

    fun fill(gradient: Gradient?): Shape = this.also {
        this.fill = (fill ?: Fill()).apply {
            this.gradient = gradient
            this.color = null
        }
    }

    fun stroke(
        @ColorInt color: Int,
        width: Int? = 1,
        dashWidth: Int? = null,
        dashGap: Int? = null
    ): Shape = this.also {
        stroke = (stroke ?: Stroke())
            .apply {
                this.color = color
                this.gradient = null
                width?.also { this.width = it }
                dashWidth?.also { this.dashWidth = it }
                dashGap?.also { this.dashGap = it }
            }
    }

    fun stroke(
        gradient: Gradient,
        width: Int? = null,
        dashWidth: Int? = null,
        dashGap: Int? = null
    ): Shape = this.also {
        stroke = (stroke ?: Stroke()).apply {
            this.color = null
            this.gradient = gradient
            width?.also { this.width = it }
            dashWidth?.also { this.dashWidth = it }
            dashGap?.also { this.dashGap = it }
        }
    }

    fun add(shape: Shape): Shape = this.also {
        children.add(shape)
    }

    fun remove(shape: Shape) = this.also {
        children.remove(shape)
    }

    //NB! all properties are `open` in order to be mocked in tests

    open var hidden: Boolean? = null

    open var width: Dimension? = null
    open var height: Dimension? = null

    open var gravity: Gravity? = null

    open var padding: Padding? = null

    open var translation: Translation? = null

    open var rotation: Rotation? = null

    open var shadow: Shadow? = null

    // applied to both fill and stroke and children
    open var alpha: Float? = null

    open var fill: Fill? = null

    open var stroke: Stroke? = null

    open val children: MutableList<Shape> = mutableListOf()

    internal val drawRect = Rect()
    internal val outlineRect = Rect()

    fun drawRect(): Rect = drawRect

    open fun draw(canvas: Canvas, bounds: Rect) {

        // shape is hidden, no draw
        if (true == hidden) {
            return
        }

        // if received empty bounds
        if (bounds.isEmpty) {
            return
        }

        // prepare bounds
        fillRect(bounds, drawRect)

        // if bounds are empty after padding w/h modification - do not draw
        if (drawRect.isEmpty) {
            return
        }

        val save = canvas.save()
        try {

            translation?.draw(canvas, drawRect)

            rotation?.draw(canvas, drawRect)

            shadow?.draw(canvas, this, drawRect)

            fill?.draw(canvas, this, drawRect)

            stroke?.draw(canvas, this, drawRect)

            val alpha = this.alpha

            //noinspection NewApi
            children.forEach {
                val childAlpha = it.alpha
                val childDrawAlpha = (childAlpha ?: 1F) * (alpha ?: 1F)

                // do not draw the shape if alpha would be 0
                if (childDrawAlpha > 0F) {
                    // if child had paint with alpha, it will be cleared always (even if parent has no alpha)
                    //  so, prioritize independent `alpha` usage, but prefill alpha value from color if it is supplied
                    it.alpha = childDrawAlpha

                    // draw the shape
                    it.draw(canvas, drawRect)

                    // restore initial value
                    it.alpha = childAlpha
                }
            }

        } finally {
            canvas.restoreToCount(save)
        }
    }

    abstract fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint)

    fun outline(outline: Outline, bounds: Rect) {

        if (true == hidden) {
            outline.setEmpty()
            return
        }

        fillRect(bounds, outlineRect)

        translation?.x
            ?.resolve(bounds.width())
            ?.also {
                outlineRect.left += it
                outlineRect.right += it
            }

        translation?.y
            ?.resolve(bounds.height())
            ?.also {
                outlineRect.top += it
                outlineRect.bottom += it
            }

        outlineShape(outline, outlineRect)

        // if we have generic alpha -> use it
        //  if we have fill colors and it has alpha -> use it, else just 1F
        // actually.. if fill color is 0, then we must also assume no transparency
        // if we specify 1F then outline would optimize shadow and draw it only for visible
        // parts, otherwise it executes a more advanced calculation
        outline.alpha = alpha ?: (fill?.color
            ?.let { Color.alpha(it) }
            ?.let { it / 255F }
            ?: 1F)
    }

    open fun outlineShape(outline: Outline, bounds: Rect) = Unit

    internal fun fillRect(bounds: Rect, rect: Rect) {

        val width = this.width?.resolve(bounds.width())
        val height = this.height?.resolve(bounds.height())

        // we need to apply gravity only if we have both sizes... (no reason to add gravity
        //  if we match bounds)
        if (width != null || height != null) {
            val w = width ?: bounds.width()
            val h = height ?: bounds.height()
            val gravity = this.gravity
            if (gravity != null) {
                android.view.Gravity.apply(
                    gravity.value,
                    w,
                    h,
                    bounds,
                    rect,
                    // MARK! Layout direction
                    View.LAYOUT_DIRECTION_LTR
                )
            } else {
                val left = bounds.left
                val top = bounds.top
                rect.set(
                    left,
                    top,
                    left + w,
                    top + h
                )
            }

        } else {
            rect.set(bounds)
        }

        // padding is applied to internal, so, we have width and height,
        //  and padding is applied in inner space
        padding?.set(rect)
    }

    final override fun toString(): String {
        return "Shape.${this::class.java.simpleName}(${toStringDedicatedProperties()}){hidden=$hidden, width=$width, height=$height, gravity=$gravity, padding=$padding, translation=$translation, rotation=$rotation, alpha=$alpha, fill=$fill, stroke=$stroke, children=$children, drawRect=$drawRect}"
    }

    internal class ShaderCache {
        private val bounds = Rect()

        private var shader: Shader? = null
        private var gradient: Gradient? = null

        fun shader(gradient: Gradient?, bounds: Rect, paint: Paint) {
            // if received gradient is null
            if (gradient == null) {
                this.shader = null
                this.gradient = null
                paint.shader = null
                return
            }

            if (gradient == this.gradient
                && bounds == this.bounds
                && paint.shader == shader
            ) {
                return
            }

            this.gradient = gradient
            this.bounds.set(bounds)

            gradient.createShader(bounds).also {
                this.shader = it
                paint.shader = it
            }
        }
    }

    class Padding(
        var leading: Dimension? = null,
        var top: Dimension? = null,
        var trailing: Dimension? = null,
        var bottom: Dimension? = null,
    ) {
        fun set(bounds: Rect) {
            // in case we have size specified... we apply padding to specified size?
            //  not the whole bounds? So, we have 24x24, and padding 2, this makes available
            //  width 20 (24 - 2 -2) and height 20 (24 - 2 - 2), instead of checking initial
            //  bounds, which can take the full bounds (like 100 by 100)
            val w = bounds.width()
            val h = bounds.height()

            // padding is applied to internal, so, we have width and height,
            //  and padding is applied in inner space
            // MARK! Layout direction
            leading?.resolve(w)?.also { bounds.left += it }
            top?.resolve(h)?.also { bounds.top += it }
            trailing?.resolve(w)?.also { bounds.right -= it }
            bottom?.resolve(h)?.also { bounds.bottom -= it }
        }

        fun copy(block: Padding.() -> Unit = {}): Padding =
            Padding(leading, top, trailing, bottom).also(block)


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Padding

            if (leading != other.leading) return false
            if (top != other.top) return false
            if (trailing != other.trailing) return false
            if (bottom != other.bottom) return false

            return true
        }

        override fun hashCode(): Int {
            var result = leading?.hashCode() ?: 0
            result = 31 * result + (top?.hashCode() ?: 0)
            result = 31 * result + (trailing?.hashCode() ?: 0)
            result = 31 * result + (bottom?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Padding(leading=$leading, top=$top, trailing=$trailing, bottom=$bottom)"
        }
    }

    class Translation(
        var x: Dimension? = null,
        var y: Dimension? = null
    ) {

        fun draw(canvas: Canvas, bounds: Rect) {
            // NB! we resolve x and y based on width/height, but we
            //  can receive bounds with left||top != 0, so we
            //  need to adjust for that values also
            // UPD: NAH! if size is specified with gravity, for example - end,
            //  we must not add bounds.left.. in fact.. we do we add it anyway?
            //  value is resolved based on proper dimensions, so just passing that
            //  values to canvas should be fine
            val translationX = x?.resolve(bounds.width())?.toFloat() ?: 0F
            val translationY = y?.resolve(bounds.height())?.toFloat() ?: 0F
            if (translationX != 0F || translationY != 0F) {
                canvas.translate(translationX, translationY)
            }
        }

        fun copy(block: Translation.() -> Unit = {}) = Translation(x, y).also(block)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Translation

            if (x != other.x) return false
            if (y != other.y) return false

            return true
        }

        override fun hashCode(): Int {
            var result = x?.hashCode() ?: 0
            result = 31 * result + (y?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Translation(x=$x, y=$y)"
        }
    }

    class Rotation(
        var degrees: Float? = null,
        var centerX: Dimension? = null,
        var centerY: Dimension? = null
    ) {

        fun draw(canvas: Canvas, bounds: Rect) {
            val degrees = this.degrees ?: return
            // NB! received bounds can have left||top != 0, we need to adjust
            //  centerX and centerY according to it (only if value is present, otherwise
            //  rect.centerX and centerY should return proper values)
            val cx = centerX?.resolve(bounds.width())?.plus(bounds.left)?.toFloat()
                ?: bounds.centerX().toFloat()
            val cy = centerY?.resolve(bounds.height())?.plus(bounds.top)?.toFloat()
                ?: bounds.centerY().toFloat()
            canvas.rotate(degrees, cx, cy)
        }

        fun copy(block: Rotation.() -> Unit = {}) = Rotation(degrees, centerX, centerY).also(block)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Rotation

            if (degrees != other.degrees) return false
            if (centerX != other.centerX) return false
            if (centerY != other.centerY) return false

            return true
        }

        override fun hashCode(): Int {
            var result = degrees?.hashCode() ?: 0
            result = 31 * result + (centerX?.hashCode() ?: 0)
            result = 31 * result + (centerY?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Rotation(degrees=$degrees, centerX=$centerX, centerY=$centerY)"
        }
    }

    class Shadow(
        var color: Int? = null,
        var radius: Dimension? = null,
        var offsetX: Dimension? = null,
        var offsetY: Dimension? = null
    ) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.FILL
        }

        private val maskFilterCache = MaskFilterCache()

        private val rect = Rect()

        fun copy(block: Shadow.() -> Unit = {}): Shadow =
            Shadow(color, radius, offsetX, offsetY).also(block)

        fun draw(canvas: Canvas, shape: Shape, bounds: Rect) {
            val color = this.color ?: return
            val radius = this.radius?.let {
                it.resolve(bounds.width())
                    .takeIf { v -> v > 0 }
                    ?: it.resolve(bounds.height()).takeIf { v -> v > 0 }
            } ?: return

            rect.set(bounds)

            offsetX?.resolve(bounds.width())?.also { x ->
                rect.left += x
                rect.right += x
            }

            offsetY?.resolve(bounds.height())?.also { y ->
                rect.top += y
                rect.bottom += y
            }

            paint.color = color
            paint.maskFilter = maskFilterCache.maskFilter(radius.toFloat())

            shape.drawShape(canvas, rect, paint)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Shadow

            if (color != other.color) return false
            if (radius != other.radius) return false
            if (offsetX != other.offsetX) return false
            if (offsetY != other.offsetY) return false

            return true
        }

        override fun hashCode(): Int {
            var result = color ?: 0
            result = 31 * result + (radius?.hashCode() ?: 0)
            result = 31 * result + (offsetX?.hashCode() ?: 0)
            result = 31 * result + (offsetY?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Shadow(color=$color, radius=$radius, offsetX=$offsetX, offsetY=$offsetY)"
        }

        private class MaskFilterCache {

            private var filter: BlurMaskFilter? = null
            private var radius: Float = 0F

            fun maskFilter(radius: Float): MaskFilter {
                // NB! We can allow customizing the Blue, like NORMAL, INSIDE, etc
                //  but it seems to be a highly optional
                val filter = this.filter

                if (filter != null && radius == this.radius) {
                    return filter
                }

                return BlurMaskFilter(
                    radius, BlurMaskFilter.Blur.NORMAL
                ).also { this.filter = it }
            }
        }
    }

    class Fill(
        var color: Int? = null,
        var gradient: Gradient? = null
    ) {
        private val shaderCache = ShaderCache()

        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.FILL
            it.color = defaultFillColor
        }

        fun copy(block: Fill.() -> Unit = {}): Fill = Fill(color, gradient).also(block)

        fun draw(canvas: Canvas, shape: Shape, bounds: Rect) {
            val fillColor = this.color ?: 0
            val fillGradient = this.gradient

            val hasColor = fillColor != 0
            val hasGradient = fillGradient != null

            // if there are no values, no need to draw anything
            if (!hasColor && !hasGradient) {
                return
            }

            // by default 1F (if null)
            val alpha = shape.alpha
            if (0F == alpha) {
                // if alpha is 0 -> no need to draw anything
                return
            }

            // gradient shader needs color to be drawn (with 255 alpha)
            val targetColor = if (hasColor) fillColor else defaultFillColor
            if (fillPaint.color != targetColor) {
                fillPaint.color = targetColor
            }

            // important to check if there is alpha, otherwise, a color with alpha component
            //  would be drawn without alpha (solid)

            // if alpha is null, do not apply it, as it would override paint value (in case
            //  color was specified with alpha channel)
            if (alpha != null) {
                fillPaint.alpha = (fillPaint.alpha * alpha).roundToInt()
            }

            if (fillPaint.alpha == 0) {
                // is it possible to have 0 alpha?
                // 2 possibilities: shape.alpha is 0, or paint.color alpha is 0
                // (as 0 would be possible only if one of parts is 0)
                return
            }

            // cache would clear shader is gradient is null
            shaderCache.shader(gradient, bounds, fillPaint)

            // finally draw the shape with paint initialized
            shape.drawShape(canvas, bounds, fillPaint)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Fill

            if (color != other.color) return false
            if (gradient != other.gradient) return false

            return true
        }

        override fun hashCode(): Int {
            var result = color ?: 0
            result = 31 * result + (gradient?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Fill(color=${color?.toHexString()}, gradient=$gradient)"
        }
    }

    // We could make them dimensions, but we would need to have a reference for them
    //  and it is not clear which width or height to use
    class Stroke(
        var color: Int? = null,
        var width: Int? = null,
        var dashWidth: Int? = null,
        var dashGap: Int? = null,
        var gradient: Gradient? = null,
    ) {
        private val shaderCache = ShaderCache()
        private val effectCache = DashEffectCache()

        private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }

        fun copy(block: Stroke.() -> Unit = {}): Stroke = Stroke(
            color,
            width,
            dashWidth,
            dashGap,
            gradient
        ).also(block)

        fun draw(canvas: Canvas, shape: Shape, bounds: Rect) {
            val strokeWidth = this.width ?: 1

            // if we have no stroke, do not draw
            if (strokeWidth < 1) {
                return
            }

            val strokeColor = this.color ?: 0
            val strokeGradient = this.gradient

            val hasColor = strokeColor != 0
            val hasGradient = strokeGradient != null

            // if we have no color and gradient - do not draw
            if (!hasColor && !hasGradient) {
                return
            }

            strokePaint.strokeWidth = strokeWidth.dip.toFloat()
            strokePaint.color = if (hasColor) strokeColor else defaultFillColor

            val alpha = shape.alpha
            if (alpha != null) {
                strokePaint.alpha = (strokePaint.alpha * alpha).roundToInt()
            }

            // if paint has no alpha - do not draw
            if (strokePaint.alpha == 0) {
                return
            }

            effectCache.effect(this, strokePaint)

            shaderCache.shader(gradient, bounds, strokePaint)

            shape.drawShape(canvas, bounds, strokePaint)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Stroke

            if (color != other.color) return false
            if (width != other.width) return false
            if (dashWidth != other.dashWidth) return false
            if (dashGap != other.dashGap) return false
            if (gradient != other.gradient) return false

            return true
        }

        override fun hashCode(): Int {
            var result = color ?: 0
            result = 31 * result + (width ?: 0)
            result = 31 * result + (dashWidth ?: 0)
            result = 31 * result + (dashGap ?: 0)
            result = 31 * result + (gradient?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Stroke(color=${color?.toHexString()}, width=$width, dashWidth=$dashWidth, dashGap=$dashGap, gradient=$gradient)"
        }

        private class DashEffectCache {
            private var lastDashWidth: Int? = null
            private var lastDashGap: Int? = null

            fun effect(stroke: Stroke, paint: Paint) {
                val dashWidth = stroke.dashWidth?.dip
                if (dashWidth != null) {
                    val dashGap = stroke.dashGap?.dip ?: (dashWidth / 4)
                    if (lastDashWidth != dashWidth
                        || lastDashGap != dashGap
                        || paint.pathEffect == null
                    ) {
                        lastDashWidth = dashWidth
                        lastDashGap = dashGap
                        paint.pathEffect = DashPathEffect(
                            floatArrayOf(dashWidth.toFloat(), dashGap.toFloat()),
                            0F
                        )
                    } // else it should be the same

                } else {
                    // clear dash path effect
                    if (paint.pathEffect != null) {
                        paint.pathEffect = null
                    }

                    this.lastDashGap = null
                    this.lastDashWidth = null
                }
            }
        }
    }
}