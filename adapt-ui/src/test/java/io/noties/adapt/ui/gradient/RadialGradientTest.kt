package io.noties.adapt.ui.gradient

import android.content.res.Resources
import android.graphics.Rect
import android.graphics.Shader
import io.noties.adapt.ui.shape.Dimension
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import kotlin.math.min
import kotlin.math.roundToInt

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Config.TARGET_SDK],
    shadows = [RadialGradientTest.ShadowRadialGradient::class]
)
class RadialGradientTest {

    @Test
    fun `builder - center`() {
        val gradient = RadialGradient.center()
            .setColors(1, 2)
        Assert.assertEquals(RadialGradient.Edge(null), gradient.type)
    }

    @Test
    fun `builder - edge`() {
        val inputs = GradientEdge.GradientEdgeType.values()
        for (input in inputs) {
            val edge = GradientEdge(input)
            val gradient = RadialGradient.edge(edge)
                .setColors(2, 3)
            Assert.assertEquals(RadialGradient.Edge(edge), gradient.type)
        }
    }

    @Test
    fun `builder - angle`() {
        val angle = 359.99F
        val gradient = RadialGradient.angle(angle)
            .setColors(4, 5, 6)
        Assert.assertEquals(RadialGradient.Angle(angle), gradient.type)
    }

    @Test
    fun `init - colors2`() {
        val (start, end) = 0 to 1

        fun assert(builder: RadialGradient.Builder) {
            val gradient = builder.setColors(start, end)
            Assert.assertArrayEquals(
                intArrayOf(start, end),
                gradient.colors
            )
            Assert.assertNull(gradient.positions)
            Assert.assertNull(gradient.tileMode)
            Assert.assertNull(gradient.radius)
        }

        assert(RadialGradient.center())
        assert(RadialGradient.edge(GradientEdge.top))
        assert(RadialGradient.angle(42F))
    }

    @Test
    fun `init - colors vararg`() {
        val input = intArrayOf(0, 1, 2, 3, 4)

        fun assert(builder: RadialGradient.Builder) {
            val gradient = builder.setColors(*input)
            Assert.assertArrayEquals(
                input,
                gradient.colors
            )
            Assert.assertNull(gradient.positions)
            Assert.assertNull(gradient.tileMode)
            Assert.assertNull(gradient.radius)
        }

        assert(RadialGradient.center())
        assert(RadialGradient.edge(GradientEdge.bottom))
        assert(RadialGradient.angle(1F))
    }

    @Test
    fun `init - colors and positions`() {
        val input = (1..5).map { it to it / 5F }.toTypedArray()

        fun assert(builder: RadialGradient.Builder) {
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
            Assert.assertNull(gradient.tileMode)
            Assert.assertNull(gradient.radius)
        }

        assert(RadialGradient.center())
        assert(RadialGradient.edge(GradientEdge.bottom))
        assert(RadialGradient.angle(1F))
    }

    @Test
    fun `radius default`() {
        // takes min dimension by default
        val inputs = listOf(
            1 to Rect(0, 0, 1, 999),
            2 to Rect(1, 1, 3, 4),
            3 to Rect(3, 3, 9, 6)
        )

        for ((expected, rect) in inputs) {
            Assert.assertEquals(
                rect.toShortString(),
                expected,
                RadialGradient.radius(rect)
            )
        }
    }

    @Test
    fun `init - radius - default`() {
        // by default is null
        val gradient = RadialGradient.center()
            .setColors(0, 0)
        Assert.assertNull(gradient.radius)
    }

    @Test
    fun `init - radius - exact`() {
        Assert.assertEquals(1F, Resources.getSystem().displayMetrics.density)

        val radius = 42
        val gradient = RadialGradient.center()
            .setColors(0, 0)
            .setRadius(radius)

        Assert.assertEquals(Dimension.Exact(radius), gradient.radius)
    }

    @Test
    fun `init - radius - relative`() {
        val radius = 0.42F
        val gradient = RadialGradient.center()
            .setColors(0, 0)
            .setRadiusRelative(radius)

        Assert.assertEquals(Dimension.Relative(radius), gradient.radius)
    }

