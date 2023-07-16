package io.noties.adapt.sample.explore

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.layout
import io.noties.debug.Debug
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

class ExploreAdaptUIBinding {
    companion object {
        fun <T, V : View, LP : LayoutParams> ViewElement<V, LP>.bind(
            property: Prop<T>,
            block: ViewElement<V, LP>.(T) -> Unit
        ) = this.also { element ->

            // if used inside adapt context (in an item) we must use the getValue
            //  getter, which accepts receiver... but our property does not use
            //  receiver, would it be possible to still reference it?

            fun deliver(value: T) {
                if (element.isInitialized) {
                    // can dispatch here... must ensure that it is not rending
//                block(element, value)
//                element.render()
                    element.render {
                        block(it, value)
                    }
                } else {
                    element.onView {
                        element.view.post {
                            element.render {
                                block(it, value)
                            }
                        }
                    }
                }
            }

            // trigger initial result
//        block(element, property.value)
            deliver(property.value)
            // also must validate if it is initialized in order to render

            // subscribe to updates
            property.listeners.add {
//            // todo block should also be executed only if not rendering
//            block(element, it)
//            if (element.isInitialized) {
//                if (element.isRendering) {
//                    // must post here
//                } else {
//                    element.render()
//                }
//            }
                deliver(it)
            }

            // unsubscibe when view is detached..
            //  how to also give ability to unsubscribe a prop?
        }

        // okay, this, then how to subscribe adapt updates?
//  or.. receive an update and trigger an update?
//  we cannot do until we went through onBind, as createView is called
//  on a random item and we cannot expect it to have proper property initialized
//  (as we would subscribe to item that creates property, which is not that would be actually
//  bound)
        fun <R, T, P : Prop<T>, V : View, LP : LayoutParams> ViewElement<V, LP>.bind(
            property: KProperty1<R, P>,
            block: ViewElement<V, LP>.(T) -> Unit
        ): ViewElement<V, LP> = this.also {
            // should we add to references the binding that we would trigger in onbind?
            // we can register a callback that would be called in on-bind with target item
            // and in that callback we would simply redirect to normal bind?

            val callback: (R) -> Unit = {
                bind(property.get(it), block)
            }
        }
    }

    fun interface PropUpdated<V> {
        fun update(value: V)
    }

    // another thing.. we can pass prop around, so receiver might not be the one
    //  that is actually holding/subscribing to updates...
    class Prop<V : Any?>(
        var value: V
    ) : ReadWriteProperty<Any, V> {

        fun set(value: V) {
            Debug.i("set value:$value listeners:${listeners.size}")
            this.value = value
            // notify observers
            listeners.forEach { it.update(value) }
        }

        val listeners = mutableListOf<PropUpdated<V>>()

        override fun getValue(thisRef: Any, property: KProperty<*>): V {
            return value
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
            set(value)
        }

        @CheckResult
        operator fun invoke(): V = value

        operator fun invoke(value: V) = set(value)

        fun update(block: V.() -> Unit) {
            block(value)
            // trigger update
        }
    }

    class TestItem(val color: Prop<Int>) : ElementItemNoRef(0) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            // we have no accept to adapt or holder, so we cannot subscribe..
            // this would work only when items are not recreated - in ViewGroup (but not RecyclerView)
            Text("Hello")
                .bind(color) { textColor(it) }
                .bind(TestItem::color) {
                    textColor(it)
                }
        }
    }

    //    private val colorValue: Int by Prop(0)
    // TODO: how to create a property that can accept receiver and actually
    //  store property in that receiver?
    private val color = Prop(0)
    private val text = Prop<String?>(null)

    fun hey(context: Context) {
        ViewFactory.createView(context) {
            Text()
                .layout(FILL, WRAP)
//                .textStylePrimary()
                .bind(color) {
                    Debug.i("color:$it")
                    background(it)
                }
        }
    }
}