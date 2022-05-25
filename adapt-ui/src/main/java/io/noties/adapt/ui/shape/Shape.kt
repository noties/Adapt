package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.DashPathEffect
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.GravityInt
import io.noties.adapt.ui.dip

abstract class Shape {

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
        to.strokeColor = this.strokeColor
        to.strokeWidth = this.strokeWidth
        to.strokeDashWidth = this.strokeDashWidth
        to.strokeDashGap = this.strokeDashGap
        to.children.addAll(this.children.map { it.copy() })
    }

    fun drawable(): Drawable = ShapeDrawable(this)

    // if null, then use bounds value
    fun size(
        width: Int? = null,
        height: Int? = null,
        @GravityInt gravity: Int? = null
    ): Shape {
        this.width = width
        this.height = height
        this.gravity = gravity
        return this
    }

    fun gravity(@GravityInt gravity: Int?): Shape {
        this.gravity = gravity
        return this
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
        this.paddingLeading = leading
        this.paddingTop = top
        this.paddingTrailing = trailing
        this.paddingBottom = bottom
        return this
    }

    fun translate(x: Int? = null, y: Int? = null): Shape {
        this.translateX = x
        this.translateY = y
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

    fun stroke(
        @ColorInt color: Int,
        width: Int? = 1,
        dashWidth: Int? = null,
        dashGap: Int? = null
    ): Shape {
        this.strokeColor = color
        this.strokeWidth = width ?: 1
        this.strokeDashWidth = dashWidth
        this.strokeDashGap = dashGap
        return this
    }

    fun <S : Shape> add(shape: S, block: S.() -> Unit = {}): Shape {
        children.add(shape)
        block(shape)
        return this
    }

    var width: Int? = null
    var height: Int? = null

    private var gravity: Int? = null

    private var translateX: Int? = null
    private var translateY: Int? = null

    private var paddingLeading: Int? = null
    private var paddingTop: Int? = null
    private var paddingTrailing: Int? = null
    private var paddingBottom: Int? = null

    // applied to both fill and stroke and children (?)
    private var alpha: Float? = null

    private var fillColor: Int? = null

    private var strokeColor: Int? = null
    private var strokeWidth: Int? = null
    private var strokeDashWidth: Int? = null
    private var strokeDashGap: Int? = null

    private val children: MutableList<Shape> = mutableListOf()

    private val fillPaint: Paint by lazy(LazyThreadSafetyMode.NONE) { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val strokePaint by lazy(LazyThreadSafetyMode.NONE) { Paint(Paint.ANTI_ALIAS_FLAG) }

    private val fillRect = Rect()
    private val strokeRect = Rect()

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
            if (fillColor != null && fillColor != 0) {
                fillPaint.color = fillColor
                fillPaint.style = Paint.Style.FILL

                // important to check if there is alpha, otherwise, a color with alpha component
                //  would be drawn without alpha (solid)
                if (alpha != null) {
                    fillPaint.alpha = alphaInt
                }

                drawShape(canvas, fillRect, fillPaint)
            }

            val strokeColor = this.strokeColor
            val strokeWidth = this.strokeWidth

            if (strokeColor != null && strokeWidth != null && strokeColor != 0 && strokeWidth != 0) {
                strokePaint.strokeWidth = strokeWidth.dip.toFloat()
                strokePaint.style = Paint.Style.STROKE
                strokePaint.color = strokeColor

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
                val inset = (strokePaint.strokeWidth / 2.25F).toInt()
                strokeRect.inset(
                    inset,
                    inset
                )

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

        val (x, y) = translateX to translateY
        if (x != null) {
            fillRect.left += x.dip
            fillRect.right += x.dip
        }

        if (y != null) {
            fillRect.top += y.dip
            fillRect.bottom += y.dip
        }

        outlineShape(outline, fillRect)
        outline.alpha = alpha ?: 1F
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

        // padding is applied to internal? so, we have width and height,
        //  and padding is applied in inner space
        // TODO: layout direction?
        paddingLeading?.dip?.also { fillRect.left += it }
        paddingTop?.dip?.also { fillRect.top += it }
        paddingTrailing?.dip?.also { fillRect.right -= it }
        paddingBottom?.dip?.also { fillRect.bottom -= it }
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

