package io.noties.adapt.ui.gradient

import android.graphics.Rect
import android.graphics.Shader.TileMode
import androidx.annotation.ColorInt
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Config.TARGET_SDK],
    shadows = [LinearGradient_Test.ShadowLinearGradient::class]
)
class LinearGradient_Test {

    @get:Rule
    val testName = TestName()

    @Test
    fun `builder - colors2`() {
        val inputs = listOf(
            "edges" to { LinearGradient.edges { top.leading to bottom } },
            "angle" to { LinearGradient.angle(67F) }
        )

        val colors = intArrayOf(1, -2)

        for ((name, builder) in inputs) {
            val gradient = builder()
                .setColors(colors[0], colors[1])
            printGradient(gradient)
            Assert.assertArrayEquals(name, colors, gradient.colors)
        }
    }

    @Test
    fun `builder - colors vararg`() {
        // required number

        val inputs = listOf(
            "edges" to { LinearGradient.edges { top.leading to bottom } },
            "angle" to { LinearGradient.angle(67F) }
        )

        for ((name, builder) in inputs) {
            try {
                @Suppress("UNUSED_VARIABLE")
                val g = builder()
                    .setColors(0)
                Assert.fail(name)
            } catch (t: Throwable) {
                printThrowable(t)
                Assert.assertTrue(
                    "$name, ${t.message}",
                    t.message!!.contains("Minimum 2 colors are required")
                )
            }

            val colors = (0 until 10).map { it }.toIntArray()
            val gradient = builder()
                .setColors(*colors)
            printGradient(gradient)
            Assert.assertArrayEquals(name, colors, gradient.colors)
        }
    }

    @Test
    fun `builder - colors and positions`() {
        // required number

        val inputs = listOf(
            "edges" to { LinearGradient.edges { top.leading to bottom } },
            "angle" to { LinearGradient.angle(67F) }
        )

        for ((name, builder) in inputs) {
            try {
                @Suppress("UNUSED_VARIABLE")
                val g = builder()
                    .setColors(0 to 0F)
                Assert.fail(name)
            } catch (t: Throwable) {
                printThrowable(t)
                Assert.assertTrue(
                    "$name, ${t.message}",
                    t.message!!.contains("Minimum 2 colors are required")
                )
            }

            val colorsAndPositions = (0 until 10).map { it to it.toFloat() }.toTypedArray()
            val gradient = builder()
                .setColors(*colorsAndPositions)
            printGradient(gradient)
            Assert.assertArrayEquals(
                name,
                colorsAndPositions.map { it.first }.toIntArray(),
                gradient.colors
            )
            Assert.assertArrayEquals(
                name,
                colorsAndPositions.map { it.second }.toFloatArray(),
                gradient.positions,
                0.001F
            )
        }
    }

    @Test
    fun `init - tileMode`() {
        val inputs = listOf(
            "edges" to { LinearGradient.edges { top.leading to bottom } },
            "angle" to { LinearGradient.angle(67F) }
        )
        val modes = listOf(null) + TileMode.values()

        for ((name, builder) in inputs) {
            for (mode in modes) {
                val gradient = builder()
                    .setColors(1, 2)
                    .setTileMode(mode)
                printGradient(gradient)
                // clamp is default, but it would be used on created shader (not before)
                Assert.assertEquals(name, mode, gradient.tileMode)
            }
        }
    }

    @Test
    fun `init - type - edges`() {
        val gradient = LinearGradient.edges { top to trailing }
            .setColors(1, 66)
        Assert.assertEquals(
            LinearGradient.Edges(GradientEdge.top to GradientEdge.trailing),
            gradient.type
        )
    }

    @Test
    fun `init - type - angle`() {
        val gradient = LinearGradient.angle(87F)
            .setColors(9897, 1)
        Assert.assertEquals(
            LinearGradient.Angle(87F),
            gradient.type
        )
    }

