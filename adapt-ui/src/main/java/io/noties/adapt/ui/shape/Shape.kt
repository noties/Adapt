package io.noties.adapt.ui.shape

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.DashPathEffect
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.GravityInt
import io.noties.adapt.ui.dip
import io.noties.adapt.ui.gradient.Gradient
import kotlin.math.min

abstract class Shape {

    companion object {
        fun <S : Shape> drawable(shape: S, block: S.() -> Unit = {}): Drawable {
            block(shape)
            return ShapeDrawable(shape)
        }
    }

    // cannot have abstract data class
    abstract fun copy(block: Shape.() -> Unit = {}): Shape

    fun copy(to: Shape) {
        to.width = this.width
        to.height = this.height
        to.gravity = this.gravity
        to.translateX = this.translateX
        to.translateY = this.translateY
        to.paddingLeading = this.paddingLeading
        to.paddingTop = this.paddingTop
        to.paddingTrailing = this.paddingTrailing
        to.paddingBottom = this.paddingBottom
        to.alpha = this.alpha
        to.fillColor = this.fillColor
        to.fillGradient = this.fillGradient
        to.strokeColor = this.strokeColor
        to.strokeWidth = this.strokeWidth
        to.strokeDashWidth = this.strokeDashWidth
        to.strokeDashGap = this.strokeDashGap
        to.strokeGradient = this.strokeGradient
        to.children.addAll(this.children.map { it.copy() })
    }

    fun drawable(): Drawable = ShapeDrawable(this)

    // if null, then use bounds value
    fun size(
        width: Int? = null,
        height: Int? = null,
        @GravityInt gravity: Int? = null
    ): Shape {
        width?.also { this.width = it }
        height?.also { this.height = it }
        gravity?.also { this.gravity = it }
        return this
    }

    fun gravity(@GravityInt gravity: Int?) = this.also {
        gravity?.also { this.gravity = it }
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
        leading?.also { this.paddingLeading = it }
        top?.also { this.paddingTop = it }
        trailing?.also { this.paddingTrailing = it }
        bottom?.also { this.paddingBottom = it }
        return this
    }

    fun translate(x: Int? = null, y: Int? = null): Shape {
        x?.also { this.translateX = it }
        y?.also { this.translateY = it }
        return this
    }

