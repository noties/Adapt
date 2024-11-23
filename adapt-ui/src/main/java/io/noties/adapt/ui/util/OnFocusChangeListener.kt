package io.noties.adapt.ui.util

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.R
import io.noties.adapt.ui.ViewElement
import java.util.concurrent.CopyOnWriteArrayList

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onFocusChanged(
    listener: OnFocusChangedListenerRegistration.(view: V, hasFocus: Boolean) -> Unit
) = onView { v ->
    lateinit var registration: OnFocusChangedListenerRegistration
    registration = v.addOnFocusChangedListener { _, hasFocus ->
        listener(registration, v, hasFocus)
    }
}

fun interface OnFocusChangedListenerRegistration {
    fun unregisterOnFocusChangedListener()
}

fun View.addOnFocusChangedListener(
    onFocusChangedListener: View.OnFocusChangeListener
): OnFocusChangedListenerRegistration {
    val delegate = OnFocusChangedListenerDelegate.getOrCreate(this)
    delegate.add(onFocusChangedListener)
    return OnFocusChangedListenerRegistration { delegate.remove(onFocusChangedListener) }
}

fun View.removeOnFocusChangedListener(
    onFocusChangedListener: View.OnFocusChangeListener
) {
    val delegate = OnFocusChangedListenerDelegate.get(this)
    delegate?.remove(onFocusChangedListener)
}

class OnFocusChangedListenerDelegate private constructor(val view: View) {
    companion object {
        fun get(view: View): OnFocusChangedListenerDelegate? {
            return view.getTag(tagId) as? OnFocusChangedListenerDelegate
        }

        fun getOrCreate(view: View): OnFocusChangedListenerDelegate {
            return get(view) ?: OnFocusChangedListenerDelegate(view).also {
                view.setTag(tagId, it)
            }
        }

        private val tagId get() = R.id.adaptui_internal_focus_delegate
    }

    private val listeners = CopyOnWriteArrayList<View.OnFocusChangeListener>()

    init {
        // NB! views are detached when used in adaptors, so sometimes it does not make sense
        //  to unsubscribe when detached, but if it is intended or not, hard to guess
        view.onDetachedOnce {
            view.setTag(tagId, null)
            listeners.clear()
            view.onFocusChangeListener = null
        }
        view.setOnFocusChangeListener { view, focused ->
            listeners.forEach {
                it.onFocusChange(view, focused)
            }
        }
    }

    fun add(listener: View.OnFocusChangeListener) {
        listeners.add(listener)
    }

    fun remove(listener: View.OnFocusChangeListener) {
        listeners.remove(listener)
    }
}