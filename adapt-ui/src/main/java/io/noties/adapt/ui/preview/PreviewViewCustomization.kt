package io.noties.adapt.ui.preview

import android.view.View
import androidx.annotation.ColorInt

interface PreviewViewCustomization {
    // return null, if no preview should happen?
    // draw this one, but do not draw children?
    sealed class Result
    data object NoPreview: Result()
    class Preview(
        val drawable: PreviewDrawable,
        /**
         * Tells if children should be additionally drawn or this drawable
         * already have taken care of it (thus continueDrawingChildren = false = do not draw my children)
         */
        val continueDrawingChildren: Boolean = true
    ): Result()

    // we need also a way to stop default preview drawing itself
    fun preview(@ColorInt color: Int, view: View): Result

    companion object : PreviewViewCustomization {
        override fun preview(@ColorInt color: Int, view: View): Result {
            // by default bounds and padding drawables are used to preview
            return Preview(
                drawable = PreviewDrawable.default(view, color),
                continueDrawingChildren = true
            )
        }
    }

}