package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.mockito.Mockito

internal fun newElement(): ViewElement<View, ViewGroup.LayoutParams> = newElementOfType()

internal inline fun <reified V : View> newElementOfType(): ViewElement<V, ViewGroup.LayoutParams> {
    return newElementOfTypeLayout()
}

internal inline fun <reified V : View, reified LP : LayoutParams> newElementOfTypeLayout(): ViewElement<V, LP> {
    return ViewElement<V, LP> { Mockito.mock(V::class.java) }.also {
        val context = Mockito.mock(Context::class.java)
        it.init(context)
        Mockito.`when`(it.view.context).thenReturn(context)
    }
}

internal inline fun <reified V : View, LP : LayoutParams> ViewElement<V, LP>.renderView(block: V.() -> Unit) {
    // there could be multiple view blocks, iterate over each
    render()
    block(view)
}