    @Test
    fun `createShader - center`() {
        // center is used as the point

        val colors = intArrayOf(0, 1)
        val bounds = Rect(0, 0, 99, 999)
        val gradient = RadialGradient.center()
            .setColors(*colors)
        val shadow = createShaderShadow(gradient, bounds)

        Assert.assertEquals(bounds.centerX().toFloat(), shadow.centerX)
        Assert.assertEquals(bounds.centerY().toFloat(), shadow.centerY)
        Assert.assertEquals(
            RadialGradient.radius(bounds).toFloat(),
            shadow.radius
        )
        Assert.assertArrayEquals(
            colors,
            shadow.colors
        )
        Assert.assertNull(shadow.stops)
        Assert.assertEquals(Shader.TileMode.CLAMP, shadow.tileMode)
    }

    @Test
    fun `createShader - edge`() {
        Assert.assertEquals(1F, Resources.getSystem().displayMetrics.density)

        val tileMode = Shader.TileMode.MIRROR
        val (colors, positions) = (0..4)
            .map { it }
            .toIntArray()
            .let {
                it to it.map { it / 4F }.toFloatArray()
            }

        val radius = 42
        val bounds = Rect(1, 1, 99, 199)
        val edge = GradientEdge.bottom.leading
        val gradient = RadialGradient.edge(edge)
            .setColors(*colors.zip(positions.toList()).toTypedArray())
            .setTileMode(tileMode)
            .setRadius(radius)
        val shadow = createShaderShadow(gradient, bounds)

        val point = Gradient.positionOfEdge(edge, bounds)

        Assert.assertEquals(point.x, shadow.centerX)
        Assert.assertEquals(point.y, shadow.centerY)
        Assert.assertEquals(radius.toFloat(), shadow.radius)
        Assert.assertArrayEquals(
            colors,
            shadow.colors
        )
        Assert.assertArrayEquals(
            positions,
            shadow.stops,
            0.01F
        )
        Assert.assertEquals(tileMode, shadow.tileMode)
    }

    @Test
    fun `createShader - angle`() {
        val angle = 42F
        val (start, end) = 99 to -1
        val bounds = Rect(2, 3, 4, 5)
        val radiusRelative = 0.42F

        val gradient = RadialGradient.angle(angle)
            .setColors(start, end)
            .setRadiusRelative(radiusRelative)

        val (point, _) = Gradient.positionsOfAngle(angle, bounds)
        val shadow = createShaderShadow(gradient, bounds)

        Assert.assertEquals(point.x, shadow.centerX)
        Assert.assertEquals(point.y, shadow.centerY)

        val expectedRadius = (min(bounds.width(), bounds.height()) * radiusRelative).roundToInt().toFloat()
        Assert.assertEquals(expectedRadius, shadow.radius)
        Assert.assertArrayEquals(
            intArrayOf(start, end),
            shadow.colors
        )
        Assert.assertNull(shadow.stops)
        Assert.assertEquals(Shader.TileMode.CLAMP, shadow.tileMode)
    }

    private fun createShaderShadow(gradient: RadialGradient, bounds: Rect): ShadowRadialGradient {
        val shader = gradient.createShader(bounds)
        return Shadow.extract(shader) as ShadowRadialGradient
    }

    @Implements(android.graphics.RadialGradient::class)
    class ShadowRadialGradient {

        var centerX: Float? = null
        var centerY: Float? = null
        var radius: Float? = null
        var colors: IntArray? = null
        var stops: FloatArray? = null
        var tileMode: Shader.TileMode? = null

        @Suppress("unused", "TestFunctionName")
        @Implementation
        fun __constructor__(
            centerX: Float,
            centerY: Float,
            radius: Float,
            colors: IntArray,
            stops: FloatArray?,
            tileMode: Shader.TileMode
        ) {
            this.centerX = centerX
            this.centerY = centerY
            this.radius = radius
            this.colors = colors
            this.stops = stops
            this.tileMode = tileMode
        }
    }
}