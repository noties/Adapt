package io.noties.adapt.ui.shape

import kotlin.reflect.KMutableProperty0

fun <S : Shape> S.reference(property: KMutableProperty0<in S>): Shape = this.also {
    property.set(it)
}

fun <R, D : ShapeDrawable<R>> D.reference(property: KMutableProperty0<in D>): D = this.also {
    property.set(it)
}