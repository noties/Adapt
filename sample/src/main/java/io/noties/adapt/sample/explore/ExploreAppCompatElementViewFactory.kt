//package io.noties.adapt.sample.explore
//
//import androidx.appcompat.widget.AppCompatEditText
//import androidx.appcompat.widget.AppCompatImageView
//import androidx.appcompat.widget.AppCompatTextView
//import io.noties.adapt.ui.element.ElementViewFactory
//
//object ExploreAppCompatElementViewFactory {
//    fun install() {
//        ElementViewFactory.Image = { AppCompatImageView(it) }
//        ElementViewFactory.Text = { AppCompatTextView(it) }
//        ElementViewFactory.TextInput = { AppCompatEditText(it) }
//    }
//}