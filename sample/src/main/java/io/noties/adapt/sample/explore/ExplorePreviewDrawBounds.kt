package io.noties.adapt.sample.explore

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.sample.util.children
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape

object ExplorePreviewDrawBounds {
    // TODO: this can be an extension for all views
    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.previewDrawBounds() = onView { view ->
        fun process(view: View) {
            PreviewBoundsDrawable(view, Colors.black)
            if (view is ViewGroup) {
                view.children.forEach { process(it) }
            }
        }
        process(view)
    }

    private class PreviewBoundsDrawable(
//        val root: ViewGroup,
        val view: View,
        @ColorInt val color: Int
    ) {

        private val drawable = RectangleShape {
//            padding(1)
            stroke(color, 1)
//            Rectangle { stroke(color, 1) }
            Label(view::class.java.simpleName)
                .textSize(11)
                .textColor(color)
        }.newDrawable()

        init {
            view.overlay.add(drawable)
            view.viewTreeObserver
                .takeIf { it.isAlive }
                ?.addOnPreDrawListener {
                    drawable.setBounds(0, 0, view.width, view.height)
                    true
                }
        }
    }
}