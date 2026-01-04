package io.noties.adapt.ui.shape

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.Drawable
import io.noties.adapt.ui.state.ViewStateBuilder
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.eq
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
        val bounds = Rect(0, 0, 10, 22)
        val shape = mockt<Shape>()
        val drawable = ShapeDrawable.createActual(shape, Unit).also { it.bounds = bounds }
        val canvas = mockt<Canvas>()

        assertEquals(shape, drawable.shape)
        drawable.draw(canvas)

        verify(shape).draw(eq(canvas), eq(bounds))
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
        val bounds = Rect(0, 0, 11, 22)
        val shape = mockt<Shape>()
        val outline = mockt<Outline>()

        val drawable = ShapeDrawable.createActual(shape, Unit).also { it.bounds = bounds }
        drawable.getOutline(outline)

        verify(shape).outline(eq(outline), eq(bounds))
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

    private data class StatefulIntegrationInput(
        val name: String,
        val filterBuilder: ViewStateBuilder?,
        val states: List<IntArray>,
        val expectedResults: List<Boolean>,
        val expectedCallbackStates: List<Set<Int>>
    )

    @Test
    fun `stateful integration`() {
        val pressed = android.R.attr.state_pressed
        val enabled = android.R.attr.state_enabled

        val inputs = listOf(
            StatefulIntegrationInput(
                name = "stateful without filter",
                filterBuilder = null,
                states = listOf(
                    intArrayOf(pressed),
                    intArrayOf(pressed, enabled),
                    intArrayOf(pressed, enabled)
                ),
                expectedResults = listOf(true, true, false),
                expectedCallbackStates = listOf(
                    setOf(pressed),
                    setOf(pressed, enabled)
                )
            ),
            StatefulIntegrationInput(
                name = "stateful filtered by pressed",
                filterBuilder = { this.pressed },
                states = listOf(
                    intArrayOf(pressed),
                    intArrayOf(enabled),
                    intArrayOf(enabled)
                ),
                expectedResults = listOf(true, true, false),
                expectedCallbackStates = listOf(
                    setOf(pressed),
                    emptySet()
                )
            ),
            StatefulIntegrationInput(
                name = "stateful with empty filter",
                filterBuilder = { default },
                states = listOf(
                    intArrayOf(pressed),
                    intArrayOf(enabled)
                ),
                expectedResults = listOf(false, false),
                expectedCallbackStates = emptyList()
            )
        )

        for (input in inputs) {
            val callbacks = mutableListOf<Set<Int>>()
            val drawable = ShapeDrawable(RectangleShape())

            drawable.stateful(filter = input.filterBuilder) {
                callbacks += it.rawValues
            }

            assertEquals(true, drawable.isStateful)

            val results = input.states.map { state ->
                drawable.setState(state)
            }

            assertEquals(input.name, input.expectedResults, results)
            assertEquals(input.name, input.expectedCallbackStates, callbacks)

            drawable.clearStateful()
            assertEquals(false, drawable.isStateful)
            assertEquals(false, drawable.setState(intArrayOf(pressed)))
        }
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
