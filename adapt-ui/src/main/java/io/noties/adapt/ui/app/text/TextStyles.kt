package io.noties.adapt.ui.app.text

import android.widget.TextView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.element.ElementStyle

// TextStyles should be available to TextView only, but right now I cannot
//  specify it in generics -> limit extensions to accept only children of TextView, but do
//  not allow specifying generic extensions, like padding (which are available to all views)
//  Also, it seems to not be possible to _close_ extensions of layout, `Nothing` would just
//  disable ALL extensions (even view-based)
// Let it be, as we might find a way and if we would have a dedicated type already
interface TextStyles {
    companion object: TextStyles

    fun textStyle(
        block: ViewFactoryConstants.(ViewElement<TextView, *>) -> Unit
    ) = ElementStyle.view<TextView> { block(this, it) }
}

typealias TextStylesBuilder = TextStyles.() -> ElementStyle<TextView, *>