    fun alpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): Shape {
        this.alpha = alpha
        return this
    }

    fun fill(@ColorInt color: Int): Shape {
        this.fillColor = color
        return this
    }

    fun fill(gradient: Gradient?) = this.also {
        this.fillGradient = gradient
    }

    fun stroke(
        @ColorInt color: Int,
        width: Int? = 1,
        dashWidth: Int? = null,
        dashGap: Int? = null
    ): Shape {
        this.strokeColor = color
        width?.also { this.strokeWidth = it }
        dashWidth?.also { this.strokeDashWidth = it }
        dashGap?.also { this.strokeDashGap = it }
        return this
    }

    fun stroke(
        gradient: Gradient,
        width: Int? = null,
        dashWidth: Int? = null,
        dashGap: Int? = null
    ) = this.also {
        this.strokeGradient = gradient
        width?.also { this.strokeWidth = it }
        dashWidth?.also { this.strokeDashWidth = it }
        dashGap?.also { this.strokeDashGap = it }
    }

    fun <S : Shape> add(shape: S, block: S.() -> Unit = {}): Shape {
        children.add(shape)
        block(shape)
        return this
    }

    var width: Int? = null
    var height: Int? = null

    var gravity: Int? = null

    var translateX: Int? = null
    var translateY: Int? = null

    var paddingLeading: Int? = null
    var paddingTop: Int? = null
    var paddingTrailing: Int? = null
    var paddingBottom: Int? = null

    // applied to both fill and stroke and children (?)
    var alpha: Float? = null

    var fillColor: Int? = null
    var fillGradient: Gradient? = null

    var strokeColor: Int? = null
    var strokeWidth: Int? = null
    var strokeDashWidth: Int? = null
    var strokeDashGap: Int? = null

    var strokeGradient: Gradient? = null

    fun children(): List<Shape> = children.toList()

    private val children: MutableList<Shape> = mutableListOf()

    private val fillPaint: Paint by lazy(LazyThreadSafetyMode.NONE) { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val strokePaint by lazy(LazyThreadSafetyMode.NONE) { Paint(Paint.ANTI_ALIAS_FLAG) }

    private val fillRect = Rect()
    private val strokeRect = Rect()

    private var fillShaderCache: ShaderCache? = null
    private var strokeShaderCache: ShaderCache? = null

    fun draw(canvas: Canvas, bounds: Rect) {
        val save = canvas.save()
        try {

            val offsetX = this.translateX
            val offsetY = this.translateY

            if (offsetX != null || offsetY != null) {
                canvas.translate(
                    offsetX?.dip?.toFloat() ?: 0F,
                    offsetY?.dip?.toFloat() ?: 0F
                )
            }

            fillRect(bounds)

            val alpha = this.alpha
            val alphaInt = ((alpha ?: 1F) * 255).toInt()

            val fillColor = this.fillColor
            val fillGradient = this.fillGradient
            // we fill if we have color OR gradient
            if ((fillGradient != null)
                || (fillColor != null && fillColor != 0)
            ) {

                fillPaint.style = Paint.Style.FILL

                if (fillColor != null) {
                    fillPaint.color = fillColor
                }

                // important to check if there is alpha, otherwise, a color with alpha component
                //  would be drawn without alpha (solid)
                if (alpha != null) {
                    fillPaint.alpha = alphaInt
                }

                // Gradient + Shader
                // actual gradient object (must be the same)
                // actual bounds used, because on new bounds new shader must be created
                if (fillGradient != null) {
                    // if fillPaint.shader != equal our
                    val cache = fillShaderCache
                    if (cache != null
                        && cache.gradient == fillGradient && fillRect == cache.bounds
                    ) {
                        // check that gradient and bounds are the same
                        fillPaint.shader = cache.shader
                    } else {
                        // create new cache instance
                        fillShaderCache = ShaderCache(
                            fillGradient,
                            fillRect
                        ).also {
                            fillPaint.shader = it.shader
                        }
                    }
                } else {
                    fillPaint.shader = null
                    fillShaderCache = null
                }

                drawShape(canvas, fillRect, fillPaint)
            }

            val strokeColor = this.strokeColor
            val strokeWidth = this.strokeWidth ?: 1
            val strokeGradient = this.strokeGradient

            if ((strokeColor != null || strokeGradient != null)
                && strokeColor != 0 && strokeWidth != 0
            ) {
                strokePaint.strokeWidth = strokeWidth.dip.toFloat()
                strokePaint.style = Paint.Style.STROKE
                if (strokeColor != null) {
                    strokePaint.color = strokeColor
                }

                if (alpha != null) {
                    strokePaint.alpha = alphaInt
                }

                val dashWidth = this.strokeDashWidth?.dip
                if (dashWidth != null) {
                    val dashGap = this.strokeDashGap?.dip ?: dashWidth / 4
                    strokePaint.pathEffect = DashPathEffect(
                        floatArrayOf(
                            dashWidth.toFloat(),
                            dashGap.toFloat()
                        ),
                        0F
                    )
                }

                strokeRect.set(fillRect)

                // "2.25" is a bit arbitrary - it is a little less than 2,
                //  so in rounded rectangles the corner curve is drawn properly
                //  can we even have a proper calculation here? so out-most border of stroke
                //      would fir exactly fill bounds? can this be done via simple inset?
                //      or would we need to recalculate everything - including corner radius...
//                val inset = (strokePaint.strokeWidth / 2.25F).toInt()
//                strokeRect.inset(
//                    inset,
//                    inset
//                )

                if (strokeGradient != null) {
                    val cache = strokeShaderCache
                    if (cache != null && cache.gradient == strokeGradient && cache.bounds == strokeRect) {
                        strokePaint.shader = cache.shader
                    } else {
                        fillShaderCache = ShaderCache(strokeGradient, strokeRect).also {
                            strokePaint.shader = it.shader
                        }
                    }
                } else {
                    strokePaint.shader = null
                    strokeShaderCache = null
                }

                drawShape(canvas, strokeRect, strokePaint)
            }

            children.forEach {
                val childAlpha = it.alpha
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
        fillRect(bounds)

        val (x, y) = translateX?.dip to translateY?.dip

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
        // parts, overwise it executes a more advanced calculation
        outline.alpha =
            alpha ?: (fillColor?.takeIf { Color.alpha(it) < 255 }?.toFloat())
                    ?: 1F
    }

    open fun outlineShape(outline: Outline, bounds: Rect) = Unit

    private fun fillRect(bounds: Rect) {

        val width = this.width?.dip
        val height = this.height?.dip

        // we need to apply gravity only if we have both sizes... (no reason to add gravity
        //  if we match bounds)
        if (width != null || height != null) {
            val w = width ?: bounds.width()
            val h = height ?: bounds.height()
            val gravity = this.gravity
            if (gravity != null) {
                Gravity.apply(
                    gravity,
                    w,
                    h,
                    bounds,
                    fillRect,
                    // TODO: the layout direction
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
        // TODO: layout direction?
        paddingLeading?.dip?.also { fillRect.left += it }
        paddingTop?.dip?.also { fillRect.top += it }
        paddingTrailing?.dip?.also { fillRect.right -= it }
        paddingBottom?.dip?.also { fillRect.bottom -= it }
    }

    private class ShaderCache(
        val gradient: Gradient,
        bounds: Rect
    ) {
        val bounds = Rect(bounds)
        val shader = gradient.createShader(bounds)
    }
}

class Oval : Shape() {

    companion object {
        operator fun invoke(block: Oval.() -> Unit): Oval {
            val shape = Oval()
            block(shape)
            return shape
        }
    }

    private val rectF = RectF()

    override fun copy(block: Shape.() -> Unit): Shape = Oval().also {
        this.copy(it)
        block(it)
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        rectF.set(bounds)
        canvas.drawOval(rectF, paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setOval(bounds)
    }
}

class Circle : Shape() {

    companion object {
        operator fun invoke(block: Circle.() -> Unit): Circle {
            val shape = Circle()
            block(shape)
            return shape
        }
    }

    override fun copy(block: Shape.() -> Unit): Shape = Circle().also {
        this.copy(it)
        block(it)
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        val radius = radius(bounds)
        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            radius.toFloat(),
            paint
        )
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        val centerX = bounds.centerX()
        val centerY = bounds.centerY()
        val radius = radius(bounds)
        outline.setOval(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    private fun radius(bounds: Rect): Int = Math.min(bounds.width(), bounds.height()) / 2
}

class Rectangle : Shape() {

    companion object {
        operator fun invoke(block: Rectangle.() -> Unit): Rectangle {
            val shape = Rectangle()
            block(shape)
            return shape
        }
    }

    override fun copy(block: Shape.() -> Unit): Shape = Rectangle().also {
        this.copy(it)
        block(it)
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        canvas.drawRect(bounds, paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setRect(bounds)
    }
}

class RoundedRectangle private constructor(private val radius: Float) : Shape() {

    constructor(radius: Int) : this(radius.dip.toFloat())

    companion object {
        operator fun invoke(radius: Int, block: RoundedRectangle.() -> Unit): RoundedRectangle {
            val shape = RoundedRectangle(radius)
            block(shape)
            return shape
        }
    }

    private val rectF = RectF()

    override fun copy(block: Shape.() -> Unit): Shape = RoundedRectangle(radius).also {
        this.copy(it)
        block(it)
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        rectF.set(bounds)
        canvas.drawRoundRect(rectF, radius, radius, paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setRoundRect(bounds, radius)
    }
}

class Corners private constructor(
    private val leadingTop: Float,
    private val topTrailing: Float,
    private val trailingBottom: Float,
    private val bottomLeading: Float
) : Shape() {

    constructor(
        leadingTop: Int? = null,
        topTrailing: Int? = null,
        trailingBottom: Int? = null,
        bottomLeading: Int? = null
    ) : this(
        leadingTop?.dip?.toFloat() ?: 0F,
        topTrailing?.dip?.toFloat() ?: 0F,
        trailingBottom?.dip?.toFloat() ?: 0F,
        bottomLeading?.dip?.toFloat() ?: 0F
    )

    companion object {
        operator fun invoke(
            leadingTop: Int? = null,
            topTrailing: Int? = null,
            trailingBottom: Int? = null,
            bottomLeading: Int? = null,
            block: Corners.() -> Unit
        ): Corners {
            val shape = Corners(leadingTop, topTrailing, trailingBottom, bottomLeading)
            block(shape)
            return shape
        }
    }

    private val path = Path()
    private val rectF = RectF()

    override fun copy(block: Shape.() -> Unit): Shape =
        Corners(
            leadingTop, topTrailing, trailingBottom, bottomLeading
        ).also {
            this.copy(it)
            block(it)
        }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        buildPath(bounds)

        canvas.drawPath(path, paint)
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        buildPath(bounds)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path)
        } else {
            @Suppress("DEPRECATION")
            outline.setConvexPath(path)
        }
    }

    private fun buildPath(bounds: Rect) {
        path.reset()

        val halfX = bounds.centerX().toFloat()
        val halfY = bounds.centerY().toFloat()

        val left = bounds.left.toFloat()
        val top = bounds.top.toFloat()
        val right = bounds.right.toFloat()
        val bottom = bounds.bottom.toFloat()

        val maxRadius = min(bounds.width(), bounds.height()) / 2F

        fun radius(value: Float): Float? {
            val radius = min(value, maxRadius)
            return if (radius > 0) {
                radius
            } else null
        }

        // leading
        path.moveTo(left, halfY)

        val leadingTop = radius(this.leadingTop)
        if (leadingTop != null) {
            val d = leadingTop * 2F
            rectF.set(left, top, left + d, top + d)
            path.lineTo(left, top + leadingTop)
            path.arcTo(rectF, 180F, 90F)
        } else {
            path.lineTo(left, top)
        }
        path.lineTo(halfX, top)

        val topTrailing = radius(this.topTrailing)
        if (topTrailing != null) {
            val d = topTrailing * 2F
            rectF.set(right - d, top, right, top + d)
            path.lineTo(right - topTrailing, top)
            path.arcTo(rectF, 270F, 90F)
        } else {
            path.lineTo(right, top)
        }
        path.lineTo(right, halfY)

        val trailingBottom = radius(this.trailingBottom)
        if (trailingBottom != null) {
            val d = trailingBottom * 2F
            rectF.set(right - d, bottom - d, right, bottom)
            path.lineTo(right, bottom - d)
            path.arcTo(rectF, 0F, 90F)
        } else {
            path.lineTo(right, bottom)
        }

        path.lineTo(halfX, bottom)

        val bottomLeading = radius(this.bottomLeading)
        if (bottomLeading != null) {
            val d = bottomLeading * 2F
            rectF.set(left, bottom - d, left + d, bottom)
            path.lineTo(left + bottomLeading, bottom)
            path.arcTo(rectF, 90F, 90F)
        } else {
            path.lineTo(left, bottom)
        }
        path.lineTo(left, halfY)
        path.close()
    }
}

class Capsule : Shape() {

    companion object {
        operator fun invoke(block: Capsule.() -> Unit): Capsule {
            val shape = Capsule()
            block(shape)
            return shape
        }
    }

    private val rectF = RectF()

    override fun copy(block: Shape.() -> Unit): Shape = Capsule().also {
        this.copy(it)
        block(it)
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        val radius = radius(bounds)

        rectF.set(bounds)

        canvas.drawRoundRect(
            rectF,
            radius,
            radius,
            paint
        )
    }

    override fun outlineShape(outline: Outline, bounds: Rect) {
        outline.setRoundRect(bounds, radius(bounds))
    }

    private fun radius(bounds: Rect): Float = Math.min(bounds.width(), bounds.height()) / 2F
}

// NB! it discards received paint (so, fill, nor stroke would function)
class Asset(private val resource: Drawable) : Shape() {

    // empty companion object in order to allow users adding factory-like creation methods
    companion object {}

    init {

        // we need fill value in order to trigger drawing
        fill(0xFF000000.toInt())

        // we need to report size, let's see if bounds are empty
        val density = Resources.getSystem().displayMetrics.density

        fun value(intrinsic: Int?): Int? = intrinsic
            ?.takeIf { it > 0 }
            ?.let { (it / density).toInt() }

        val w = value(resource.intrinsicWidth)
        val h = value(resource.intrinsicHeight)

        if (w != null && h != null) {
            size(w, h)
        }
    }

    override fun copy(block: Shape.() -> Unit): Shape = Asset(resource).also {
        this.copy(it)
        block(this)
    }

    override fun drawShape(canvas: Canvas, bounds: Rect, paint: Paint) {
        resource.bounds = bounds
        resource.alpha = paint.alpha
        resource.draw(canvas)
    }
}

class ShapeDrawable(private val shape: Shape) : Drawable() {

    override fun draw(canvas: Canvas) {
        shape.draw(canvas, bounds)
    }

    override fun getIntrinsicWidth(): Int {
        return shape.width?.dip ?: super.getIntrinsicWidth()
    }

    override fun getIntrinsicHeight(): Int {
        return shape.height?.dip ?: super.getIntrinsicHeight()
    }

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun getOutline(outline: Outline) {
        shape.outline(outline, bounds)
    }
}

