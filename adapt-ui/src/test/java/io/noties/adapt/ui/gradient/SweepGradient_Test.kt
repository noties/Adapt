package io.noties.adapt.ui.gradient

import android.graphics.Rect
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Config.TARGET_SDK],
    shadows = [SweepGradient_Test.ShadowSweepGradient::class]
)
class SweepGradient_Test {

    @Test
    fun `builder - center`() {
        val gradient = SweepGradient.center()
            .setColors(0, 0)
        Assert.assertEquals(
            SweepGradient.Edge(null),
            gradient.type
        )
    }

    @Test
    fun `builder - edge`() {
        val inputs = GradientEdge.GradientEdgeType.values()
        for (input in inputs) {
            val edge = GradientEdge(input)
            val gradient = SweepGradient.edge(edge)
                .setColors(0, 0)
            Assert.assertEquals(
                input.name,
                SweepGradient.Edge(edge),
                gradient.type
            )
        }
    }

    @Test
    fun `builder - angle`() {
        val angle = 42F
        val gradient = SweepGradient.angle(angle)
            .setColors(0, 0)
        Assert.assertEquals(
            SweepGradient.Angle(angle),
            gradient.type
        )
    }

    @Test
    fun `init - colors2`() {
        val (start, end) = 1 to 2

        fun assert(builder: SweepGradient.Builder) {
            val gradient = builder.setColors(start, end)
            Assert.assertArrayEquals(
                intArrayOf(start, end),
                gradient.colors
            )
            Assert.assertNull(gradient.positions)
        }

        assert(SweepGradient.center())
        assert(SweepGradient.edge(GradientEdge.top))
        assert(SweepGradient.angle(42F))
    }

    @Test
    fun `init - colors vararg`() {
        val input = intArrayOf(0, 1, 2, 3, 4)

        fun assert(builder: SweepGradient.Builder) {
            val gradient = builder.setColors(*input)
            Assert.assertArrayEquals(
                input,
                gradient.colors
            )
            Assert.assertNull(gradient.positions)
        }

        assert(SweepGradient.center())
        assert(SweepGradient.edge(GradientEdge.bottom))
        assert(SweepGradient.angle(1F))
    }

    @Test
    fun `init - colors and positions`() {
        val input = (1..5).map { it to it / 5F }.toTypedArray()

        fun assert(builder: SweepGradient.Builder) {
            val gradient = builder.setColors(*input)
            Assert.assertArrayEquals(
                input.map { it.first }.toIntArray(),
                gradient.colors
            )
            Assert.assertArrayEquals(
                input.map { it.second }.toFloatArray(),
                gradient.positions,
                0.01F
            )
        }

        assert(SweepGradient.center())
        assert(SweepGradient.edge(GradientEdge.bottom))
        assert(SweepGradient.angle(1F))
    }

    @Test
    fun `createShader - center`() {
        val (start, end) = 1 to 10
        val bounds = Rect(0, 0, 10, 22)
        val gradient = SweepGradient.center()
            .setColors(start, end)

        val (centerX, centerY) = bounds.centerX().toFloat() to bounds.centerY().toFloat()

        val shadow = createShadow(gradient, bounds)
        Assert.assertEquals(centerX, shadow.centerX)
        Assert.assertEquals(centerY, shadow.centerY)
        Assert.assertArrayEquals(
            intArrayOf(start, end),
            shadow.colors
        )
        Assert.assertNull(shadow.positions)
    }

    @Test
    fun `createShader - edge`() {
        val colors = intArrayOf(1, 2, 3)
        val bounds = Rect(10, 20, 30, 40)
        val edge = GradientEdge.trailing.bottom
        val gradient = SweepGradient.edge(edge)
            .setColors(*colors)

        val center = Gradient.positionOfEdge(edge, bounds)

        val shadow = createShadow(gradient, bounds)
        Assert.assertEquals(center.x, shadow.centerX)
        Assert.assertEquals(center.y, shadow.centerY)
        Assert.assertArrayEquals(colors, shadow.colors)
        Assert.assertNull(shadow.positions)
    }

    @Test
    fun `createShader - angle`() {
        val colorsAndPositions = (0..9)
            .map { it to it / 9F }
            .toTypedArray()

        val bounds = Rect(22, 33, 44, 55)
        val angle = 77F
        val gradient = SweepGradient.angle(angle)
            .setColors(*colorsAndPositions)

        val (center, _) = Gradient.positionsOfAngle(angle, bounds)

        val shadow = createShadow(gradient, bounds)
        Assert.assertEquals(center.x, shadow.centerX)
        Assert.assertEquals(center.y, shadow.centerY)
        Assert.assertArrayEquals(
            colorsAndPositions.map { it.first }.toIntArray(),
            shadow.colors
        )
        Assert.assertArrayEquals(
            colorsAndPositions.map { it.second }.toFloatArray(),
            shadow.positions,
            0.01F
        )
    }

    private fun createShadow(gradient: SweepGradient, bounds: Rect): ShadowSweepGradient {
        val shader = gradient.createShader(bounds)
        return Shadow.extract(shader) as ShadowSweepGradient
    }

    @Implements(android.graphics.SweepGradient::class)
    class ShadowSweepGradient {

        var centerX: Float? = null
        var centerY: Float? = null
        var colors: IntArray? = null
        var positions: FloatArray? = null

        @Suppress("TestFunctionName", "unused")
        fun __constructor__(
            centerX: Float,
            centerY: Float,
            colors: IntArray,
            positions: FloatArray?
        ) {
            this.centerX = centerX
            this.centerY = centerY
            this.colors = colors
            this.positions = positions
        }
    }
}