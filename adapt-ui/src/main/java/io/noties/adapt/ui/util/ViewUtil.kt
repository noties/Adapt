package io.noties.adapt.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement

fun <V : View> V.onPreDrawOnce(block: (V) -> Unit) {
    val view = this
    view.viewTreeObserver
        .takeIf { it.isAlive }
        ?.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val vto = view.viewTreeObserver
                if (vto.isAlive) {
                    block(view)
                    vto.removeOnPreDrawListener(this)
                }
                return true
            }
        })
    view.invalidate()
}

fun <V : View> V.onAttachedOnce(block: (V) -> Unit) {
    val view = this
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            block(view)
            view.removeOnAttachStateChangeListener(this)
        }

        override fun onViewDetachedFromWindow(v: View) = Unit
    })
}

fun <V : View> V.onDetachedOnce(block: (V) -> Unit) {
    val view = this
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = Unit
        override fun onViewDetachedFromWindow(v: View) {
            block(view)
            view.removeOnAttachStateChangeListener(this)
        }
    })
}

/**
 * Turn a view into `ViewElement<View, *>`
 * ```kotlin
 * val text: TextView = /* obtain view */
 * text.element
 *   .textSize { body }
 * ```
 */
val <V: View> V.element: ViewElement<V, LayoutParams> get() = ViewElement.create(this)

inline fun <V: View> V.renderElement(block: (ViewElement<V, out LayoutParams>) -> Unit) {
    val element = this.element
    try {
        block(element)
    } finally {
        element.render()
    }
}

/**
 * Searches for the holding Activity
 */
val View.activity: Activity? get() {
    var context: Context? = this.context
    while (context != null) {
        if (context is Activity) {
            return context
        }
        context = (context as? ContextWrapper)?.baseContext
    }
    return null
}

/**
 * Searches for focused view in dedicated to this view Activity
 */
val View.currentFocus: View? get() = activity?.currentFocus