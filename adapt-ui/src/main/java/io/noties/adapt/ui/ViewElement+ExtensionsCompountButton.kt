package io.noties.adapt.ui

import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton


fun <V, LP : LayoutParams> ViewElement<V, LP>.checked(
    checked: Boolean = true
): ViewElement<V, LP> where V : View, V : Checkable = onView {
    isChecked = checked
}

fun <V : CompoundButton, LP : LayoutParams> ViewElement<V, LP>.onCheckedChanged(
    callback: (checked: Boolean) -> Unit
) = this.onView {
    setOnCheckedChangeListener { _, isChecked -> callback(isChecked) }
}