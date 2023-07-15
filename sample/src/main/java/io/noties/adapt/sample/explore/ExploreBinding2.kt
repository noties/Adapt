package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.util.onAttachedOnce
import io.noties.adapt.ui.util.onDetachedOnce

object ExploreBinding2 {
    // prop should handle the same values (ignore or deliver)
    interface Prop<T : Any?> {
        interface PropRegistration {
            fun unregister()
        }

        val value: T
        fun setValue(value: T)

        fun observe(action: (T) -> Unit): PropRegistration

        // fun distinct()
        // fun distinct(checker: (T, T) -> Boolean)

        // should we even expose it? it seems when observing it is logical to deliver initial result
        // fun deliverInitial
    }

    // observe with view attach/detach
    //  should we resubscribe if view was detached then attached? hm.. seems no
    fun <T> Prop<T>.observe(view: View, action: (T) -> Unit) {
        // initial result, can it be triggered?
        if (view.isAttachedToWindow) {
            val registration = observe(action)
            view.onDetachedOnce { registration.unregister() }
        } else {
            view.onAttachedOnce {
                val registration = observe(action)
                it.onDetachedOnce {
                    registration.unregister()
                }
            }
        }
    }

    fun <V: TextView, T: CharSequence?> ViewElement<V, out LayoutParams>.textBind(prop: Prop<T>) = bind(prop) { el, value ->
        el.text(value)
    }

    private val someText: Prop<String> = TODO()

    fun hey(context: Context) {
        ViewFactory.createView(context) {
            Text()
                .bind(someText) { el, value ->
                    el.text(value)
                }
                .textBind(someText)
        }
    }

    // not very convenient, as we need to specify both argument names
    fun <V : View, LP : LayoutParams, T> ViewElement<V, LP>.bind(
        prop: Prop<T>,
        action: (ViewElement<V, LP>, T) -> Unit
    ) = this.also { element ->
        element.onView {
            // register attach/detach
            // should we keep track of previous? or should prop do it? yep, prop should handle it
            val callback: (T) -> Unit = { value ->
                action(element, value)
                // trigger render?
                element.render()
            }

            prop.observe(it, callback)
        }
    }
}