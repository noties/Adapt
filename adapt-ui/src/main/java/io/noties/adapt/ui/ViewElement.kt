package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams

class ViewElement<V : View, LP : LayoutParams>(
    internal val provider: (Context) -> V
) {
    internal var viewBlocks: MutableList<V.() -> Unit> = mutableListOf()
    internal var layoutBlocks: MutableList<LP.() -> Unit> = mutableListOf()

    fun onView(
        block: V.() -> Unit
    ): ViewElement<V, LP> = this.also {
        it.viewBlocks.add(block)
    }
}

typealias AnyViewElement = ViewElement<*, *>