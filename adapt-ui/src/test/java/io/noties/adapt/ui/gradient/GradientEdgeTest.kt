package io.noties.adapt.ui.gradient

import io.noties.adapt.ui.gradient.GradientEdge.GradientEdgeType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class GradientEdgeTest {

    @Test
    fun values() {
        val inputs = listOf(
            GradientEdgeType.Leading to GradientEdge.leading,
            GradientEdgeType.TopLeading to GradientEdge.leading.top,
            GradientEdgeType.TopLeading to GradientEdge.top.leading,
            GradientEdgeType.Top to GradientEdge.top,
            GradientEdgeType.TopTrailing to GradientEdge.top.trailing,
            GradientEdgeType.TopTrailing to GradientEdge.trailing.top,
            GradientEdgeType.Trailing to GradientEdge.trailing,
            GradientEdgeType.BottomTrailing to GradientEdge.trailing.bottom,
            GradientEdgeType.BottomTrailing to GradientEdge.bottom.trailing,
            GradientEdgeType.Bottom to GradientEdge.bottom,
            GradientEdgeType.BottomLeading to GradientEdge.bottom.leading,
            GradientEdgeType.BottomLeading to GradientEdge.leading.bottom,
        )

        for ((type, edge) in inputs) {
            Assert.assertEquals(type, edge.type)
            Assert.assertEquals(GradientEdge(type), edge)
        }
    }

    @Test
    fun `equals - hashcode`() {
        val types = GradientEdgeType.values()
        for (type in types) {
            Assert.assertEquals(GradientEdge(type), GradientEdge(type))
            Assert.assertEquals(GradientEdge(type).hashCode(), GradientEdge(type).hashCode())
        }
    }
}