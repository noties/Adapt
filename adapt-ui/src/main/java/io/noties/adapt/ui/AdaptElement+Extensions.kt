package io.noties.adapt.ui

import io.noties.adapt.Adapt
import io.noties.adapt.Item
import kotlin.reflect.KMutableProperty0

fun <A : Adapt, AE : AdaptElement<A>> AE.onAdapt(block: A.() -> Unit): AE = this.also {
    callbacks.add(block)
}

fun <A : Adapt, AE : AdaptElement<A>> AE.reference(
    property: KMutableProperty0<in A>
) = onAdapt {
    property.set(this)
}

fun <A : Adapt, AE : AdaptElement<A>> AE.setItems(
    iterable: Iterable<Item<*>>
) = onAdapt {
    setItems(iterable.toList())
}