    @Test
    fun `createShader - edges`() {
        val edges = GradientEdge.leading.bottom to GradientEdge.trailing.top
        val gradient = LinearGradient.edges { edges }
            .setColors(3, 4, 5)
        val bounds = Rect(0, 0, 100, 150)
        val shadow = createShaderShadow(gradient, bounds)

        val start = Gradient.positionOfEdge(edges.first, bounds)
        val end = Gradient.positionOfEdge(edges.second, bounds)

        Assert.assertEquals(start.x, shadow.x0)
        Assert.assertEquals(start.y, shadow.y0)
        Assert.assertEquals(end.x, shadow.x1)
        Assert.assertEquals(end.y, shadow.y1)
    }

    @Test
    fun `createShader - angle`() {
        val angle = 99F
        val gradient = LinearGradient.angle(angle)
            .setColors(7, 8, 9, 10)
        val bounds = Rect(0, 0, 99, 333)
        val (start, end) = Gradient.positionsOfAngle(angle, bounds)
        val shadow = createShaderShadow(gradient, bounds)

        Assert.assertEquals(start.x, shadow.x0)
        Assert.assertEquals(start.y, shadow.y0)
        Assert.assertEquals(end.x, shadow.x1)
        Assert.assertEquals(end.y, shadow.y1)
    }

    @Test
    fun `createShader - colors`() {
        val inputs = listOf(
            intArrayOf(1, 2) to null,
            intArrayOf(3, 4, 5) to floatArrayOf(0F, 0.42F, 1F)
        )
        for ((colors, positions) in inputs) {
            val gradient = LinearGradient.angle(98F)
                .let {
                    if (positions != null) {
                        it.setColors(
                            *colors.zip(positions.toList()).toTypedArray()
                        )
                    } else {
                        it.setColors(*colors)
                    }
                }
            Assert.assertArrayEquals(colors, gradient.colors)
            Assert.assertArrayEquals(positions, gradient.positions, 0.01F)

            val shadow = createShaderShadow(gradient, Rect(0, 0, 100, 100))
            Assert.assertArrayEquals(colors, shadow.colors)
            Assert.assertArrayEquals(positions, shadow.positions, 0.01F)
        }
    }

    @Test
    fun `createShader - tileMode`() {
        // if null, then CLAMP is used by default
        val inputs = listOf(null) + TileMode.values()
        for (input in inputs) {
            val gradient = LinearGradient.angle(7F)
                .setColors(1, 7)
                .setTileMode(input)
            Assert.assertEquals(input, gradient.tileMode)
            val shader = createShaderShadow(gradient, Rect(0, 0, 100, 100))
            Assert.assertEquals(input ?: TileMode.CLAMP, shader.tileMode)
        }
    }

    private fun createShaderShadow(gradient: LinearGradient, bounds: Rect = Rect()) =
        ShadowLinearGradient.extract(
            gradient.createShader(bounds) as android.graphics.LinearGradient
        )

    private fun printGradient(gradient: LinearGradient) {
        println("`${testName.methodName}`:$gradient")
    }

    private fun printThrowable(throwable: Throwable) {
        println("`${testName.methodName}`:\"${throwable.message}\"")
    }

    @Implements(android.graphics.LinearGradient::class)
    class ShadowLinearGradient {

        companion object {
            fun extract(actual: Any): ShadowLinearGradient {
                return Shadow.extract(actual) as ShadowLinearGradient
            }
        }

        var x0: Float? = null
        var y0: Float? = null
        var x1: Float? = null
        var y1: Float? = null

        var colors: IntArray? = null
        var positions: FloatArray? = null

        var tileMode: TileMode? = null

        @Suppress("TestFunctionName", "unused")
        @Implementation
        fun __constructor__(
            x0: Float,
            y0: Float,
            x1: Float,
            y1: Float,
            @ColorInt colors: IntArray,
            positions: FloatArray?,
            tileMode: TileMode
        ) {
            this.x0 = x0
            this.y0 = y0
            this.x1 = x1
            this.y1 = y1
            this.colors = colors
            this.positions = positions
            this.tileMode = tileMode
        }
    }
}