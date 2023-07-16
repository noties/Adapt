package io.noties.adapt.ui.item

import android.view.ViewGroup
import io.noties.adapt.ui.ViewFactory

/**
 * @since $UNRELEASED;
 */
abstract class ElementItemNoRef(id: Long) : ElementItem<Unit>(id, {}) {
    abstract fun ViewFactory<ViewGroup.LayoutParams>.body()

    override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Unit) = body()

    // For item without references bind seems to be optional
    override fun bind(holder: Holder<Unit>) = Unit
}