package io.noties.adapt.ui.preview

import android.graphics.Color
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.onViewPreDraw
import io.noties.adapt.ui.util.addOnHierarchyChangeListener
import io.noties.adapt.ui.util.children
import io.noties.adapt.ui.util.isInPreview
import io.noties.adapt.ui.util.onAttachedOnce
import io.noties.adapt.ui.util.renderElement
import kotlin.math.max

class PreviewViewElement<V : View, LP : LayoutParams>(
    previewView: V
) : ViewElement<V, LP>(provider = {
    previewView
})

/**
 * Special configuration for Layout preview.
 *
 * @param allowRenderingOnRealDevice if true will draw preview on real device (otherwise
 * only when in Android Studio Layout preview pane)
 */
fun <V : View, LP : LayoutParams> ViewElement<V, LP>.preview(
    allowRenderingOnRealDevice: Boolean = false,
    block: (PreviewViewElement<V, LP>) -> Unit
) = this.also {
    if (allowRenderingOnRealDevice || isInPreview) {
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

    @Suppress("MoveVariableDeclarationIntoWhen")
    val result = ((view as? PreviewViewCustomization) ?: PreviewViewCustomization).preview(color, view)

    when (result) {
        // NO PREVIEW
        PreviewViewCustomization.NoPreview -> Unit

        // PREVIEW
        is PreviewViewCustomization.Preview -> {
            val d = result.drawable
            view.overlay.add(d)

            // a view can be attached/detached multiple times, allow this and re-init on-attach
            fun render() {
                view.renderElement { el ->
                    el.onViewPreDraw {
                        d.setBounds(0, 0, it.width, it.height)
                    }
                }

                if (result.continueDrawingChildren && applyToChildren && view is ViewGroup) {
                    var current = colorHsvDegree

                    fun render(child: View) {
                        current += 60F
                        previewBounds(child, current, nestedLevel + 1, true)
                    }

                    view.children.forEach {
                        render(it)
                    }

                    view.addOnHierarchyChangeListener(object: OnHierarchyChangeListener {
                        override fun onChildViewAdded(parent: View?, child: View) {
                            render(child)
                        }

                        override fun onChildViewRemoved(parent: View?, child: View) {

                        }
                    })
                }
            }

            view.addOnAttachStateChangeListener(object: OnAttachStateChangeListener {
                // render it again when attached
                override fun onViewAttachedToWindow(v: View) {
                    render()
                }

                // no op on detach
                override fun onViewDetachedFromWindow(v: View) = Unit
            })

            // render immediately if attached already
            if (view.isAttachedToWindow) {
                render()
            }
        }
    }
}