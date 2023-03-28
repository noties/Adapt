package io.noties.adapt.ui.shape

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.shape.Shape.Stroke
import io.noties.adapt.ui.testutil.ShadowPaint
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.ui.testutil.value
import io.noties.adapt.ui.testutil.withAlpha
import io.noties.adapt.ui.util.toHexString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], shadows = [io.noties.adapt.ui.testutil.ShadowPaint::class])
class ShapeStroke_Test {

    @Test
    fun copy() {
        val inputs = listOf(
            Stroke(),
            Stroke(color = 0),
            Stroke(color = 1023),
            Stroke(width = 12),
            Stroke(dashWidth = 24),
            Stroke(dashGap = 36),
            Stroke(
                gradient = LinearGradient(
                    GradientEdge.Trailing to GradientEdge.BottomTrailing,
                    1,
                    7
                )
            ),
            Stroke(87812, 88, 123, 987, SweepGradient(182, 12134))
        )

        for (input in inputs) {
            val copied = input.copy()
            // verify different instance
            assertNotEquals(
                input.toString(),
                System.identityHashCode(input),
                System.identityHashCode(copied)
            )
            // verify equals
            assertEquals(input.toString(), input, copied)
        }
    }

    @Test
    fun `copy - customize`() {
        class Input(
            var color: Int? = null,
            var width: Int? = null,
            var dashWidth: Int? = null,
            var dashGap: Int? = null,
            var gradient: Gradient? = null,
        )

        val inputs = listOf(
            Input(color = 1234),
            Input(width = 213),
            Input(dashWidth = 91),
            Input(dashGap = 991),
            Input(gradient = SweepGradient(1, 9)),
            Input(7126, 1, 2134, 12, RadialGradient(123, 2))
        )

        val strokes = listOf(
            Stroke(color = -1234),
            Stroke(width = -213),
            Stroke(dashWidth = -91),
            Stroke(dashGap = -991),
            Stroke(
                gradient = LinearGradient(
                    GradientEdge.BottomTrailing to GradientEdge.BottomTrailing,
                    9999,
                    888
                )
            ),
            Stroke(
                -1234,
                -213,
                -91,
                -991,
                LinearGradient(
                    GradientEdge.BottomTrailing to GradientEdge.BottomTrailing,
                    9999,
                    888
                )
            ),
        )

        for (input in inputs) {
            for (stroke in strokes) {
                val copied = stroke.copy {
                    this.color = input.color
                    this.width = input.width
                    this.dashWidth = input.dashWidth
                    this.dashGap = input.dashGap
                    this.gradient = input.gradient
                }
                assertNotEquals(stroke, copied)
                assertEquals(
                    Stroke(
                        input.color,
                        input.width,
                        input.dashWidth,
                        input.dashGap,
                        input.gradient
                    ),
                    copied
                )
            }
        }
    }

    @Test
    fun `draw - noop`() {
        // when width < 1 -> no draw
        // when color == null AND gradient == null -> no draw

        // stroke uses default value for stroke -> 1
        val inputs = listOf(
            Stroke(),
            Stroke(width = 0, color = 567, dashGap = 1, dashWidth = 27),
            Stroke(width = 0, gradient = io.noties.adapt.ui.testutil.mockt()),
            Stroke(width = 10, color = null),
            Stroke(width = 123, gradient = null)
        )

        for (input in inputs) {
            val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
            // important to use real object, not mock in order to see if canvas was called
            input.draw(canvas, Oval(), io.noties.adapt.ui.testutil.mockt())
            verifyNoInteractions(canvas)
        }
    }

    @Test
    fun `draw - noop - shape-alpha`() {
        // when resulting alpha would be 0 -> no draw
        val stroke = Stroke(width = 1, color = 0xFFff0000.toInt())
        // important to use real object, not mock in order to see if canvas was called
        val shape = Rectangle().alpha(0F)
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
        stroke.draw(canvas, shape, io.noties.adapt.ui.testutil.mockt())

        verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - noop - color-alpha`() {
        // when stroke paint color alpha is 0 -> no draw
        val stroke = Stroke(0x00FFFFFF, 99)
        // important to use real object, not mock in order to see if canvas was called
        val shape = Oval().alpha(1F)
        val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
        stroke.draw(canvas, shape, io.noties.adapt.ui.testutil.mockt())
        verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - color`() {
        val inputs = listOf(
            0xFF000000.toInt(),
            0x80cccccc.toInt(),
            0x00ffffff.toInt()
        )

        for (input in inputs) {
            val stroke = Stroke(input)
            val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
            val shape = io.noties.adapt.ui.testutil.mockt<Shape> {
                on { this.alpha } doReturn 1F
            }

            stroke.draw(canvas, shape, io.noties.adapt.ui.testutil.mockt())

            if (Color.alpha(input) == 0) {
                verify(shape, never()).drawShape(any(), any(), any())
            } else {
                val captor = argumentCaptor<Paint>()
                verify(shape).drawShape(
                    eq(canvas),
                    any(),
                    captor.capture()
                )

                assertEquals(input.toHexString(), captor.value.color.toHexString())
            }
        }
    }

    @Test
    fun `draw - color - alpha`() {
        val inputs = listOf(
            0xFF000000.toInt(),
            0x80cccccc.toInt(),
            0x00ffffff.toInt()
        )

        val alphas = listOf(
            1F,
            0.75F,
            0.5F,
            0.1F,
            0.05F,
            0.001F,
            0F
        )

        for (input in inputs) {
            for (alpha in alphas) {
                val stroke = Stroke(input)
                val canvas = io.noties.adapt.ui.testutil.mockt<Canvas>()
                val shape = io.noties.adapt.ui.testutil.mockt<Shape> {
                    on { this.alpha } doReturn alpha
                }

                stroke.draw(canvas, shape, io.noties.adapt.ui.testutil.mockt())

                val expectedAlpha = (255 * ((Color.alpha(input) / 255F) * alpha)).roundToInt()

                if (expectedAlpha == 0) {
                    verify(shape, never()).drawShape(any(), any(), any())
                } else {
                    val captor = argumentCaptor<Paint>()
                    verify(shape).drawShape(
                        eq(canvas),
                        any(),
                        captor.capture()
                    )

                    assertEquals(
                        input.withAlpha(expectedAlpha).toHexString(),
                        captor.value.color.toHexString()
                    )
                    assertEquals(
                        expectedAlpha,
                        captor.value.alpha
                    )
                }
            }
        }
    }

    @Test
    fun `draw - gradient - alpha`() {
        val gradient = SweepGradient(1982, 891)
        val input = 0.25F
        val stroke = Stroke(gradient = gradient)
        val shape = io.noties.adapt.ui.testutil.mockt<Shape> {
            on { this.alpha } doReturn input
        }
        stroke.draw(io.noties.adapt.ui.testutil.mockt(), shape, io.noties.adapt.ui.testutil.mockt())

        val captor = argumentCaptor<Paint>()
        verify(shape).drawShape(
            any(),
            any(),
            captor.capture()
        )

        assertEquals((255 * input).roundToInt(), captor.value.alpha)
    }
}