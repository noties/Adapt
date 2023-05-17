package io.noties.adapt.ui.shape

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.shape.Dimension.Exact
import io.noties.adapt.ui.shape.Dimension.Relative
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verifyNoInteractions
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
            Input(Shape::hidden, false),
            Input(Shape::width, Exact(42)),
            Input(Shape::height, Relative(0.25F)),
            Input(Shape::gravity, Gravity.bottom.trailing),
            Input(Shape::rotation, Shape.Rotation(259F, Exact(1), Relative(1F))),
            Input(Shape::shadow, Shape.Shadow(1, Exact(2), Relative(0.5F), Exact(16))),
            Input(Shape::translation, Shape.Translation(Exact(8), Relative(9F))),
            Input(Shape::padding, Shape.Padding(Exact(1), Relative(2F), Exact(3), Relative(0.5F))),
            Input(Shape::alpha, 88F),
            Input(
                Shape::fill,
                Shape.Fill(
                    8765,
                    LinearGradient.edges { trailing to bottom }
                        .setColors(87, 78)
                )
            ),
            Input(
                Shape::stroke, Shape.Stroke(
                    9172,
                    99,
                    1,
                    10,
                    SweepGradient(1231243, 8776)
                )
            ),
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
            base.copyTo(shape)

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

//    @Test
//    fun `drawable - factory`() {
//        val inputs = shapes()
//
//        for (input in inputs) {
//            val drawable = Shape.drawable(input)
//            Assert.assertEquals(
//                input::class.java.simpleName,
//                ShapeDrawable::class.java,
//                drawable::class.java
//            )
//            Assert.assertEquals(
//                input::class.java.simpleName,
//                input,
//                drawable.shape
//            )
//        }
//    }

    @Test
    fun `drawable - instance`() {
        val inputs = shapes()
        for (input in inputs) {
            val drawable = input.newDrawable()
            Assert.assertEquals(
                input::class.java.simpleName,
                input,
                drawable.shape
            )
        }
    }

    @Test
    fun hidden() {
        for (shape in shapes()) {
            // by default `null` (which means false)
            shape.assertEquals(null, Shape::hidden)

            shape.hidden(true)
            shape.assertEquals(true, Shape::hidden)
        }
    }

    @Test
    fun size() {

        val inputs = listOf(
            Triple(null, null, null),
            Triple(1, null, null),
            Triple(null, 2, null),
            Triple(null, null, Gravity.bottom),
            Triple(4, 5, Gravity.top.center)
        )

        for (input in inputs) {
            for (shape in shapes()) {
                val width = Shape::width
                val height = Shape::height
                val gravity: KProperty1<Shape, Gravity?> = Shape::gravity

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
            Triple(null, null, Gravity.bottom),
            Triple(4F, 5F, Gravity.center)
        )

        for (input in inputs) {
            for (shape in shapes()) {
                val width = Shape::width
                val height = Shape::height
                val gravity: KProperty1<Shape, Gravity?> = Shape::gravity

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
    fun `size - mix`() {
        // verify density
        Assert.assertEquals(1F, Resources.getSystem().displayMetrics.density)

        val shape = Circle {
            size(width = 12)
            sizeRelative(height = 0.5F)
        }

        shape.assertEquals(Exact(12), Shape::width)
        shape.assertEquals(Relative(0.5F), Shape::height)
    }

    @Test
    fun gravity() {
        for (shape in shapes()) {
            // by default null
            shape.assertEquals(null, Shape::gravity)

            shape.gravity(Gravity.trailing)
            shape.assertEquals(Gravity.trailing, Shape::gravity)
        }
    }

    @Test
    fun `padding - all`() {
        for (shape in shapes()) {
            val p: KMutableProperty1<Shape, Shape.Padding?> = Shape::padding
            shape.assertEquals(null, p)

            shape.padding(88)

            val s = Shape.Padding::leading
            val t = Shape.Padding::top
            val e = Shape.Padding::trailing
            val b = Shape.Padding::bottom

            val padding = shape.padding
            Assert.assertNotNull(padding)

            padding!!

            val expected = Exact(88)
            padding.assertEquals(expected, s)
            padding.assertEquals(expected, t)
            padding.assertEquals(expected, e)
            padding.assertEquals(expected, b)
        }
    }

    @Test
    fun `padding - vh`() {
        for (shape in shapes()) {
            val p: KMutableProperty1<Shape, Shape.Padding?> = Shape::padding
            shape.assertEquals(null, p)

            shape.padding(12, 24)

            val h = Exact(12)
            val v = Exact(24)

            val s = Shape.Padding::leading
            val t = Shape.Padding::top
            val e = Shape.Padding::trailing
            val b = Shape.Padding::bottom

            val padding = shape.padding
            Assert.assertNotNull(padding)

            padding!!

            padding.assertEquals(h, s)
            padding.assertEquals(v, t)
            padding.assertEquals(h, e)
            padding.assertEquals(v, b)
        }
    }

    @Test
    fun `padding - individual`() {
        for (shape in shapes()) {
            val p: KMutableProperty1<Shape, Shape.Padding?> = Shape::padding
            shape.assertEquals(null, p)

            shape.padding(12, 24, 48, 96)

            val s = Shape.Padding::leading
            val t = Shape.Padding::top
            val e = Shape.Padding::trailing
            val b = Shape.Padding::bottom

            val padding = shape.padding
            Assert.assertNotNull(padding)

            padding!!

            padding.assertEquals(Exact(12), s)
            padding.assertEquals(Exact(24), t)
            padding.assertEquals(Exact(48), e)
            padding.assertEquals(Exact(96), b)
        }
    }

    @Test
    fun `paddingRelative - all`() {
        for (shape in shapes()) {
            val p: KMutableProperty1<Shape, Shape.Padding?> = Shape::padding
            shape.assertEquals(null, p)

            shape.paddingRelative(0.87F)

            val expected = Relative(0.87F)

            val s = Shape.Padding::leading
            val t = Shape.Padding::top
            val e = Shape.Padding::trailing
            val b = Shape.Padding::bottom

            val padding = shape.padding
            Assert.assertNotNull(padding)

            padding!!

            padding.assertEquals(expected, s)
            padding.assertEquals(expected, t)
            padding.assertEquals(expected, e)
            padding.assertEquals(expected, b)
        }
    }

    @Test
    fun `paddingRelative - vh`() {
        for (shape in shapes()) {
            val p: KMutableProperty1<Shape, Shape.Padding?> = Shape::padding
            shape.assertEquals(null, p)

            shape.paddingRelative(0.87F, 0.25F)

            val h = Relative(0.87F)
            val v = Relative(0.25F)

            val s = Shape.Padding::leading
            val t = Shape.Padding::top
            val e = Shape.Padding::trailing
            val b = Shape.Padding::bottom

            val padding = shape.padding
            Assert.assertNotNull(padding)

            padding!!

            padding.assertEquals(h, s)
            padding.assertEquals(v, t)
            padding.assertEquals(h, e)
            padding.assertEquals(v, b)
        }
    }

    @Test
    fun `paddingRelative - individual`() {
        for (shape in shapes()) {
            val p: KMutableProperty1<Shape, Shape.Padding?> = Shape::padding
            shape.assertEquals(null, p)

            shape.paddingRelative(0.87F, 0.25F, 0.1F, 0F)

            val s = Shape.Padding::leading
            val t = Shape.Padding::top
            val e = Shape.Padding::trailing
            val b = Shape.Padding::bottom

            val padding = shape.padding
            Assert.assertNotNull(padding)

            padding!!

            padding.assertEquals(Relative(0.87F), s)
            padding.assertEquals(Relative(0.25F), t)
            padding.assertEquals(Relative(0.1F), e)
            padding.assertEquals(Relative(0F), b)
        }
    }

    @Test
    fun `padding - mix`() {
        val shape = Capsule {
            padding(leading = 1, top = 2)
            paddingRelative(trailing = 0.3F, bottom = 0.4F)
        }

        val padding = shape.padding!!

        padding.assertEquals(Exact(1), Shape.Padding::leading)
        padding.assertEquals(Exact(2), Shape.Padding::top)
        padding.assertEquals(Relative(0.3F), Shape.Padding::trailing)
        padding.assertEquals(Relative(0.4F), Shape.Padding::bottom)
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
                shape.assertEquals(null, Shape::translation)

                shape.translate(input.first, input.second)

                val translation = shape.translation!!

                translation.assertEquals(input.first?.let(::Exact), Shape.Translation::x)
                translation.assertEquals(input.second?.let(::Exact), Shape.Translation::y)
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
                shape.assertEquals(null, Shape::translation)

                shape.translateRelative(input.first, input.second)

                val translation = shape.translation!!

                translation.assertEquals(input.first?.let(::Relative), Shape.Translation::x)
                translation.assertEquals(input.second?.let(::Relative), Shape.Translation::y)
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
    fun `alpha - clamped`() {
        val inputs = listOf(
            -0.1F to 0F,
            -1000F to 0F,
            1.1F to 1F,
            1000F to 1F
        )
        for (input in inputs) {
            for (shape in shapes()) {
                shape.alpha(input.first)
                shape.assertEquals(input.second, Shape::alpha)
            }
        }
    }

    @Test
    fun shadow() {
        val inputs = kotlin.run {
            class Input(
                val color: Int? = null,
                val radius: Int? = null,
                val offsetX: Int? = null,
                val offsetY: Int? = null
            )
            listOf(
                Input(1),
                Input(2, 3),
                Input(4, 5, 6),
                Input(7, 8, 9, 10),
                Input(radius = 11),
                Input(offsetX = 12),
                Input(offsetY = 13)
            )
        }

        for (input in inputs) {
            for (shape in shapes()) {
                shape.assertEquals(null, Shape::shadow)

                shape.shadow(
                    input.color,
                    input.radius,
                    input.offsetX,
                    input.offsetY
                )

                val color = Shape.Shadow::color
                val radius = Shape.Shadow::radius
                val offsetX = Shape.Shadow::offsetX
                val offsetY = Shape.Shadow::offsetY

                fun assert(shadow: Shape.Shadow) {
                    shadow.assertEquals(input.color, color)
                    shadow.assertEquals(input.radius?.let { Exact(it) }, radius)
                    shadow.assertEquals(input.offsetX?.let { Exact(it) }, offsetX)
                    shadow.assertEquals(input.offsetY?.let { Exact(it) }, offsetY)
                }

                assert(shape.shadow!!)
                assert(shape.shadow!!.copy())
            }
        }
    }

    @Test
    fun `shadow - relative`() {
        val inputs = kotlin.run {
            class Input(
                val color: Int? = null,
                val radius: Float? = null,
                val offsetX: Float? = null,
                val offsetY: Float? = null
            )
            listOf(
                Input(1),
                Input(2, 3F),
                Input(4, 5F, 6F),
                Input(7, 8F, 9F, 10F),
                Input(radius = 11F),
                Input(offsetX = 12F),
                Input(offsetY = 13F)
            )
        }

        for (input in inputs) {
            for (shape in shapes()) {
                shape.assertEquals(null, Shape::shadow)

                shape.shadowRelative(
                    input.color,
                    input.radius,
                    input.offsetX,
                    input.offsetY
                )

                val color = Shape.Shadow::color
                val radius = Shape.Shadow::radius
                val offsetX = Shape.Shadow::offsetX
                val offsetY = Shape.Shadow::offsetY

                fun assert(shadow: Shape.Shadow) {
                    shadow.assertEquals(input.color, color)
                    shadow.assertEquals(input.radius?.let { Relative(it) }, radius)
                    shadow.assertEquals(input.offsetX?.let { Relative(it) }, offsetX)
                    shadow.assertEquals(input.offsetY?.let { Relative(it) }, offsetY)
                }

                assert(shape.shadow!!)
                assert(shape.shadow!!.copy())
            }
        }
    }

    @Test
    fun `fill - color`() {
        // NB! asset applies a fill color, otherwise it won't be drawn
        for (shape in shapes()) {
            if (shape is Asset) {
                val fill = shape.fill
                fill!!.assertEquals(Shape.defaultFillColor, Shape.Fill::color)
            } else {
                shape.assertEquals(null, Shape::fill)
            }

            shape.fill(99)
            shape.fill!!.assertEquals(99, Shape.Fill::color)
        }
    }

    @Test
    fun `fill - gradient`() {
        for (shape in shapes()) {
            if (shape is Asset) {
                val fill = shape.fill!!
                fill.assertEquals(Shape.defaultFillColor, Shape.Fill::color)
            } else {
                shape.assertEquals(null, Shape::fill)
            }
            val gradient = RadialGradient.center().setColors(12, 24)
            shape.fill(gradient)
            shape.fill!!.assertEquals(gradient, Shape.Fill::gradient)
        }
    }

    @Test
    fun `stroke - color`() {
        class Input(
            val color: Int,
            val width: Int? = null,
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
                shape.assertEquals(null, Shape::stroke)

                shape.stroke(input.color, input.width, input.dashWidth, input.dashGap)

                val color = Shape.Stroke::color
                val width = Shape.Stroke::width
                val dashWidth = Shape.Stroke::dashWidth
                val dashGap = Shape.Stroke::dashGap

                fun assert(stroke: Shape.Stroke) {
                    stroke.assertEquals(input.color, color)
                    stroke.assertEquals(input.width, width)
                    stroke.assertEquals(input.dashWidth, dashWidth)
                    stroke.assertEquals(input.dashGap, dashGap)
                }

                assert(shape.stroke!!)
                assert(shape.stroke!!.copy())
            }
        }
    }

    @Test
    fun `stroke - gradient`() {

        class Input(
            val gradient: Gradient = SweepGradient(112, 911),
            val width: Int? = null,
            val dashWidth: Int? = null,
            val dashGap: Int? = null
        )

        val inputs = listOf(
            Input(),
            Input(width = 3),
            Input(dashWidth = 5),
            Input(dashGap = 7),
            Input(width = 9, dashWidth = 10, dashGap = 11)
        )

        for (input in inputs) {
            for (shape in shapes()) {

                shape.assertEquals(null, Shape::stroke)

                shape.stroke(input.gradient, input.width, input.dashWidth, input.dashGap)

                val gradient = Shape.Stroke::gradient
                val width = Shape.Stroke::width
                val dashWidth = Shape.Stroke::dashWidth
                val dashGap = Shape.Stroke::dashGap

                fun assert(stroke: Shape.Stroke) {
                    stroke.assertEquals(input.gradient, gradient)
                    stroke.assertEquals(input.width, width)
                    stroke.assertEquals(input.dashWidth, dashWidth)
                    stroke.assertEquals(input.dashGap, dashGap)
                }

                assert(shape.stroke!!)
                assert(shape.stroke!!.copy())
            }
        }
    }

    @Test
    fun `fillRect - no customization`() {
        // should use the same dimensions that supplied bounds have

        for (shape in shapes()) {
            val rect = Rect(0, 0, 109, 765)
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(rect, Shape::drawRect)
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
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(1.dip, 2.dip, 100 - 3.dip, 200 - 4.dip),
                Shape::drawRect
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
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(0, 0, 25, 77),
                Shape::drawRect
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
                Gravity.bottom.trailing
            )
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(100 - 10.dip, 200 - 20.dip, 100, 200),
                Shape::drawRect
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
                Gravity.bottom.trailing
            )
            shape.padding(4)
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(
                    100 - (10 - 4).dip,
                    200 - (20 - 4).dip,
                    100 - 4.dip,
                    200 - 4.dip
                ),
                Shape::drawRect
            )
        }
    }

    @Test
    fun `fillRect - size - incomplete`() {
        // no height - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(25)
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(0, 0, 25, 200),
                Shape::drawRect
            )
        }

        // no width - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(height = 15)
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(0, 0, 100, 15),
                Shape::drawRect
            )
        }
    }

    @Test
    fun `fillRect - size - incomplete - gravity`() {
        // no height - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(25, gravity = Gravity.center)
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(
                    (100 - 25.dip) / 2,
                    0, // should take full height
                    (100 + 25.dip) / 2,
                    200 // should take full height
                ),
                Shape::drawRect
            )
        }

        // no width - uses bounds
        for (shape in shapes()) {
            val rect = Rect(0, 0, 100, 200)
            shape.size(height = 15, gravity = Gravity.bottom)
            shape.fillRect(rect, shape.drawRect)
            shape.assertEquals(
                Rect(
                    0, // should take full width
                    200 - 15.dip,
                    100, // should take full width
                    200
                ),
                Shape::drawRect
            )
        }
    }

    @Test
    fun `translate - bounds`() {
        // translate must use own bounds (after size is resolved)
        val rect = Rect(0, 0, 10, 20)
        val fillRect = Rect(2, 2, 8, 18)

        for (shape in shapes()) {
            val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
            val translation = Shape.Translation(Exact(3), Relative(0.25F))
            shape.padding(2)
                .also { it.translation = translation }
                .draw(canvas, rect)
            Assert.assertEquals(
                "fillRect:$fillRect shape.drawRect:${shape.drawRect}",
                fillRect,
                shape.drawRect
            )
            // NB! translation does not take into account bounds.left or bounds.top
            //  it just sends value to the canvas. This is done because, for example,
            //  there is a negative translation with gravity.end - adding bounds.left
            //  would result in wrong placement
            verify(canvas).translate(
                eq(3F),
                eq(4F)
            )
        }
    }

    @Test
    fun `rotate - bounds`() {
        // rotate must use own bounds (after size is resolved)

        val rect = Rect(0, 0, 10, 20)
        val fillRect = Rect(2, 2, 8, 18) // after 2 padding

        // rotation object receives fillRect bounds, not initial received bounds
        for (shape in shapes()) {
            val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
            val rotation = Shape.Rotation(1F, Exact(0), Relative(1F))
            shape.padding(2)
                .also { it.rotation = rotation }
                .draw(canvas, rect)
            Assert.assertEquals(
                "fillRect$fillRect shape.drawRect:${shape.drawRect}",
                fillRect,
                shape.drawRect
            )
            verify(canvas).rotate(
                eq(1F),
                eq(2 + 0F),
                eq(2 + 16F)
            )
        }
    }

    @Test
    fun rotate() {
        val inputs = listOf(
            Triple(1F, null, null),
            Triple(2F, 3, null),
            Triple(4F, null, 5),
            Triple(6F, 7, 8)
        )
        for (input in inputs) {
            for (shape in shapes()) {
                shape.assertEquals(null, Shape::rotation)
                shape.rotate(
                    input.first,
                    input.second,
                    input.third
                )
                val rotation = shape.rotation!!
                rotation.assertEquals(input.first, Shape.Rotation::degrees)
                rotation.assertEquals(input.second?.let(::Exact), Shape.Rotation::centerX)
                rotation.assertEquals(input.third?.let(::Exact), Shape.Rotation::centerY)
            }
        }
    }

    @Test
    fun rotateRelative() {
        val inputs = listOf(
            Triple(1F, null, null),
            Triple(2F, 3F, null),
            Triple(4F, null, 5F),
            Triple(6F, 7F, 8F)
        )
        for (input in inputs) {
            for (shape in shapes()) {
                shape.assertEquals(null, Shape::rotation)
                shape.rotateRelative(
                    input.first,
                    input.second,
                    input.third
                )
                val rotation = shape.rotation!!
                rotation.assertEquals(input.first, Shape.Rotation::degrees)
                rotation.assertEquals(input.second?.let(::Relative), Shape.Rotation::centerX)
                rotation.assertEquals(input.third?.let(::Relative), Shape.Rotation::centerY)
            }
        }
    }

    @Test
    fun `draw - not-visible`() {
        // when it is not visible, it is not drawn
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
        val bounds = Rect(0, 0, 100, 100)
        val shape = Rectangle().fill(6) // set fill color, so shape is drawn

        shape.hidden(true)
        shape.draw(canvas, bounds)

        Mockito.verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - empty bounds`() {
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
        val bounds = Rect(0, 0, 0, 0)
        val shape = Circle().fill(1234)

        Assert.assertTrue(bounds.toShortString(), bounds.isEmpty)

        shape.draw(canvas, bounds)

        verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - empty bounds after padding`() {
        // received bounds are not empty, but further modifications makes fillRect empty
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
        val rect = Rect(0, 0, 10, 10)
        Assert.assertFalse(rect.toShortString(), rect.isEmpty)

        val shape = RoundedRectangle(8)
            .fill(98712)
            .padding(11)

        shape.draw(canvas, rect)

        verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - empty bounds after size`() {
        // received bounds are not empty, but further modifications makes fillRect empty
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
        val rect = Rect(0, 0, 1000, 1000)
        Assert.assertFalse(rect.toShortString(), rect.isEmpty)

        val shape = RoundedRectangle(8)
            .fill(765)
            .size(0, 0)

        shape.draw(canvas, rect)

        verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - translate`() {
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
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
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
        val bounds = Rect(0, 0, 100, 200)
        val shape = Capsule().fill(-19)

        shape.translateRelative(0.5F, 0.25F)
        shape.draw(canvas, bounds)

        verify(canvas, times(1)).translate(
            eq(shape.translation?.x?.resolve(bounds.width())!!.toFloat()),
            eq(shape.translation?.y?.resolve(bounds.height())!!.toFloat())
        )
    }

    @Test
    fun `draw - rotate`() {
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
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

    @Test
    fun `draw - children`() {
        // children are drawn
        val children = (0 until 5)
            .map { io.noties.adapt.ui.testutil.mockt<Shape> { on { this.alpha } doReturn null } }

        val shape = Oval {
            children.forEach(this::add)
        }

        shape.draw(io.noties.adapt.ui.testutil.mockt(), Rect(0, 0, 100, 100))

        children.forEach {
            verify(it).draw(any(), any())
        }
    }

    @Test
    fun `draw - children - alpha`() {
        // if parent has alpha then it would be combined with child alpha
        // if child resulting alpha is 0, we do not draw it

        // alpha of parent shape
        val inputs = listOf(
            1F,
            0.5F,
            0.25F,
            0F
        )

        for (input in inputs) {
            val children = listOf(
                io.noties.adapt.ui.testutil.mockt<Shape> {
                    on { this.alpha } doReturn 1F
                },
                io.noties.adapt.ui.testutil.mockt {
                    on { this.alpha } doReturn 0.5F
                },
                io.noties.adapt.ui.testutil.mockt {
                    on { this.alpha } doReturn 0.25F
                }
            )

            val shape = Rectangle {
                children.forEach { add(it) }
                alpha(input)
            }

            shape.draw(io.noties.adapt.ui.testutil.mockt(), Rect(1, 2, 5, 8))

            for (child in children) {
                val expectedAlpha = (child.alpha ?: 1F) * (shape.alpha ?: 1F)
                if (0F == expectedAlpha) {
                    verify(child, never()).draw(any(), any())
                } else {
                    verify(child).draw(any(), any())

                    val captor = argumentCaptor<Float>()
                    verify(child, times(2)).alpha = captor.capture()
                    // first expected alpha is set, then initial value is applied back (after drawing)
                    Assert.assertEquals(
                        listOf(expectedAlpha, child.alpha!!),
                        captor.allValues
                    )
                }
            }
        }
    }

    @Test
    fun `draw - children - visible=false`() {
        // when parent is visible=false no children are drawn
        val children = (0 until 5)
            .map { io.noties.adapt.ui.testutil.mockt<Shape> { on { this.alpha } doReturn 1F } }

        val shape = Rectangle {
            children.forEach { add(it) }

            // mark as non visible
            hidden()
        }
        Assert.assertTrue(shape.hidden!!)

        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()

        shape.draw(canvas, io.noties.adapt.ui.testutil.mockt())

        // verify no calls to canvas draw methods were issued
        verifyNoInteractions(canvas)

        // verify each child was not called to draw when invisible
        children.forEach {
            verifyNoInteractions(it)
        }
    }

    @Test
    fun add() {
        for (shape in shapes()) {
            val child = Rectangle()
            Assert.assertEquals(shape.children.toString(), 0, shape.children.size)
            shape.add(child)
            Assert.assertEquals(
                listOf(child),
                shape.children
            )
        }
    }

    @Test
    fun remove() {
        for (shape in shapes()) {
            val child = Circle()
            shape.add(child)
            Assert.assertEquals(listOf(child), shape.children)
            shape.remove(child)
            Assert.assertEquals(listOf<Shape>(), shape.children)
        }
    }

    private fun shapes() = listOf(
        Asset(ColorDrawable(99)),
        Capsule(),
        Circle(),
        Corners(),
        Oval(),
        Rectangle(),
        RoundedRectangle(17),
        Arc(90F, 69F, true)
    )

    private fun <R : Any, T : Any?> R.assertEquals(expected: T, property: KProperty1<in R, T>) {
        Assert.assertEquals(
            this::class.java.simpleName + "." + property.name,
            expected,
            property.get(this)
        )
    }
}