package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName")
fun <LP : ViewGroup.LayoutParams> ViewFactory<LP>.Progress(
    indeterminate: Boolean = true
): ViewElement<ProgressBar, LP> = ViewElement<ProgressBar, LP>(::ProgressBar)
    .also(elements::add)
    .onView {
        isIndeterminate = indeterminate
    }

fun <V : ProgressBar, LP : ViewGroup.LayoutParams> ViewElement<V, LP>.tint(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    if (isIndeterminate) {
        indeterminateTintList = ColorStateList.valueOf(color)
    } else {
        progressTintList = ColorStateList.valueOf(color)
    }
}