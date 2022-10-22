package io.noties.adapt.ui.shape

import android.content.res.Resources
import android.graphics.Rect
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ShapePadding_Test {

    @Test
    fun empty() {
        val inputs = listOf(
            Rect(0, 0, 100, 250),
            Rect(100, 200, 300, 400),
            Rect(0, 0, 0, 0),
            Rect(-1, -1, -1, -1)
        )
        val padding = Shape.Padding()
        for (input in inputs) {
            // create copy
            val out = padding.let {
                val rect = input.copy()
                it.copy { it.set(rect) }
                rect
            }
            Assert.assertEquals(
                "$padding, ${input.toShortString()}",
                input,
                out
            )
        }
    }

    @Test
    fun values() {
        // verify density value
        val density = Resources.getSystem().displayMetrics.density
        Assert.assertEquals(1F, density)

        val rect = Rect(8, 16, 32, 64)
        Assert.assertEquals(rect, rect.copy())

        val w = rect.width()
        val h = rect.height()

        val inputs = listOf(
            // non altered, the same
            Shape.Padding() to rect.copy(),

            // zeros exact
            Shape.Padding(
                Dimension.Exact(0),
                Dimension.Exact(0),
                Dimension.Exact(0),
                Dimension.Exact(0),
            ) to rect.copy(),

            // zeros relative
            Shape.Padding(
                Dimension.Relative(0F),
                Dimension.Relative(0F),
                Dimension.Relative(0F),
                Dimension.Relative(0F),
            ) to rect.copy(),

            Shape.Padding(
                Dimension.Exact(2),
                Dimension.Relative(0.5F),
                Dimension.Exact(12),
                Dimension.Relative(0.25F)
            ) to rect.copy {
                left += 2
                top += (h * 0.5F).roundToInt()
                right -= 12
                bottom -= (h * 0.25F).roundToInt()
            },

            Shape.Padding(
                Dimension.Relative(0.75F),
                Dimension.Exact(4),
                Dimension.Relative(0.1F),
                Dimension.Exact(32)
            ) to rect.copy {
                left += (w * 0.75F).roundToInt()
                top += 4
                right -= (w * 0.1F).roundToInt()
                bottom -= 32
            }
        )

        for ((padding, bounds) in inputs) {
            val out = padding.copy().let {
                val r = rect.copy()
                it.set(r)
                r
            }
            Assert.assertEquals(
                "$padding, ${rect.toShortString()} - ${out.toShortString()}",
                bounds,
                out
            )
        }
    }

    @Test
    fun copy() {
        val padding = Shape.Padding(
            Dimension.Exact(666),
            Dimension.Relative(0.5F),
            Dimension.Relative(0.25F),
            Dimension.Exact(777)
        )

        // just copy
        Assert.assertEquals(
            padding.toString(),
            padding,
            padding.copy()
        )

        // copy with block
        Assert.assertEquals(
            padding.toString(),
            Shape.Padding(
                null,
                Dimension.Relative(0.5F),
                Dimension.Relative(0.25F),
                Dimension.Exact(777)
            ),
            padding.copy {
                leading = null
            }
        )
    }

    private fun Rect.copy(block: Rect.() -> Unit = {}): Rect = Rect(this).also(block)
}