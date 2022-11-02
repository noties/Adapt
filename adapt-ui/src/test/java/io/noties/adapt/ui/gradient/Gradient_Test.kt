package io.noties.adapt.ui.gradient

import android.graphics.Rect
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
            val edge: GradientEdge,
            // x, y
            positions: Pair<Int, Int>
        ) {
            val positions = positions.first.toFloat() to positions.second.toFloat()
        }

        val inputs = GradientEdge.values()
            .map {
                val positions = when (it) {
                    GradientEdge.Leading -> 0 to 2
                    GradientEdge.Top -> 2 to 0
                    GradientEdge.Trailing -> 4 to 2
                    GradientEdge.Bottom -> 2 to 4
                    GradientEdge.TopLeading -> 0 to 0
                    GradientEdge.BottomLeading -> 0 to 4
                    GradientEdge.TopTrailing -> 4 to 0
                    GradientEdge.BottomTrailing -> 4 to 4
                }
                Input(it, positions)
            }

        // validate all inputs are unique
        val unique = inputs
            .map { it.positions }
            .distinct()
        Assert.assertEquals(inputs.size, unique.size)

        for (input in inputs) {
            val result = Gradient.positionOfEdge(input.edge, bounds)
            Assert.assertEquals(input.edge.name, input.positions, result)
        }
    }
}