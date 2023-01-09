package io.noties.adapt.ui

import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton


fun <V, LP : LayoutParams> ViewElement<V, LP>.checked(
    checked: Boolean = true
): ViewElement<V, LP> where V : View, V : Checkable = onView {
    it.isChecked = checked
}

fun <V : CompoundButton, LP : LayoutParams> ViewElement<V, LP>.onViewCheckedChanged(
    callback: (V, checked: Boolean) -> Unit
) = onView {
    it.setOnCheckedChangeListener { v, isChecked ->
        @Suppress("UNCHECKED_CAST")
        callback(v as V, isChecked)
    }
}