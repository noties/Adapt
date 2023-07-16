package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader
import io.noties.adapt.ui.gradient.GradientEdge.GradientEdgeType
import io.noties.adapt.ui.util.toHexString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Gradient_Test {

    @Test
    fun positionOfEdge() {
        val bounds = Rect(0, 0, 4, 4)

        class Input(
            val type: GradientEdgeType,
            // x, y
            positions: Pair<Int, Int>
        ) {
            val positions = PointF(positions.first.toFloat(), positions.second.toFloat())
        }

        val inputs = GradientEdgeType.values()
            .map {
                val positions = when (it) {
                    GradientEdgeType.Leading -> 0 to 2
                    GradientEdgeType.Top -> 2 to 0
                    GradientEdgeType.Trailing -> 4 to 2
                    GradientEdgeType.Bottom -> 2 to 4
                    GradientEdgeType.TopLeading -> 0 to 0
                    GradientEdgeType.BottomLeading -> 0 to 4
                    GradientEdgeType.TopTrailing -> 4 to 0
                    GradientEdgeType.BottomTrailing -> 4 to 4
                }
                Input(it, positions)
            }

        // validate all inputs are unique
        val unique = inputs
            .map { it.positions }
            .distinct()
        Assert.assertEquals(inputs.size, unique.size)

        for (input in inputs) {
            val result = Gradient.positionOfEdge(GradientEdge(input.type), bounds)
            Assert.assertEquals(input.type.toString(), input.positions, result)
        }
    }

    @Test
    fun positionsOfAngle() {
        // yields the same result as PositionsOfAngle
        val angle = 101F
        val bounds = Rect(0, 0, 10, 20)

        val expected = PositionsOfAngle.positionsOfAngle(angle, bounds)
        val actual = Gradient.positionsOfAngle(angle, bounds)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun colorsAndPositionsToString() {

        val inputs = listOf(
            (intArrayOf(0, 1, 2) to null).let { pair ->
                pair.first.joinToString(", ") { "\"${it.toHexString()}\"" } to pair
            },
            (intArrayOf(3) to floatArrayOf(4F)).let { pair ->
                pair.first.zip(pair.second.toList()).joinToString(", ") {
                    "\"${it.first.toHexString()}\":${it.second}"
                } to pair
            }
        )

        for ((expected, pair) in inputs) {
            Assert.assertEquals(
                expected,
                Gradient.colorsAndPositionsToString(pair.first, pair.second)
            )
        }
    }

    @Test
    fun `createColors - fail`() {
        val inputs = listOf(
            "0" to { Gradient.createColors() },
            "1" to { Gradient.createColors(0) }
        )
        for ((name, input) in inputs) {
            try {
                input.invoke()
                Assert.fail(name)
            } catch (t: Throwable) {
                t.printStackTrace(System.err)
                Assert.assertTrue(
                    "$name, ${t.message}",
                    t.message!!.contains("Minimum 2 colors are required")
                )
            }
        }
    }

    @Test
    fun createColors() {
        val inputs = listOf(
            intArrayOf(1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8, 9)
        )
        for (input in inputs) {
            val colors = Gradient.createColors(*input)
            // not the same - copy
            Assert.assertNotEquals(input, colors)
            Assert.assertArrayEquals(input, colors)
        }
    }

    @Test
    fun `createColorsAndPositions - fail`() {
        val inputs = listOf(
            "0" to { Gradient.createColorsAndPositions() },
            "1" to { Gradient.createColorsAndPositions(0 to 1F) }
        )

        for ((name, input) in inputs) {
            try {
                input.invoke()
                Assert.fail(name)
            } catch (t: Throwable) {
                t.printStackTrace(System.err)
                Assert.assertTrue(
                    "$name, ${t.message}",
                    t.message!!.contains("Minimum 2 colors are required")
                )
            }
        }
    }

    @Test
    fun createColorsAndPositions() {
        val inputs = listOf(
            (0..2).map { it to it / 10F }.toTypedArray(),
            (0..10).map { it to it / 10F }.toTypedArray(),
        )

        for (input in inputs) {
            val (colors, positions) = Gradient.createColorsAndPositions(*input)
            Assert.assertEquals(colors.size, positions.size)
            Assert.assertArrayEquals(
                input.map { it.first }.toIntArray(),
                colors
            )
            Assert.assertArrayEquals(
                input.map { it.second }.toFloatArray(),
                positions,
                0.01F
            )
        }
    }

    @Test
    fun tileMode() {
        val inputs = listOf(null) + Shader.TileMode.values()
        for (input in inputs) {
            val gradient = RadialGradient.center()
                .setColors(7, 8, 9, 10)
                .setTileMode(input)
            Assert.assertEquals(input, gradient.tileMode)
        }
    }
}