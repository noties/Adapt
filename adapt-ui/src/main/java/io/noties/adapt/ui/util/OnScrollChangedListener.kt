package io.noties.adapt.ui.util

import android.os.Build
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.View.OnScrollChangeListener
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import io.noties.adapt.ui.R
import java.util.concurrent.CopyOnWriteArrayList

fun interface OnScrollChangedListenerRegistration {
    fun unregisterOnScrollChangedListener()
}

/**
 * Once the view is detached, the state is cleared and all callbacks are unregistered. In order to
 * receive events again a listener must be registered again.
 * NB! unregistering the listener is important, as viewTreeObserver might deliver scroll events to
 * other unrelated views meanwhile this view is already detached from window.
 * NB! at first all implementations used [ViewTreeObserver], but after testing it seems that
 * callbacks received from [ViewTreeObserver] are processed differently - views are animated
 * between states instead of following to the exact positions specified. For example, sticky view
 * would be visually glitched - move out of viewport in an unreliable way
 */
fun <V : View> V.addOnScrollChangedListener(
    action: (V, deltaX: Int, deltaY: Int) -> Unit
): OnScrollChangedListenerRegistration {
    val view = this

    // use native scroll listener (with delegate)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val listener = OnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->
            action(view, scrollX - oldScrollX, scrollY - oldScrollY)
        }
        val delegate = OnScrollChangedListenerDelegate.invoke(view)
        delegate.add(listener)
        return OnScrollChangedListenerRegistration { delegate.remove(listener) }
    }

    val delegate = OnScrollChangedListenerDelegatePreM(view) { deltaX, deltaY ->
        action(view, deltaX, deltaY)
    }
    return OnScrollChangedListenerRegistration {
        delegate.unregister()
    }
}

@RequiresApi(Build.VERSION_CODES.M)
class OnScrollChangedListenerDelegate private constructor(val view: View) {
    private val listeners = CopyOnWriteArrayList<OnScrollChangeListener>()

    companion object {
        operator fun invoke(view: View): OnScrollChangedListenerDelegate {
            val tag = view.getTag(tagId) as? OnScrollChangedListenerDelegate
            if (tag != null) {
                return tag
            }
            val delegate = OnScrollChangedListenerDelegate(view)
            view.setTag(tagId, delegate)
            return delegate
        }

        private val tagId = R.id.adaptui_internal_scroll_delegate
    }

    init {
        // unregister all when view is detached from window
        view.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) = Unit
            override fun onViewDetachedFromWindow(v: View?) {
                view.removeOnAttachStateChangeListener(this)
                view.setTag(tagId, null)
                view.setOnScrollChangeListener(null)
                listeners.clear()
            }
        })
        view.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            listeners.forEach {
                it.onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY)
            }
        }
    }

    fun add(listener: OnScrollChangeListener) {
        listeners.add(listener)
    }

    fun remove(listener: OnScrollChangeListener) {
        listeners.remove(listener)
    }
}

private class OnScrollChangedListenerDelegatePreM(
    val view: View,
    val callback: (deltaX: Int, deltaY: Int) -> Unit
) {

    private var previousX = view.scrollX
    private var previousY = view.scrollY

    private val onScrollChangedListener = ViewTreeObserver.OnScrollChangedListener {
        val x = view.scrollX
        val y = view.scrollY
        if (x != previousX || y != previousY) {
            callback(x - previousX, y - previousY)
            previousX = x
            previousY = y
        }
    }

    private val onAttachStateChangeListener = object : OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) = Unit
        override fun onViewDetachedFromWindow(v: View?) {
            unregister()
        }
    }

    init {
        view.viewTreeObserver
            .takeIf { it.isAlive }
            ?.addOnScrollChangedListener(onScrollChangedListener)
        view.addOnAttachStateChangeListener(onAttachStateChangeListener)
    }

    fun unregister() {
        view.viewTreeObserver
            .takeIf { it.isAlive }
            ?.removeOnScrollChangedListener(onScrollChangedListener)
        view.removeOnAttachStateChangeListener(onAttachStateChangeListener)
    }
}