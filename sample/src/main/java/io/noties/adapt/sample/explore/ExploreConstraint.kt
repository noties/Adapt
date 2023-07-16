package io.noties.adapt.sample.explore

import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.constraintlayout.widget.ConstraintLayout
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.ElementGroup

object ExploreConstraint {
    @Suppress("FunctionName")
    fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Constraint(
        children: ViewFactory<ConstraintLayout.LayoutParams>.() -> Unit
    ): ViewElement<ConstraintLayout, LP> = ElementGroup(
        { ConstraintLayout(it) },
        children
    )

/*
setMinWidth
setMinHeight
setMaxWidth
setMaxHeight

setOptimizationLevel

setConstraintSet

setOnConstraintsChanged
*/

/*
top => to topOf, bottomOf
left = to left, to right
*/

    interface ConstraintBuilder {
        // what we want
        // top.equalParent()
        // leading.top.trailing.bottom.equalParent() == edges.matchParent()
        // top.match(element.top), how to limit to top and bottom?
        // top.match(element) == top.match(element.top)
        // top.bottom.match(element) // a list, cannot specify nor top, nor bottom only element (or parent)

        // `match` is a bad name, it does not make it fill the bounds by default, only constraints inside
        //  given edges

        // leading.trailing.constraint(element).bias(0.5F) -> bias is automatically horizontal
        // top.bottom.constraintParent().bias(0.25F) -> bias is automatically vertical

//    val top

        // helpers to specify layout also here

        // 0x0
        fun layoutMatchConstraints()

        // this should be the default?
        fun layoutWrap()
        fun layoutFill()
        fun layout(width: Int? = null, height: Int? = null)

        // layout margins?
        fun layoutMargins(all: Int)
        fun layoutMargins(horizontal: Int? = null, vertical: Int? = null)
        fun layoutMargins(
            leading: Int? = null,
            top: Int? = null,
            trailing: Int? = null,
            bottom: Int? = null
        )

        @FloatRange(from = 0.0, to = 1.0)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Bias

        fun bias(@Bias all: Float)
        fun bias(@Bias horizontal: Float? = null, @Bias vertical: Float? = null)

        interface ConstraintTarget {
            fun constraintParent(): ConstraintCustomization
            fun constraint(element: ViewElement<*, *>): ConstraintCustomization
        }

        interface ConstraintTargetEdge {
            fun constraint(edge: ElementEdge)
        }

        interface ConstraintEdgeMultiple : ConstraintTarget {
            val leading: ConstraintEdgeMultiple
            val top: ConstraintEdgeMultiple
            val trailing: ConstraintEdgeMultiple
            val bottom: ConstraintEdgeMultiple
        }

        interface ConstraintEdge : ConstraintTarget, ConstraintTargetEdge {
            val leading: ConstraintEdgeMultiple
            val top: ConstraintEdgeMultiple
            val trailing: ConstraintEdgeMultiple
            val bottom: ConstraintEdgeMultiple
        }

        interface ConstraintTargetHorizontal : ConstraintTarget {

        }

        // possible targets: edge.leading, edge.trailing, parent, element
        val leading: ConstraintEdge

        val top: ConstraintEdge
        val trailing: ConstraintEdge
        val bottom: ConstraintEdge

        interface ConstraintAxis : ConstraintTarget {
            val horizontal: ConstraintAxis
            val vertical: ConstraintAxis
        }

        interface ConstraintEdges {
            val edges: ConstraintTarget

            // keeps size (do not change it) and sets constraints
            val center: ConstraintTarget
            val baseline: ConstraintTarget
        }

        interface ConstraintSize : ConstraintTarget {
            // sets layout_width=0
            val width: ConstraintSize

            // sets layout_height=0
            val height: ConstraintSize
        }

        interface ConstraintCustomization {
            fun offset(offset: Int)
        }

        interface ElementEdge

        val <V : View, LP : ConstraintLayout.LayoutParams> ViewElement<V, LP>.leading: ElementEdge
        val <V : View, LP : ConstraintLayout.LayoutParams> ViewElement<V, LP>.top: ElementEdge
        val <V : View, LP : ConstraintLayout.LayoutParams> ViewElement<V, LP>.trailing: ElementEdge
        val <V : View, LP : ConstraintLayout.LayoutParams> ViewElement<V, LP>.bottom: ElementEdge

        // links to other baseline,top,bottom
        val <V : View, LP : ConstraintLayout.LayoutParams> ViewElement<V, LP>.baseline: ElementEdge
    }

    fun <V : View, LP : ConstraintLayout.LayoutParams> ViewElement<V, LP>.layoutConstraints(
        builder: ConstraintBuilder.() -> Unit
    ) = Unit

    fun hey() {
        fun func(block: ConstraintBuilder.() -> Unit) = Unit

        val el = ViewElement<View, ConstraintLayout.LayoutParams> { View(it) }

        func {
            leading.constraint(el.trailing)

        }
    }
}