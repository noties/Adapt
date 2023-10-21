package io.noties.adapt.ui.util

import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener

val View.density: Float get() = context.resources.displayMetrics.density

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