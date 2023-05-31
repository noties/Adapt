package io.noties.adapt.ui.shape

import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi

typealias ShapeFactoryBuilder = ShapeFactory.() -> Shape
typealias ShapeFactoryRefBuilder<R> = ShapeFactory.(R) -> Shape

interface ShapeFactory {
    fun add(shape: Shape)

    fun <S : Shape> processAndAdd(children: S.() -> Unit): (S) -> Unit {
        return {
            children(it)
            add(it)
        }
    }

    /**
     * Special no-op implementation that exposes the factory methods,
     * but do not track of added shapes. Useful for initial (root) factory when
     * it is explicitly returns a shape
     */
    object NoOp : ShapeFactory {
        override fun add(shape: Shape) = Unit
    }
}

@Suppress("FunctionName")
fun ShapeFactory.Arc(
    startAngle: Float,
    sweepAngle: Float,
    useCenter: Boolean? = null,
    children: ArcShape.() -> Unit = {}
) = ArcShape(startAngle, sweepAngle, useCenter)
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Asset(
    drawable: Drawable,
    children: AssetShape.() -> Unit = {}
): AssetShape = AssetShape(drawable)
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Capsule(
    children: CapsuleShape.() -> Unit = {}
) = CapsuleShape()
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Circle(
    children: CircleShape.() -> Unit = {}
) = CircleShape()
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Corners(
    leadingTop: Int = 0,
    topTrailing: Int = 0,
    trailingBottom: Int = 0,
    bottomLeading: Int = 0,
    children: CornersShape.() -> Unit = {}
) = CornersShape(leadingTop, topTrailing, trailingBottom, bottomLeading)
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Line(
    children: LineShape.() -> Unit = {}
) = LineShape()
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Oval(
    children: OvalShape.() -> Unit = {}
) = OvalShape()
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Rectangle(
    children: RectangleShape.() -> Unit = {}
) = RectangleShape()
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.RoundedRectangle(
    radius: Int,
    children: RoundedRectangleShape.() -> Unit = {}
) = RoundedRectangleShape(radius)
    .also(processAndAdd(children))

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("FunctionName")
fun ShapeFactory.Text(
    text: CharSequence? = null,
    children: TextShape.() -> Unit = {}
) = TextShape(text)
    .also(processAndAdd(children))

@Suppress("FunctionName")
fun ShapeFactory.Label(
    text: String? = null,
    children: LabelShape.() -> Unit = {}
) = LabelShape(text)
    .also(processAndAdd(children))