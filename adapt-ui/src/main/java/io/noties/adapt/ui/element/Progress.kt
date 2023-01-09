package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * Creates an indeterminate progress bar
 * @see ProgressBar
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Progress(): ViewElement<ProgressBar, LP> =
    Element(ElementViewFactory.Progress)

/**
 * @see ProgressBar.setIndeterminateTintList
 * @see ProgressBar.setProgressTintList
 */
fun <V : ProgressBar, LP : LayoutParams> ViewElement<V, LP>.progressTint(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    it.indeterminateTintList = ColorStateList.valueOf(color)
}

/**
 * @see ProgressBar.setIndeterminateTintList
 * @see ProgressBar.setProgressTintList
 * @see io.noties.adapt.ui.util.ColorStateListBuilder
 */
fun <V : ProgressBar, LP : LayoutParams> ViewElement<V, LP>.progressTint(
    colorStateList: ColorStateList
): ViewElement<V, LP> = onView {
    it.indeterminateTintList = colorStateList
}