package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams

// TODO: button
class ViewElement<V : View, LP : LayoutParams>(
    val provider: (Context) -> V
) {
    var viewBlocks: MutableList<V.() -> Unit> = mutableListOf()
    var layoutBlocks: MutableList<LP.() -> Unit> = mutableListOf()

    fun onView(
        block: V.() -> Unit
    ): ViewElement<V, LP> {
        return this.also {
            it.viewBlocks.add(block)
        }
    }
}