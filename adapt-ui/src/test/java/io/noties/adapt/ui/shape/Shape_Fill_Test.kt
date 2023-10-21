package io.noties.adapt.ui.shape

import android.graphics.Color
import android.graphics.Paint
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.testutil.ShadowPaint
import io.noties.adapt.ui.testutil.value
import io.noties.adapt.ui.testutil.withAlpha
import io.noties.adapt.ui.util.toHexString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], shadows = [ShadowPaint::class])
class Shape_Fill_Test {

    @Test
    fun copy() {
        val inputs = listOf(
            Shape.Fill(null, null),
            Shape.Fill(0),
            Shape.Fill(
                null,
                LinearGradient.edges { trailing to bottom.trailing }
                    .setColors(1, 7)
            ),
            Shape.Fill(1234, SweepGradient.center().setColors(182, 12134))
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
        val inputs = listOf(
            null to null,
            0 to null,
            null to SweepGradient.center().setColors(1, 9),
            98712 to RadialGradient.center().setColors(9887, 1)
        )

        val fills = listOf(
            Shape.Fill(1234567),
            Shape.Fill(
                null,
                LinearGradient.edges { bottom.trailing to bottom.trailing }
                    .setColors(9999, 888)
            ),
            Shape.Fill(
                456789,
                LinearGradient.edges { bottom.trailing to bottom }
                    .setColors(19999, 1888)
            ),
        )

        for ((color, gradient) in inputs) {
            for (fill in fills) {
                val copied = fill.copy {
                    this.color = color
                    this.gradient = gradient
                }
                assertNotEquals(fill, copied)
                assertEquals(Shape.Fill(color, gradient), copied)
            }
        }
    }

    @Test
    fun `draw - noop`() {
        // when there is no color and no gradient, fill won't trigger drawing
        val inputs = listOf(
            Shape.Fill(null, null),
            Shape.Fill(0, null)
        )

        val density = 10987F

        for (input in inputs) {
            val shape = mock<Shape>()

            input.draw(mock(), shape, mock(), density)

            verify(shape, never()).drawShape(
                any(),
                any(),
                any(),
                ArgumentMatchers.eq(density)
            )
        }
    }

    @Test
    fun `draw - color`() {
        // if color is present, it should draw
        val input = 0xFF123456.toInt()
        val density = 9F

        val fill = Shape.Fill(input, null)
        val shape = mock<Shape> {
            on { alpha } doReturn null
        }

        fill.draw(mock(), shape, mock(), density)

        val captor = argumentCaptor<Paint>()
        verify(shape).drawShape(
            any(),
            any(),
            captor.capture(),
            ArgumentMatchers.eq(density)
        )

        val paint = captor.value

        assertNull(shape.alpha)

        assertEquals("paint.style", Paint.Style.FILL, paint.style)
        assertEquals("paint.color", input, paint.color)
        assertEquals("paint.alpha", 255, paint.alpha)
    }

    @Test
    fun `draw - color - alpha`() {
        val inputs = listOf(
            0xFF123456.toInt(),
            0x80987654.toInt(),
            0x00ffffff
        )

        val alphas = listOf(
            1F,
            0.5F,
            0.33F,
            0.095F,
            0F
        )

        val density = 2F

        for (input in inputs) {
            for (alpha in alphas) {

                val fill = Shape.Fill(input, null)
                val shape = mock<Shape> {
                    on { this.alpha } doReturn alpha
                }

                fill.draw(mock(), shape, mock(), density)

                val colorAlpha = Color.alpha(input)
                val shapeAlpha = (colorAlpha * alpha).roundToInt()

                if (shapeAlpha == 0) {
                    verify(shape, never()).drawShape(any(), any(), any(), ArgumentMatchers.eq(density))
                } else {

                    val captor = argumentCaptor<Paint>()
                    verify(shape).drawShape(any(), any(), captor.capture(), ArgumentMatchers.eq(density))

                    val paint = captor.value

                    // if alpha was applied to paint, it would automatically adjust alpha channel?
                    assertEquals("paint.style", Paint.Style.FILL, paint.style)
                    assertEquals(
                        "paint.color",
                        input.withAlpha(shapeAlpha).toHexString(),
                        paint.color.toHexString()
                    )
                    assertEquals("paint.alpha", shapeAlpha, paint.alpha)
                }
            }
        }
    }

    @Test
    fun `draw - gradient - alpha`() {
        val alphas = listOf(
            0F,
            0.25F,
            0.5F,
            1F
        )

        val density = 7F

        for (alpha in alphas) {
            val shape = mock<Shape> {
                on { this.alpha } doReturn alpha
            }
            val gradient = mock<Gradient>(defaultAnswer = Mockito.RETURNS_MOCKS)
            val fill = Shape.Fill(null, gradient)
            fill.draw(mock(), shape, mock(), density)

            if (0F == alpha) {
                verify(shape, never()).drawShape(any(), any(), any(), ArgumentMatchers.eq(density))
            } else {
                val captor = argumentCaptor<Paint>()
                verify(shape).drawShape(any(), any(), captor.capture(), ArgumentMatchers.eq(density))

                val paint = captor.value
                assertEquals(
                    Shape.defaultFillColor.withAlpha((alpha * 255).roundToInt()).toHexString(),
                    paint.color.toHexString()
                )
                assertEquals((255 * alpha).roundToInt(), paint.alpha)

                verify(gradient).createShader(any(), any())
                assertNotNull("paint.shader", paint.shader)
            }
        }
    }
}