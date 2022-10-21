package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
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
import io.noties.adapt.ui.util.toHexColor
import kotlin.math.roundToInt
import kotlin.reflect.KProperty1

// A new shape: Path (provide path building)
// TODO: test, copy method returns a new instance and block
// TODO: test padding exact/relative combined ok (padding copied)
abstract class Shape {

    companion object {
        fun drawable(shape: Shape): ShapeDrawable {
            return ShapeDrawable(shape)
        }

        fun <S : Shape, R : Any> drawable(
            shape: S,
            references: R,
            block: S.(R) -> Unit
        ): ShapeDrawableRef<R> {
            block(shape, references)
            return ShapeDrawableRef(shape, references)
        }
    }

    /**
     * `clone` creates a new instance of a shape. In order to create a customized
     * copy the [copy] function should be used
     */
    abstract fun clone(): Shape

    abstract fun toStringProperties(): String

    fun copyTo(to: Shape) {
        to.visible = this.visible
        to.width = this.width
        to.height = this.height
        to.gravity = this.gravity
        to.rotation = this.rotation
        to.translateX = this.translateX
        to.translateY = this.translateY
        to.padding = this.padding?.copy()
        to.alpha = this.alpha
        to.fill = this.fill?.copy()
        to.stroke = this.stroke?.copy()
        to.children.addAll(this.children.map { it.copy() })
    }

    fun drawable(): ShapeDrawable = ShapeDrawable(this)

