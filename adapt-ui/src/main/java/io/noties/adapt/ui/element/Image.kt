package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Image(): ViewElement<ImageView, LP> {
    return ViewElement<ImageView, LP> {
        ImageView(it)
    }.also(elements::add)
        .imageScaleType(ImageView.ScaleType.CENTER_INSIDE)
}

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Image(
    @DrawableRes resourceId: Int
): ViewElement<ImageView, LP> = Image().onView { setImageResource(resourceId) }

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Image(
    drawable: Drawable?
): ViewElement<ImageView, LP> = Image().onView { setImageDrawable(drawable) }

@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Image(
    bitmap: Bitmap?
): ViewElement<ImageView, LP> = Image().onView { setImageBitmap(bitmap) }


fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.imageScaleType(
    scaleType: ImageView.ScaleType
): ViewElement<V, LP> = onView {
    this.scaleType = scaleType
}

fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.imageTint(
    @ColorInt color: Int
): ViewElement<V, LP> = onView {
    imageTintList = ColorStateList.valueOf(color)
}