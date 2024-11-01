package io.noties.adapt.ui.preview

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.onViewPreDraw
import io.noties.adapt.ui.util.children
import io.noties.adapt.ui.util.element
import io.noties.adapt.ui.util.isInPreview
import io.noties.adapt.ui.util.renderElement
import kotlin.math.max

class PreviewViewElement<V : View, LP : LayoutParams>(
    previewView: V
) : ViewElement<V, LP>(provider = {
    previewView
})

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.preview(
    block: (PreviewViewElement<V, LP>) -> Unit
) = this.also {
    if (isInPreview) {
        // hm, do we need to add it to the factory? it would be rendered otherwise
        //  would not receive a callback
        it.onView { view ->
            val element = PreviewViewElement<V, LP>(view).also { el -> el.init(view.context) }
            block(element)
            element.render()
        }
    }
}

fun <V : View, LP : LayoutParams> PreviewViewElement<V, LP>.previewBounds(
    applyToChildren: Boolean = true
) = onView { view ->
    previewBounds(view, 0F, 0, applyToChildren)
}

private fun previewBounds(
    view: View,
    colorHsvDegree: Float,
    nestedLevel: Int,
    applyToChildren: Boolean
) {

    val array = floatArrayOf(colorHsvDegree % 360F, max(0.1F, 1F - (nestedLevel * 0.1F)), 1F)
    val color = Color.HSVToColor(array)

    val drawable = ((view as? PreviewViewCustomization) ?: PreviewViewCustomization).preview(color, view)

    view.overlay.add(drawable)

    view.renderElement { el ->
        el.onViewPreDraw {
            drawable.setBounds(0, 0, it.width, it.height)
        }
    }

    if (applyToChildren && view is ViewGroup) {
        var current = colorHsvDegree
        view.children.forEach {
            current += 60F
            previewBounds(it, current, nestedLevel + 1, true)
        }
    }
}