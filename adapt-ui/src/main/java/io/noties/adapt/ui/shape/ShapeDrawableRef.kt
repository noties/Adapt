package io.noties.adapt.ui.shape

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