package io.noties.adapt.ui.app.text

// TextStyles should be available to TextView only, but right now I cannot
//  specify it in generics -> limit extensions to accept only children of TextView, but do
//  not allow specifying generic extensions, like padding (which are available to all views)
//  Also, it seems to not be possible to _close_ extensions of layout, `Nothing` would just
//  disable ALL extensions (even view-based)
//object TextStyles {
//    fun textStyle(
//        block: ViewFactoryConstants.(ViewElement<TextView, *>) -> Unit
//    ) = ElementStyle.view<TextView> { block(this, it) }
//}

