package io.noties.adapt.sample.explore

import android.view.View
import io.noties.adapt.sample.explore.ExploreOnFocusChangeListener.OnFocusChangedListenerRegistration
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.util.onDetachedOnce
import java.util.concurrent.CopyOnWriteArrayList

object ExploreOnFocusChangeListener {

    fun interface OnFocusChangedListenerRegistration {
        fun unregisterOnFocusChangedListener()
    }

    fun <V : View> V.addOnFocusChangedListener(
        listener: (view: V, hasFocus: Boolean) -> Unit
    ): OnFocusChangedListenerRegistration {
        val delegate = OnFocusChangedListenerDelegate.invoke(this)
        val focusListener: View.OnFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            listener(this, hasFocus)
        }
        delegate.add(focusListener)
        return OnFocusChangedListenerRegistration { delegate.remove(focusListener) }
    }

    fun <V : View, LP : LayoutParams> ViewElement<V, LP>.onFocusChanged(
        listener: OnFocusChangedListenerRegistration.(view: V, hasFocus: Boolean) -> Unit
    ) = onView {
        lateinit var registration: OnFocusChangedListenerRegistration
        registration = it.addOnFocusChangedListener { view, hasFocus ->
            listener(registration, view, hasFocus)
        }
    }

    class OnFocusChangedListenerDelegate private constructor(val view: View) {
        companion object {
            operator fun invoke(view: View): OnFocusChangedListenerDelegate {
                return view.getTag(tagId) as? OnFocusChangedListenerDelegate ?: kotlin.run {
                    OnFocusChangedListenerDelegate(view).also {
                        view.setTag(tagId, it)
                    }
                }
            }

            // TODO: proper id
            private const val tagId = 0
        }

        private val listeners = CopyOnWriteArrayList<View.OnFocusChangeListener>()

        init {
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
}