package io.noties.adapt.ui.element

import android.content.Context
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName")
fun <V : View, LP : ViewGroup.LayoutParams> ViewFactory<LP>.Element(
    provider: (Context) -> V
): ViewElement<V, LP> = ViewElement<V, LP>(provider).also(elements::add)