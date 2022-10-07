package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.shape.Dimension.Exact
import io.noties.adapt.ui.shape.Dimension.Relative
import io.noties.adapt.ui.util.dip
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Shape_Test {

    @Test
    fun copy() {
        class Input<T, out P : KProperty1<Shape, T>>(
            val property: P,
            val value: T
        )

        val inputs = listOf(
            Input(Shape::visible, false),
            Input(Shape::width, Exact(42)),
            Input(Shape::height, Relative(0.25F)),
            Input(Shape::gravity, Gravity.BOTTOM or Gravity.END),
            Input(Shape::rotation, 45F),
            Input(Shape::translateX, Exact(-19)),
            Input(Shape::translateY, Relative(0.56F)),
            Input(Shape::paddingLeading, Exact(2)),
            Input(Shape::paddingTop, Relative(1.12F)),
            Input(Shape::paddingTrailing, Exact(100)),
            Input(Shape::paddingBottom, Relative(-0.29F)),
            Input(Shape::alpha, 88F),
            Input(Shape::fillColor, 777),
            Input(Shape::fillGradient, SweepGradient(12, 999)),
            Input(Shape::strokeColor, 1000),
            Input(Shape::strokeWidth, 9),
            Input(Shape::strokeDashWidth, 6),
            Input(Shape::strokeDashGap, -1),
            Input(Shape::strokeGradient, RadialGradient(12, 211))
        )
        val inputChildren = Input(
            Shape::children,
            shapes()
        )

        // reflect available public properties to see if a new one had been added,
        //  but not added to the copy function
        val reflectedMissingProperties = Shape::class.members
            .filterIsInstance<KProperty1<out Shape, Any?>>()
            .filter { KVisibility.PUBLIC == it.visibility }
            .toSet()
            .subtract((inputs.map { it.property } + inputChildren.property).toSet())

        Assert.assertEquals(
            "Reflected properties",
            emptySet<KProperty1<out Shape, Any?>>(),
            reflectedMissingProperties
        )

        val base = Rectangle().apply {
            inputs.forEach {
                @Suppress("UNCHECKED_CAST")
                (it as Input<Any?, KMutableProperty1<Shape, Any?>>)
                    .property.set(this, it.value)
            }
            inputChildren.property.get(this)
                .addAll(inputChildren.value)
        }

        val shapes = shapes()

        fun assertShapeProperties(shape: Shape) {
            for (property in inputs) {
                Assert.assertEquals(
                    "${shape::class.java.simpleName}.${property.property.name}",
                    property.value,
                    property.property.get(shape)
                )
            }

            // children is unique, as they are also copied, check for classes
            val expectedChildren = inputChildren.value
                .map { it::class }
                .toList()
            val actualChildren = inputChildren.property.get(shape)
                .map { it::class }
                .toList()
            Assert.assertEquals(
                "${shape::class.java.simpleName}.${inputChildren.property.name}",
                expectedChildren,
                actualChildren
            )
        }

        assertShapeProperties(base)

        for (shape in shapes) {
            base.copy(shape)

            // assert shape
            assertShapeProperties(shape)

            // assert self copy
            val copy = shape.copy()
            Assert.assertEquals(shape::class.java.simpleName, shape::class.java, copy::class.java)
            assertShapeProperties(copy)
        }
    }

    @Test
    fun `copy - self`() {
        // when copying self, supplied block receives copied shape, not original
        val inputs = shapes()

        for (input in inputs) {
            val called = AtomicBoolean(false)
            input.copy {
                called.set(true)
                Assert.assertNotEquals(input::class.java.simpleName, input, this)
            }
            Assert.assertTrue(called.get())
        }
    }

    @Test
    fun `drawable - factory`() {
        val inputs = shapes()

        for (input in inputs) {
            val drawable = Shape.drawable(input)
            Assert.assertEquals(
                input::class.java.simpleName,
                ShapeDrawable::class.java,
                drawable::class.java
            )
            Assert.assertEquals(
                input::class.java.simpleName,
                input,
                drawable.shape
            )
        }
    }

    @Test
    fun `drawable - instance`() {
        val inputs = shapes()
        for (input in inputs) {
            val drawable = input.drawable()
            Assert.assertEquals(
                input::class.java.simpleName,
                input,
                drawable.shape
            )
        }
    }

    @Test
    fun visible() {
        for (shape in shapes()) {
            // by default `true`
            shape.assertEquals(true, Shape::visible)

            shape.visible(false)
            shape.assertEquals(false, Shape::visible)
        }
    }

    @Test
    fun size() {

        val inputs = listOf(
            Triple(null, null, null),
            Triple(1, null, null),
            Triple(null, 2, null),
            Triple(null, null, 3),
            Triple(4, 5, 6)
        )

        for (input in inputs) {
            for (shape in shapes()) {
                val width = Shape::width
                val height = Shape::height
                val gravity: KProperty1<Shape, Int?> = Shape::gravity

                // by default null
                shape.assertEquals(null, width)
                shape.assertEquals(null, height)
                shape.assertEquals(null, gravity)

                shape.size(input.first, input.second, input.third)

                shape.assertEquals(input.first?.let(::Exact), width)
                shape.assertEquals(input.second?.let(::Exact), height)
                shape.assertEquals(input.third, gravity)
            }
        }
    }

    @Test
    fun sizeRelative() {

        val inputs = listOf(
            Triple(null, null, null),
            Triple(1F, null, null),
            Triple(null, 2F, null),
            Triple(null, null, 3),
            Triple(4F, 5F, 6)
        )

        for (input in inputs) {
            for (shape in shapes()) {
                val width = Shape::width
                val height = Shape::height
                val gravity: KProperty1<Shape, Int?> = Shape::gravity

                // by default null
                shape.assertEquals(null, width)
                shape.assertEquals(null, height)
                shape.assertEquals(null, gravity)

                shape.sizeRelative(input.first, input.second, input.third)

                shape.assertEquals(input.first?.let(::Relative), width)
                shape.assertEquals(input.second?.let(::Relative), height)
                shape.assertEquals(input.third, gravity)
            }
        }
    }

    @Test
    fun gravity() {
        for (shape in shapes()) {
            // by default null
            shape.assertEquals(null, Shape::gravity)

            shape.gravity(87)
            shape.assertEquals(87, Shape::gravity)
        }
    }

    @Test
    fun rotate() {
        for (shape in shapes()) {
            shape.assertEquals(null, Shape::rotation)
            shape.rotate(180F)
            shape.assertEquals(180F, Shape::rotation)
        }
    }

    @Test
    fun `padding - all`() {
        for (shape in shapes()) {
            val s = Shape::paddingLeading
            val t = Shape::paddingTop
            val e = Shape::paddingTrailing
            val b = Shape::paddingBottom

            shape.assertEquals(null, s)
            shape.assertEquals(null, t)
            shape.assertEquals(null, e)
            shape.assertEquals(null, b)

            shape.padding(88)

            val expected = Exact(88)
            shape.assertEquals(expected, s)
            shape.assertEquals(expected, t)
            shape.assertEquals(expected, e)
            shape.assertEquals(expected, b)
        }
    }

    @Test
    fun `padding - vh`() {
        for (shape in shapes()) {
            val s = Shape::paddingLeading
            val t = Shape::paddingTop
            val e = Shape::paddingTrailing
            val b = Shape::paddingBottom

            shape.assertEquals(null, s)
            shape.assertEquals(null, t)
            shape.assertEquals(null, e)
            shape.assertEquals(null, b)

            shape.padding(12, 24)

            val h = Exact(12)
            val v = Exact(24)
            shape.assertEquals(h, s)
            shape.assertEquals(v, t)
            shape.assertEquals(h, e)
            shape.assertEquals(v, b)
        }
    }

    @Test
    fun `padding - individual`() {
        for (shape in shapes()) {
            val s = Shape::paddingLeading
            val t = Shape::paddingTop
            val e = Shape::paddingTrailing
            val b = Shape::paddingBottom

            shape.assertEquals(null, s)
            shape.assertEquals(null, t)
            shape.assertEquals(null, e)
            shape.assertEquals(null, b)

            shape.padding(12, 24, 48, 96)

            shape.assertEquals(Exact(12), s)
            shape.assertEquals(Exact(24), t)
            shape.assertEquals(Exact(48), e)
            shape.assertEquals(Exact(96), b)
        }
    }

    @Test
    fun `paddingRelative - all`() {
        for (shape in shapes()) {
            val s = Shape::paddingLeading
            val t = Shape::paddingTop
            val e = Shape::paddingTrailing
            val b = Shape::paddingBottom

            shape.assertEquals(null, s)
            shape.assertEquals(null, t)
            shape.assertEquals(null, e)
            shape.assertEquals(null, b)

            shape.paddingRelative(0.87F)

            val expected = Relative(0.87F)
            shape.assertEquals(expected, s)
            shape.assertEquals(expected, t)
            shape.assertEquals(expected, e)
            shape.assertEquals(expected, b)
        }
    }

    @Test
    fun `paddingRelative - vh`() {
        for (shape in shapes()) {
            val s = Shape::paddingLeading
            val t = Shape::paddingTop
            val e = Shape::paddingTrailing
            val b = Shape::paddingBottom

            shape.assertEquals(null, s)
            shape.assertEquals(null, t)
            shape.assertEquals(null, e)
            shape.assertEquals(null, b)

            shape.paddingRelative(0.87F, 0.25F)

            val h = Relative(0.87F)
            val v = Relative(0.25F)
            shape.assertEquals(h, s)
            shape.assertEquals(v, t)
            shape.assertEquals(h, e)
            shape.assertEquals(v, b)
        }
    }

    @Test
    fun `paddingRelative - individual`() {
        for (shape in shapes()) {
            val s = Shape::paddingLeading
            val t = Shape::paddingTop
            val e = Shape::paddingTrailing
            val b = Shape::paddingBottom

            shape.assertEquals(null, s)
            shape.assertEquals(null, t)
            shape.assertEquals(null, e)
            shape.assertEquals(null, b)

            shape.paddingRelative(0.87F, 0.25F, 0.1F, 0F)

            shape.assertEquals(Relative(0.87F), s)
            shape.assertEquals(Relative(0.25F), t)
            shape.assertEquals(Relative(0.1F), e)
            shape.assertEquals(Relative(0F), b)
        }
    }

    @Test
    fun translate() {
        val inputs = listOf(
            null to null,
            81 to null,
            null to 911,
            1092 to 2
        )
        for (input in inputs) {
            for (shape in shapes()) {
                shape.assertEquals(null, Shape::translateX)
                shape.assertEquals(null, Shape::translateY)

                shape.translate(input.first, input.second)

                shape.assertEquals(input.first?.let(::Exact), Shape::translateX)
                shape.assertEquals(input.second?.let(::Exact), Shape::translateY)
            }
        }
    }

    @Test
    fun translateRelative() {
        val inputs = listOf(
            null to null,
            0.81F to null,
            null to 0.911F,
            1.092F to 2F
        )
        for (input in inputs) {
            for (shape in shapes()) {
                shape.assertEquals(null, Shape::translateX)
                shape.assertEquals(null, Shape::translateY)

                shape.translateRelative(input.first, input.second)

                shape.assertEquals(input.first?.let(::Relative), Shape::translateX)
                shape.assertEquals(input.second?.let(::Relative), Shape::translateY)
            }
        }
    }

    @Test
    fun alpha() {
        for (shape in shapes()) {
            shape.assertEquals(null, Shape::alpha)
            shape.alpha(0.5F)
            shape.assertEquals(0.5F, Shape::alpha)
        }
    }

    @Test
    fun `fill - color`() {
        // NB! asset applies a fill color, otherwise it won't be drawn
        for (shape in shapes()) {
            if (shape is Asset) {
                shape.assertEquals(Asset.defaultFillColor, Shape::fillColor)
            } else {
                shape.assertEquals(null, Shape::fillColor)
            }

            shape.fill(99)
            shape.assertEquals(99, Shape::fillColor)
        }
    }

    @Test
    fun `fill - gradient`() {
        for (shape in shapes()) {
            shape.assertEquals(null, Shape::fillGradient)
            val gradient = RadialGradient(12, 24)
            shape.fill(gradient)
            shape.assertEquals(gradient, Shape::fillGradient)
        }
    }

    @Test
    fun `stroke - color`() {
        class Input(
            val color: Int,
            val strokeWidth: Int? = null,
            val dashWidth: Int? = null,
            val dashGap: Int? = null
        )

        val inputs = listOf(
            Input(1),
            Input(2, 3),
            Input(4, null, 5),
            Input(6, null, null, 7),
            Input(8, 9, 10, 11)
        )

        for (input in inputs) {
            for (shape in shapes()) {
                val color = Shape::strokeColor
                val strokeWidth = Shape::strokeWidth
                val dashWidth = Shape::strokeDashWidth
                val dashGap = Shape::strokeDashGap

                shape.assertEquals(null, color)
                shape.assertEquals(null, strokeWidth)
                shape.assertEquals(null, dashWidth)
                shape.assertEquals(null, dashGap)

                shape.stroke(input.color, input.strokeWidth, input.dashWidth, input.dashGap)

                shape.assertEquals(input.color, color)
                shape.assertEquals(input.strokeWidth, strokeWidth)
                shape.assertEquals(input.dashWidth, dashWidth)
                shape.assertEquals(input.dashGap, dashGap)
            }
        }
    }

    @Test
    fun `stroke - gradient`() {

        class Input(
            val gradient: Gradient = SweepGradient(112, 911),
            val strokeWidth: Int? = null,
            val dashWidth: Int? = null,
            val dashGap: Int? = null
        )

        val inputs = listOf(
            Input(),
            Input(strokeWidth = 3),
            Input(dashWidth = 5),
            Input(dashGap = 7),
            Input(strokeWidth = 9, dashWidth = 10, dashGap = 11)
        )

        for (input in inputs) {
            for (shape in shapes()) {
                val gradient = Shape::strokeGradient
                val strokeWidth = Shape::strokeWidth
                val dashWidth = Shape::strokeDashWidth
                val dashGap = Shape::strokeDashGap

                shape.assertEquals(null, gradient)
                shape.assertEquals(null, strokeWidth)
                shape.assertEquals(null, dashWidth)
                shape.assertEquals(null, dashGap)

                shape.stroke(input.gradient, input.strokeWidth, input.dashWidth, input.dashGap)

                shape.assertEquals(input.gradient, gradient)
                shape.assertEquals(input.strokeWidth, strokeWidth)
                shape.assertEquals(input.dashWidth, dashWidth)
                shape.assertEquals(input.dashGap, dashGap)
            }
        }
    }

    @Test
    fun `fillRect - no customization`() {
        // should use the same dimensions that supplied bounds have

        for (shape in shapes()) {
            val rect = Rect(0, 0, 109, 765)
            shape.fillRect(rect)
            shape.assertEquals(rect, Shape::fillRect)
        }
    }

    @Test
    fun `fillRect - no customization - padding`() {
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.padding(
                leading = 1,
                top = 2,
                trailing = 3,
                bottom = 4
            )
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(1.dip, 2.dip, 100 - 3.dip, 200 - 4.dip),
                Shape::fillRect
            )
        }
    }

    @Test
    fun `fillRect - size`() {
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(
                25,
                77
            )
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(0, 0, 25, 77),
                Shape::fillRect
            )
        }
    }

    @Test
    fun `fillRect - size - gravity`() {
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(
                10,
                20,
                Gravity.BOTTOM or Gravity.END
            )
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(100 - 10.dip, 200 - 20.dip, 100, 200),
                Shape::fillRect
            )
        }
    }

    @Test
    fun `fillRect - size - gravity - padding`() {
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(
                10,
                20,
                Gravity.BOTTOM or Gravity.END
            )
            shape.padding(4)
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(
                    100 - (10 - 4).dip,
                    200 - (20 - 4).dip,
                    100 - 4.dip,
                    200 - 4.dip
                ),
                Shape::fillRect
            )
        }
    }

    @Test
    fun `fillRect - size - incomplete`() {
        // no height - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(25)
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(0, 0, 25, 200),
                Shape::fillRect
            )
        }

        // no width - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(height = 15)
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(0, 0, 100, 15),
                Shape::fillRect
            )
        }
    }

    @Test
    fun `fillRect - size - incomplete - gravity`() {
        // no height - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(25, gravity = Gravity.CENTER)
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(
                    (100 - 25.dip) / 2,
                    0, // should take full height
                    (100 + 25.dip) / 2,
                    200 // should take full height
                ),
                Shape::fillRect
            )
        }

        // no width - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(height = 15, gravity = Gravity.BOTTOM)
            shape.fillRect(rect)
            shape.assertEquals(
                Rect(
                    0, // should take full width
                    200 - 15.dip,
                    100, // should take full width
                    200
                ),
                Shape::fillRect
            )
        }
    }

    @Test
    fun `draw - not-visible`() {
        // when it is not visible, it is not drawn
        val canvas = mock(Canvas::class.java)
        val bounds = Rect(0, 0, 100, 100)
        val shape = Rectangle().fill(6) // set fill color, so shape is drawn

        shape.visible(false)
        shape.draw(canvas, bounds)

        Mockito.verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - translate`() {
        val canvas = mock(Canvas::class.java)
        val bounds = Rect(0, 0, 100, 100)
        val shape = Oval().fill(3)

        shape.translate(10, 20)
        shape.draw(canvas, bounds)

        verify(canvas, times(1)).translate(
            eq(10.dip.toFloat()),
            eq(20.dip.toFloat())
        )
    }

    @Test
    fun `draw - translateRelative`() {
        val canvas = mock(Canvas::class.java)
        val bounds = Rect(0, 0, 100, 200)
        val shape = Capsule().fill(-19)

        shape.translateRelative(0.5F, 0.25F)
        shape.draw(canvas, bounds)

        verify(canvas, times(1)).translate(
            eq(shape.translateX?.resolve(bounds.width())!!.toFloat()),
            eq(shape.translateY?.resolve(bounds.height())!!.toFloat())
        )
    }

    @Test
    fun `draw - rotate`() {
        val canvas = mock(Canvas::class.java)
        val bounds = Rect(0, 0, 100, 200)
        val shape = RoundedRectangle(12).fill(-1908)

        shape.rotate(45F)
        shape.draw(canvas, bounds)

        verify(canvas).rotate(
            eq(45F),
            eq(bounds.centerX().toFloat()),
            eq(bounds.centerY().toFloat())
        )
    }

    private fun shapes() = listOf(
        Asset(ColorDrawable(99)),
        Capsule(),
        Circle(),
        Corners(),
        Oval(),
        Rectangle(),
        RoundedRectangle(17)
    )

    private fun <T : Any?> Shape.assertEquals(expected: T, property: KProperty1<Shape, T>) {
        Assert.assertEquals(
            this::class.java.simpleName + "." + property.name,
            expected,
            property.get(this)
        )
    }
}