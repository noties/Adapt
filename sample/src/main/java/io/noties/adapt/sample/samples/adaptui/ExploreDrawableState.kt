package io.noties.adapt.sample.samples.adaptui

import android.graphics.drawable.DrawableContainer
import android.view.View
import androidx.annotation.AttrRes
import io.noties.adapt.sample.App
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text

class DrawableState(@AttrRes val state: IntArray) {
    val enabled: Boolean by lazy(LazyThreadSafetyMode.NONE) { state.contains(android.R.attr.state_enabled) }
    val pressed: Boolean get() = false
}

// pressed is not reported without drawable
//
fun <V: View, LP: LayoutParams> ViewElement<V, LP>.onDrawableStateChange(
    action: (V, DrawableState) -> Unit
) = onView {
    // TODO: add a drawable that would report stateful-ness
    //  decide which - background, foreground or... something else
    //  if statelist (drawable container, add to it?)
}

fun hey2() {
    ViewFactory.createView(App.shared) {
        Text()
            .onDrawableStateChange { v, state ->
                when {
                    state.enabled && state.pressed -> 9
                }
            }
    }
}