    fun visible(visible: Boolean): Shape = this.also {
        it.visible = visible
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
     * Rotates shape around its center x and y coordinates by given angle in degrees.
     * NB! this does not change actual bounds of the shape
     */
    fun rotate(degrees: Float?): Shape = this.also {
        this.rotation = degrees
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

    fun translate(x: Int? = null, y: Int? = null): Shape = this.apply {
        x?.also { this.translateX = Dimension.Exact(it) }
        y?.also { this.translateY = Dimension.Exact(it) }
    }

    fun translateRelative(
        @FloatRange(from = -1.0, to = 1.0) x: Float? = null,
        @FloatRange(from = -1.0, to = 1.0) y: Float? = null
    ): Shape = this.apply {
        x?.also { this.translateX = Dimension.Relative(it) }
        y?.also { this.translateY = Dimension.Relative(it) }
    }

    fun alpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): Shape {
        this.alpha = alpha
        return this
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

    override fun toString(): String {
        // cannot infer type without explicit type (because fun and var share the same names)
        @Suppress("RemoveExplicitTypeArguments")
        val properties = listOf<KProperty1<Shape, Any?>>(
            Shape::visible,
            Shape::width,
            Shape::height,
            Shape::gravity,
            Shape::rotation,
            Shape::translateX,
            Shape::translateY,
            Shape::padding,
            Shape::alpha,
            Shape::fill,
            Shape::stroke,
            Shape::children
        ).map { it.name to it.get(this) }
            .filter { it.second != null }
            .joinToString(", ") {
                "${it.first}=${it.second}"
            }
        return "Shape.${this::class.java.simpleName}(${toStringProperties()}){$properties}"
    }

    var visible: Boolean = true

    var width: Dimension? = null
    var height: Dimension? = null

    var gravity: Gravity? = null

    var rotation: Float? = null

    var translateX: Dimension? = null
    var translateY: Dimension? = null

    var padding: Padding? = null

    // applied to both fill and stroke and children
    var alpha: Float? = null

    var fill: Fill? = null

    var stroke: Stroke? = null

    val children: MutableList<Shape> = mutableListOf()

    internal val fillRect = Rect()

    fun draw(canvas: Canvas, bounds: Rect) {

        if (!visible) {
            return
        }

        val save = canvas.save()
        try {

            val offsetX = this.translateX?.resolve(bounds.width())
            val offsetY = this.translateY?.resolve(bounds.height())

            if (offsetX != null || offsetY != null) {
                canvas.translate(
                    offsetX?.toFloat() ?: 0F,
                    offsetY?.toFloat() ?: 0F
                )
            }

            fillRect(bounds)

            rotation?.also {
                canvas.rotate(it, fillRect.centerX().toFloat(), fillRect.centerY().toFloat())
            }

            fill?.draw(canvas, this, fillRect)

            stroke?.draw(canvas, this, fillRect)

            val alpha = this.alpha

            children.forEach {
                val childAlpha = it.alpha

                // if child had paint with alpha, it will be cleared always (even if parent has no alpha)
                //  so, prioritize independent `alpha` usage, but prefill alpha value from color if it is supplied
                it.alpha = (childAlpha ?: 1F) * (alpha ?: 1F)

                it.draw(canvas, fillRect)
                it.alpha = childAlpha
            }

        } finally {
            canvas.restoreToCount(save)
        }
    }

    protected abstract fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint)

    fun outline(outline: Outline, bounds: Rect) {

        if (!visible) {
            outline.setEmpty()
            return
        }

        fillRect(bounds)

        val (x, y) = translateX?.resolve(bounds.width()) to translateY?.resolve(bounds.height())

        if (x != null) {
            fillRect.left += x
            fillRect.right += x
        }

        // NB! we need to update both top+bottom for translation
        if (y != null) {
            fillRect.top += y
            fillRect.bottom += y
        }

        outlineShape(outline, fillRect)

        // if we have generic alpha -> use it
        //  if we have fill colors and it has alpha -> use it, else just 1F
        // actually.. if fill color is 0, then we must also assume no transparency
        // if we specify 1F then outline would optimize shadow and draw it only for visible
        // parts, otherwise it executes a more advanced calculation
        outline.alpha =
            alpha ?: (fill?.color?.takeIf { Color.alpha(it) < 255 }?.toFloat())
                    ?: 1F
    }

    open fun outlineShape(outline: Outline, bounds: Rect) = Unit

    internal fun fillRect(bounds: Rect) {

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
                    fillRect,
                    // MARK! Layout direction
                    View.LAYOUT_DIRECTION_LTR
                )
            } else {
                val left = bounds.left
                val top = bounds.top
                fillRect.set(
                    left,
                    top,
                    left + w,
                    top + h
                )
            }

        } else {
            fillRect.set(bounds)
        }

        // padding is applied to internal, so, we have width and height,
        //  and padding is applied in inner space
        padding?.set(fillRect)
    }

    private class ShaderCache {
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

        override fun toString(): String {
            val properties = listOf(
                ::leading,
                ::top,
                ::trailing,
                ::bottom
            ).map { it.name to it.get() }
                .filter { it.second != null }
                .joinToString(", ") {
                    "${it.first}=${it.second}"
                }
            return "Padding($properties)"
        }
    }

    class Fill(
        var color: Int? = null,
        var gradient: Gradient? = null
    ) {
        private val shaderCache = ShaderCache()

        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun copy(block: Fill.() -> Unit = {}): Fill = Fill(color, gradient).also(block)

        fun draw(canvas: Canvas, shape: Shape, bounds: Rect) {
            val fillColor = this.color ?: 0
            val fillGradient = this.gradient

            // we fill if we have color OR gradient
            if (fillGradient != null || fillColor != 0) {

                fillPaint.style = Paint.Style.FILL

                if (fillColor != 0) {
                    fillPaint.color = fillColor
                }

                // important to check if there is alpha, otherwise, a color with alpha component
                //  would be drawn without alpha (solid)
                val alpha = shape.alpha
                if (alpha != null) {
                    fillPaint.alpha = (fillPaint.alpha * alpha).roundToInt()
                }

                shaderCache.shader(gradient, bounds, fillPaint)

                shape.drawShape(canvas, bounds, fillPaint)
            }
        }

        override fun toString(): String {
            val properties = listOf(
                ::color to color?.toHexColor(),
                ::gradient to gradient
            ).map {
                it.first.name to it.second
            }.filter { it.second != null }
                .joinToString(", ") {
                    "${it.first}=${it.second}"
                }
            return "Shape.Fill($properties)"
        }
    }

    class Stroke(
        var color: Int? = null,
        var width: Int? = null,
        var dashWidth: Int? = null,
        var dashGap: Int? = null,
        var gradient: Gradient? = null,
    ) {
        private val shaderCache = ShaderCache()
        private val effectCache = DashEffectCache()

        private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun copy(block: Stroke.() -> Unit = {}): Stroke = Stroke(
            color,
            width,
            dashWidth,
            dashGap,
            gradient
        ).also(block)

        fun draw(canvas: Canvas, shape: Shape, bounds: Rect) {
            val strokeColor = this.color ?: 0
            val strokeWidth = this.width ?: 1
            val strokeGradient = this.gradient

            if (strokeWidth > 0 && (strokeColor != 0 || strokeGradient != null)) {
                strokePaint.strokeWidth = strokeWidth.dip.toFloat()
                strokePaint.style = Paint.Style.STROKE

                if (strokeColor != 0) {
                    strokePaint.color = strokeColor
                }

                val alpha = shape.alpha
                if (alpha != null) {
                    strokePaint.alpha = (strokePaint.alpha * alpha).roundToInt()
                }

                effectCache.effect(this, strokePaint)

                shaderCache.shader(gradient, bounds, strokePaint)

                shape.drawShape(canvas, bounds, strokePaint)
            }
        }

        override fun toString(): String {
            val colorProperty = (::color to { color?.toHexColor() })
                .let { it.first.name to it.second.invoke() }
            val properties = listOf(colorProperty) + (listOf(
                ::width,
                ::dashWidth,
                ::dashGap,
                ::gradient
            ).map {
                it.name to it.get()
            }).filter { it.second != null }
                .joinToString(", ") {
                    "${it.first}=${it.second}"
                }
            return "Shape.Stroke($properties)"
        }

        private class DashEffectCache {
            private var lastDashWidth: Int? = null
            private var lastDashGap: Int? = null

            fun effect(stroke: Stroke, paint: Paint) {
                val dashWidth = stroke.dashWidth?.dip
                if (dashWidth != null) {
                    val dashGap = stroke.dashGap?.dip ?: dashWidth / 4
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