package io.noties.adapt.ui.shape

import android.graphics.drawable.Drawable
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.reflect.KClass

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ShapeFactory_Test {

//    @Test
//    fun `no-op`() {
//        // no op impl does nothing... how can we test it?
//    }

    private lateinit var factory: ShapeFactory
    private lateinit var children: Shape.() -> Unit
    private lateinit var callback: (Shape) -> Unit

    @Test
    fun processAndAdd() {
        var refAdd: Shape? = null
        var refChildren: Shape? = null

        val shape = OvalShape()

        val factory: ShapeFactory = object : ShapeFactory {
            override fun add(shape: Shape) {
                refAdd = shape
            }
        }

        val callback = factory.processAndAdd<Shape> { refChildren = this }
        Assert.assertNull(refAdd)
        Assert.assertNull(refChildren)

        callback.invoke(shape)

        Assert.assertEquals(shape, refAdd)
        Assert.assertEquals(shape, refChildren)
    }

    @Before
    fun before() {
        children = mockt()
        callback = mockt()
        factory = mockt {
            on { processAndAdd(eq(children)) } doReturn callback
        }
    }

    @Test
    fun arc() {
        val startAngle = 3F
        val sweepAngle = 42F
        val useCenter = true

        val shape = factory.Arc(startAngle, sweepAngle, useCenter, children)
        assertFactory(ArcShape::class, shape)

        Assert.assertEquals(startAngle, shape.startAngle)
        Assert.assertEquals(sweepAngle, shape.sweepAngle)
        Assert.assertEquals(useCenter, shape.useCenter)
    }

    @Test
    fun asset() {
        val drawable: Drawable = mockt()

        val shape = factory.Asset(drawable, children)
        assertFactory(AssetShape::class, shape)

        Assert.assertEquals(drawable, shape.drawable)
    }

    @Test
    fun capsule() {
        val shape = factory.Capsule(children)
        assertFactory(CapsuleShape::class, shape)
    }

    @Test
    fun circle() {
        val shape = factory.Circle(children)
        assertFactory(CircleShape::class, shape)
    }

    @Test
    fun corners() {
        val lt = 2
        val tt = 4
        val tb = 8
        val bl = 16

        val shape = factory.Corners(
            leadingTop = lt,
            topTrailing = tt,
            trailingBottom = tb,
            bottomLeading = bl,
            children
        )
        assertFactory(CornersShape::class, shape)

        Assert.assertEquals(lt, shape.leadingTop)
        Assert.assertEquals(tt, shape.topTrailing)
        Assert.assertEquals(tb, shape.trailingBottom)
        Assert.assertEquals(bl, shape.bottomLeading)
    }

    @Test
    fun line() {
        val shape = factory.Line(children)
        assertFactory(LineShape::class, shape)
    }

    @Test
    fun oval() {
        val shape = factory.Oval(children)
        assertFactory(OvalShape::class, shape)
    }

    @Test
    fun rectangle() {
        val shape = factory.Rectangle(children)
        assertFactory(RectangleShape::class, shape)
    }

    @Test
    fun roundedRectangle() {
        val radius = 91
        val shape = factory.RoundedRectangle(radius, children)
        assertFactory(RoundedRectangleShape::class, shape)
    }

    @Test
    fun text() {
        val text = "Hello"

        val shape = factory.Text(text, children)
        assertFactory(TextShape::class, shape)
        
        Assert.assertEquals(text, shape.text)
    }

    private fun assertFactory(expected: KClass<out Shape>, receivedShape: Shape) {
        val shape = kotlin.run {
            val captor = ArgumentCaptor.forClass(Shape::class.java)
            verify(factory).processAndAdd(eq(children))
            verify(callback).invoke(captor.capture() ?: OvalShape())
            captor.value
        }
        Assert.assertEquals(expected, shape::class)
        Assert.assertEquals(receivedShape, shape)
    }
}