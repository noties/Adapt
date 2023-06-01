package io.noties.adapt.ui.util

import android.view.View

fun <V: View> V.onAttachedOnce(block: (V) -> Unit) {
    val view = this
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            block(view)
            view.removeOnAttachStateChangeListener(this)
        }
        override fun onViewDetachedFromWindow(v: View?) = Unit
    })
}

fun <V: View> V.onDetachedOnce(block: (V) -> Unit) {
    val view = this
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) = Unit
        override fun onViewDetachedFromWindow(v: View?) {
            block(view)
            view.removeOnAttachStateChangeListener(this)
        }
    })
}