package io.noties.adapt.ui.shape

import kotlin.reflect.KMutableProperty0

@Suppress("MemberVisibilityCanBePrivate")
class ShapeDrawableRef<R : Any>(
    shape: Shape,
    val ref: R
) : ShapeDrawable(shape) {

    // TODO: rename to render?
    fun invalidate(block: R.() -> Unit) {
        block(ref)
        invalidateSelf()
    }
}

fun <S : Shape> S.reference(property: KMutableProperty0<in S>): Shape = this.also {
    property.set(it)
}

fun <D : ShapeDrawable> D.reference(property: KMutableProperty0<in D>): D = this.also {
    property.set(it)
}