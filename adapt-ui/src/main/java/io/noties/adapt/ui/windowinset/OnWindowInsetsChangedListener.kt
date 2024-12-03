package io.noties.adapt.ui.windowinset

import android.view.View
import android.view.WindowInsets
import io.noties.adapt.ui.R
import io.noties.adapt.ui.util.onAttachedOnce
import io.noties.adapt.ui.util.onDetachedOnce
import java.util.concurrent.CopyOnWriteArrayList

fun interface OnWindowInsetsChangedListener {
    fun onWindowInsetsChanged(view: View, windowInsets: WindowInsets)
}

class OnWindowInsetsChangedListenerDelegate(val view: View) {
    companion object {
        fun get(view: View): OnWindowInsetsChangedListenerDelegate? {
            return view.getTag(tagId) as? OnWindowInsetsChangedListenerDelegate
        }

        fun getOrCreate(view: View): OnWindowInsetsChangedListenerDelegate {
            return get(view) ?: run {
                val delegate = OnWindowInsetsChangedListenerDelegate(view)
                view.setTag(tagId, delegate)
                delegate
            }
        }

        private val tagId: Int get() = R.id.adaptui_on_window_insets_changed_listener_delegate
    }

    private val listeners = CopyOnWriteArrayList<OnWindowInsetsChangedListener>()

    init {
        view.onDetachedOnce {
            view.setTag(tagId, null)
            listeners.clear()
            view.setOnApplyWindowInsetsListener(null)
        }
        view.setOnApplyWindowInsetsListener { v, insets ->
            for (listener in listeners) {
                listener.onWindowInsetsChanged(v, insets)
            }
            // do not change
            insets
        }
        triggerRequest()
    }

    fun add(listener: OnWindowInsetsChangedListener) {
        listeners.add(listener)

        triggerRequest()
    }

    fun remove(listener: OnWindowInsetsChangedListener) {
        listeners.remove(listener)
    }

    private fun triggerRequest() {
        if (view.isAttachedToWindow) {
            view.requestApplyInsets()
        } else {
            view.onAttachedOnce {
                view.requestApplyInsets()
            }
        }
    }
}