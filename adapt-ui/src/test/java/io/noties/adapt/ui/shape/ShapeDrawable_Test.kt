package io.noties.adapt.ui.shape

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.ui.util.DrawableState
import io.noties.adapt.ui.util.DrawableStateSet
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ShapeDrawable_Test {

    @Test
    fun invalidate() {
        class Ref

        val ref = Ref()

        val drawable = ShapeDrawable(ref) { RectangleShape() }
        val callback = mockt<Drawable.Callback>()
        drawable.callback = callback

        val called = AtomicBoolean()
        drawable.invalidate {
            called.set(true)
            assertEquals(ref, it)
        }
        // verify callback after `invalidate` block (invalidation happens after invoking it)
        verify(callback).invalidateDrawable(eq(drawable))
        assertEquals("called", true, called.get())
    }

    @Test
    fun draw() {
        val density = 1F
        val bounds = Rect(0, 0, 10, 22)
        val shape = mockt<Shape>()
        val drawable = ShapeDrawable.createActual(shape, Unit).also {
            it.bounds = bounds
            it.density = density
        }
        val canvas = mockt<Canvas>()

        assertEquals(shape, drawable.shape)
        drawable.draw(canvas)

        verify(shape).draw(eq(canvas), eq(bounds), eq(density))
    }

    @Test
    fun `intrinsic - exact`() {
        assertEquals(1F, Resources.getSystem().displayMetrics.density)

        val (width, height) = 23 to 911
        val shape = RectangleShape {
            size(width, height)
        }

        val drawable = ShapeDrawable(shape)
        assertEquals(width, drawable.intrinsicWidth)
        assertEquals(height, drawable.intrinsicHeight)
    }

    @Test
    fun `intrinsic - relative`() {
        assertEquals(1F, Resources.getSystem().displayMetrics.density)

        val shape = RectangleShape {
            sizeRelative(1F, 2F)
        }

        val drawable = ShapeDrawable(shape)
        assertEquals(-1, drawable.intrinsicWidth)
        assertEquals(-1, drawable.intrinsicHeight)
    }

    @Test
    fun getAlpha() {
        val inputs = listOf(
            255 to null,
            (25.5F).roundToInt() to 0.1F,
            0 to 0F
        )

        for ((expected, alpha) in inputs) {
            val shape = RectangleShape {
                if (alpha != null) {
                    this.alpha(alpha)
                }
            }
            val drawable = ShapeDrawable(shape)
            assertEquals(expected, drawable.alpha)
        }
    }

    @Test
    fun getOutline() {
        val density = 765F
        val bounds = Rect(0, 0, 11, 22)
        val shape = mockt<Shape>()
        val outline = mockt<Outline>()

        val drawable = ShapeDrawable.createActual(shape, Unit).also {
            it.bounds = bounds
            it.density = density
        }
        drawable.getOutline(outline)

        verify(shape).outline(eq(outline), eq(bounds), eq(density))
    }

    @Test
    fun `stateless by default`() {
        val drawable = ShapeDrawable(RectangleShape())
        assertEquals(false, drawable.isStateful)

        // no state change should we reported
        assertEquals(false, drawable.setState(intArrayOf()))
        assertEquals(false, drawable.setState(intArrayOf(0)))
        assertEquals(false, drawable.setState(intArrayOf(android.R.attr.state_enabled)))
    }

    @Test
    fun `stateful - all`() {
        val callbacks: ShapeDrawable<Unit>.(DrawableStateSet) -> Unit = mockt()

        // when no specific state is requested, all states are being reported
        val drawable = ShapeDrawable(RectangleShape())
        drawable.stateful(
            emptySet(),
            callbacks
        )
        assertEquals(true, drawable.isStateful)

        val inputs = listOf(
            intArrayOf(999, 888, 777, -666),
            intArrayOf(999, 888, 777),
            intArrayOf(999, 888, -666),
            intArrayOf(999, 888),
            intArrayOf(0, 1),
            intArrayOf(-1),
            intArrayOf(android.R.attr.state_enabled)
        )

        for (input in inputs) {
            assertEquals(true, drawable.setState(input))
        }
        val captor = ArgumentCaptor.forClass(DrawableStateSet::class.java)
        verify(
            callbacks, times(inputs.size)
        ).invoke(eq(drawable), captor.capture() ?: DrawableStateSet(intArrayOf(Int.MIN_VALUE)))

        captor.allValues.withIndex()
            .map { it.value.state to inputs[it.index] }
            .forEach { assertArrayEquals(it.first, it.second) }

        drawable.clearStateful()
        assertEquals(false, drawable.isStateful)
    }

    @Test
    fun `stateful - set`() {
        val callbacks: ShapeDrawable<Unit>.(DrawableStateSet) -> Unit = mockt()
        val drawableCallback: Drawable.Callback = mockt()
        val set = (DrawableState.activated + DrawableState.checked).toList()

        // when no specific state is requested, all states are being reported
        val drawable = ShapeDrawable(RectangleShape())
        drawable.callback = drawableCallback
        drawable.stateful(
            set.toSet(),
            callbacks
        )
        assertEquals(true, drawable.isStateful)

        val noTrigger = listOf(
            intArrayOf(999, 888, 777, -666),
            intArrayOf(999, 888, 777),
            intArrayOf(999, 888, -666),
            intArrayOf(999, 888),
            intArrayOf(0, 1),
            intArrayOf(-1),
            intArrayOf(android.R.attr.state_enabled)
        )

        // should not trigger change
        for (input in noTrigger) {
            assertEquals(false, drawable.setState(input))
        }

        // should trigger change
        val trigger = noTrigger.withIndex()
            .map { (i, a) ->
                when (i % 3) {
                    0 -> a.plus(set[0].value)
                    1 -> a.plus(set[1].value)
                    2 -> a.plus(intArrayOf(set[0].value, set[1].value))
                    else -> error("Unexpected index")
                }
            }

        for (input in trigger) {
            assertEquals(input.contentToString(), true, drawable.setState(input))
        }

        val captor = ArgumentCaptor.forClass(DrawableStateSet::class.java)
        verify(
            callbacks,
            times(trigger.size)
        ).invoke(eq(drawable), captor.capture() ?: DrawableStateSet(intArrayOf(Int.MIN_VALUE)))

        captor.allValues.withIndex()
            .forEach {
                assertArrayEquals(it.value.state, trigger[it.index])
            }

        drawable.clearStateful()
        assertEquals(false, drawable.isStateful)

        // invalidated each time state changes + first time when statefulness is requested +
        //  one time drawable is set stateless
        verify(drawableCallback, times(trigger.size + 2)).invalidateDrawable(eq(drawable))
    }

    @Test
    fun `factory - calls shape`() {
        // invoke(shape)
        kotlin.run {
            val shape = mockt<Shape>()
            ShapeDrawable.invoke(shape)
            verify(shape).newDrawable()
        }

        // invoke(shape, ref)
        kotlin.run {
            val shape = mockt<Shape>()
            val ref = Any()
            ShapeDrawable.invoke(ref, shape)
            verify(shape).newDrawable(eq(ref))
        }

        // invoke(ShapeFactory)
        kotlin.run {
            val shape = mockt<Shape>()
            ShapeDrawable.invoke { shape }
            verify(shape).newDrawable()
        }

        // invoke(ref, ShapeFactory)
        kotlin.run {
            val shape = mockt<Shape>()
            val ref = Any()
            ShapeDrawable.invoke(ref) { shape }
            verify(shape).newDrawable(eq(ref))
        }
    }

    @Test
    fun hotspot() {
        val drawable = ShapeDrawable.createActual(mockt(), Any())
        // nothing happens
        drawable.setHotspot(1F, 2F)

        val callback: ShapeDrawable<Any>.(Float, Float) -> Unit = mockt()
        drawable.hotspot(callback)

        drawable.setHotspot(3F, 4F)
        verify(callback).invoke(eq(drawable), eq(3F), eq(4F))

        drawable.clearHotspot()
        drawable.setHotspot(5F, 6F)
        verifyNoMoreInteractions(callback)
    }
}