package io.noties.adapt.ui

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RuntimeEnvironment

fun newElement(): ViewElement<View, ViewGroup.LayoutParams> = newElementOfType()

inline fun <reified V : View> newElementOfType(): ViewElement<V, ViewGroup.LayoutParams> {
    return newElementOfTypeLayout()
}

inline fun <reified V : View, reified LP : LayoutParams> newElementOfTypeLayout(): ViewElement<V, LP> {
    val context = Mockito.mock(Context::class.java).also {
        whenever(it.resources).thenReturn(Resources.getSystem())
    }
    return ViewElement<V, LP> {
        Mockito.mock(V::class.java).also { Mockito.`when`(it.context).thenReturn(context) }
    }.also {
        it.init(context)
    }
}

inline fun <reified V : View, LP : LayoutParams> ViewElement<V, LP>.renderView(block: V.() -> Unit) {
    // there could be multiple view blocks, iterate over each
    render()
    block(view)
}

inline fun obtainView(block: (ViewFactory<LayoutParams>).() -> Unit): View {
    return obtainView2(block)
}

inline fun <reified LP : LayoutParams> obtainView2(block: (ViewFactory<LP>).() -> Unit): View {
    val context = RuntimeEnvironment.getApplication()
    val factory = ViewFactory<LP>(context)
    block(factory)
    return factory.consumeElements().first().also {
        it.init(context)
        it.render()
    }.view
}

fun <V : View> ViewElement<V, LayoutParams>.useLayoutParams() = useLayoutParams(
    LayoutParams(0, 0)
)

fun <V : View, LP : LayoutParams> ViewElement<V, LP>.useLayoutParams(params: LP) =
    this.also {
        Mockito.`when`(view.layoutParams).thenReturn(params)
    }