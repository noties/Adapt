package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import io.noties.adapt.ui.gradient.GradientEdge.GradientEdgeType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class GradientTest {

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
}