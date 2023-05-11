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
    // TODO: lazy
    val enabled: Boolean get() = false
    val pressed: Boolean get() = false
}

//enum class DrawableState2(@AttrRes val value: Int) {
//    Enabled(android.R.attr.state_enabled)
//}

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