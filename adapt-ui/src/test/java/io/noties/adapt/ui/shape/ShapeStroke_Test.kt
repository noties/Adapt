package io.noties.adapt.ui.shape

import android.graphics.Canvas
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.gradient.GradientEdge
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.gradient.SweepGradient
import io.noties.adapt.ui.shape.Shape.Stroke
import io.noties.adapt.ui.testutil.ShadowPaint
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], shadows = [ShadowPaint::class])
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

        val inputs = listOf(
            Stroke(),
            Stroke(width = null, color = 567, dashGap = 1, dashWidth = 27),
            Stroke(width = null, gradient = mockt()),
            Stroke(width = 10, color = null),
            Stroke(width = 123, gradient = null)
        )

        for (input in inputs) {
            val canvas = mockt<Canvas>()
            input.draw(canvas, Oval(), mockt())
            verifyNoInteractions(canvas)
        }
    }

    @Test
    fun `draw - noop - shape-alpha`() {
        // when resulting alpha would be 0 -> no draw
        val stroke = Stroke(width = 1, color = 0xFFff0000.toInt())
        val shape = Rectangle().alpha(0F)
        val canvas = mockt<Canvas>()
        stroke.draw(canvas, shape, mockt())

        verifyNoInteractions(canvas)
    }

    @Test
    fun `draw - noop - color-alpha`() {
        // when stroke paint color alpha is 0 -> no draw
    }

    @Test
    fun `draw - color`() {
//        // if color is present, it should draw
//        val input = 0xFF123456.toInt()
//
//        val fill = Shape.Fill(input, null)
//        val shape = mock<Shape> {
//            on { alpha } doReturn null
//        }
//
//        fill.draw(mock(), shape, mock())
//
//        val captor = argumentCaptor<Paint>()
//        verify(shape).drawShape(
//            any(),
//            any(),
//            captor.capture()
//        )
//
//        val paint = captor.value
//
//        assertNull(shape.alpha)
//
//        assertEquals("paint.style", Paint.Style.FILL, paint.style)
//        assertEquals("paint.color", input, paint.color)
//        assertEquals("paint.alpha", 255, paint.alpha)
    }

    @Test
    fun `draw - color - alpha`() {
//        val inputs = listOf(
//            0xFF123456.toInt(),
//            0x80987654.toInt(),
//            0x00ffffff
//        )
//
//        val alphas = listOf(
//            1F,
//            0.5F,
//            0.33F,
//            0.095F,
//            0F
//        )
//
//        for (input in inputs) {
//            for (alpha in alphas) {
//
//                val fill = Shape.Fill(input, null)
//                val shape = mock<Shape> {
//                    on { this.alpha } doReturn alpha
//                }
//
//                fill.draw(mock(), shape, mock())
//
//                val colorAlpha = Color.alpha(input)
//                val shapeAlpha = (colorAlpha * alpha).roundToInt()
//
//                if (shapeAlpha == 0) {
//                    verify(shape, never()).drawShape(any(), any(), any())
//                } else {
//
//                    val captor = argumentCaptor<Paint>()
//                    verify(shape).drawShape(any(), any(), captor.capture())
//
//                    val paint = captor.value
//
//                    // if alpha was applied to paint, it would automatically adjust alpha channel?
//                    assertEquals("paint.style", Paint.Style.FILL, paint.style)
//                    assertEquals(
//                        "paint.color",
//                        input.withAlpha(shapeAlpha).toHexString(),
//                        paint.color.toHexString()
//                    )
//                    assertEquals("paint.alpha", shapeAlpha, paint.alpha)
//                }
//            }
//        }
    }

    @Test
    fun `draw - gradient - alpha`() {
//        val alphas = listOf(
//            0F,
//            0.25F,
//            0.5F,
//            1F
//        )
//
//        for (alpha in alphas) {
//            val shape = mock<Shape> {
//                on { this.alpha } doReturn alpha
//            }
//            val gradient = mock<Gradient>(defaultAnswer = Mockito.RETURNS_MOCKS)
//            val fill = Shape.Fill(null, gradient)
//            fill.draw(mock(), shape, mock())
//
//            if (0F == alpha) {
//                verify(shape, never()).drawShape(any(), any(), any())
//            } else {
//                val captor = argumentCaptor<Paint>()
//                verify(shape).drawShape(any(), any(), captor.capture())
//
//                val paint = captor.value
//                assertEquals(
//                    Shape.defaultFillColor.withAlpha((alpha * 255).roundToInt()).toHexString(),
//                    paint.color.toHexString()
//                )
//                assertEquals((255 * alpha).roundToInt(), paint.alpha)
//
//                verify(gradient).createShader(any())
//                assertNotNull("paint.shader", paint.shader)
//            }
//        }
    }
}