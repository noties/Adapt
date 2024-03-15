package io.noties.adapt.sample.explore

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.util.children
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.RectangleShape

// Renamed, as `preview` might be considered to be used only in actual layout-preview
//  `overlayDrawBounds` (which could be used with `ifPreview { it.overlayDrawBounds() }`
object ExploreOverlayDrawBounds {
    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.overlayDrawBounds() = onView { view ->
        fun process(view: View) {
            PreviewBoundsDrawable(view, Colors.black)
            if (view is ViewGroup) {
                // can we also check the level? so, we could nest wrap children
                view.children.forEach { process(it) }
            }
        }
        process(view)
    }

    private class PreviewBoundsDrawable(
        val view: View,
        @ColorInt val color: Int
    ) {

        private val drawable = RectangleShape {
            stroke(color, 1)
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