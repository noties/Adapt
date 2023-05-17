package io.noties.adapt.ui.gradient

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader

abstract class Gradient {
    abstract fun createShader(bounds: Rect): Shader

    companion object {
        fun positionOfEdge(edge: GradientEdge, bounds: Rect): PointF {
            val pair: Pair<Int, Int> = when (edge.type) {
                GradientEdge.GradientEdgeType.Leading -> bounds.left to bounds.centerY()
                GradientEdge.GradientEdgeType.TopLeading -> bounds.left to bounds.top
                GradientEdge.GradientEdgeType.Top -> bounds.centerX() to bounds.top
                GradientEdge.GradientEdgeType.TopTrailing -> bounds.right to bounds.top
                GradientEdge.GradientEdgeType.Trailing -> bounds.right to bounds.centerY()
                GradientEdge.GradientEdgeType.BottomLeading -> bounds.left to bounds.bottom
                GradientEdge.GradientEdgeType.Bottom -> bounds.centerX() to bounds.bottom
                GradientEdge.GradientEdgeType.BottomTrailing -> bounds.right to bounds.bottom
            }
            return PointF(pair.first.toFloat(), pair.second.toFloat())
        }

        fun positionsOfAngle(angle: Float, bounds: Rect): Pair<PointF, PointF> {
            return PositionsOfAngle.positionsOfAngle(angle, bounds)
        }
    }
}