package io.noties.adapt.ui.gradient

open class GradientEdge(val type: GradientEdgeType) {
    enum class GradientEdgeType {
        Leading,
        Top,
        Trailing,
        Bottom,
        TopLeading,
        BottomLeading,
        TopTrailing,
        BottomTrailing
    }

    companion object {
        val leading = HorizontalGradientEdge(
            GradientEdgeType.Leading,
            GradientEdgeType.TopLeading,
            GradientEdgeType.BottomLeading
        )
        val top = VerticalGradientEdge(
            GradientEdgeType.Top,
            GradientEdgeType.TopLeading,
            GradientEdgeType.TopTrailing
        )
        val trailing = HorizontalGradientEdge(
            GradientEdgeType.Trailing,
            GradientEdgeType.TopTrailing,
            GradientEdgeType.BottomTrailing
        )
        val bottom = VerticalGradientEdge(
            GradientEdgeType.Bottom,
            GradientEdgeType.BottomLeading,
            GradientEdgeType.BottomTrailing
        )

        class HorizontalGradientEdge(
            type: GradientEdgeType,
            topType: GradientEdgeType,
            bottomType: GradientEdgeType
        ) : GradientEdge(type) {
            val top = GradientEdge(topType)
            val bottom = GradientEdge(bottomType)
        }

        class VerticalGradientEdge(
            type: GradientEdgeType,
            leadingType: GradientEdgeType,
            trailingType: GradientEdgeType
        ) : GradientEdge(type) {
            val leading = GradientEdge(leadingType)
            val trailing = GradientEdge(trailingType)
        }
    }

    override fun toString(): String {
        return "GradientEdge(type=$type)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GradientEdge) return false

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}