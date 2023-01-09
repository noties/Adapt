package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory

/**
 * Element for [ImageView].
 * By default uses [ImageView.ScaleType.CENTER_INSIDE] scaleType
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Image(
    scaleType: ImageView.ScaleType? = null
): ViewElement<ImageView, LP> = Element(ElementViewFactory.Image) {
    it.scaleType = scaleType ?: ImageView.ScaleType.CENTER_INSIDE
}

/**
 * @see ImageView.setImageResource
 */
@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Image(
    @DrawableRes resourceId: Int,
    scaleType: ImageView.ScaleType? = null
): ViewElement<ImageView, LP> = Image(scaleType).image(resourceId)

/**
 * @see ImageView.setImageDrawable
 */
@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Image(
    drawable: Drawable?,
    scaleType: ImageView.ScaleType? = null
): ViewElement<ImageView, LP> = Image(scaleType).image(drawable)

/**
 * @see ImageView.setImageBitmap
 */
@Suppress("FunctionName", "unused")
fun <LP : LayoutParams> ViewFactory<LP>.Image(
    bitmap: Bitmap?,
    scaleType: ImageView.ScaleType? = null
): ViewElement<ImageView, LP> = Image(scaleType).image(bitmap)

/**
 * @see ImageView.setImageResource
 */
fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.image(
    @DrawableRes resourceId: Int
): ViewElement<V, LP> = onView {
    it.setImageResource(resourceId)
}

/**
 * @see ImageView.setImageDrawable
 */
fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.image(
    drawable: Drawable?,
): ViewElement<V, LP> = onView {
    it.setImageDrawable(drawable)
}

/**
 * @see ImageView.setImageBitmap
 */
fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.image(
    bitmap: Bitmap?,
): ViewElement<V, LP> = onView {
    it.setImageBitmap(bitmap)
}

/**
 * Scale Type
 * @see ImageView.setScaleType
 */
fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.imageScaleType(
    scaleType: ImageView.ScaleType
): ViewElement<V, LP> = onView {
    it.scaleType = scaleType
}

/**
 * Null value for the `mode` argument would not set it, otherwise tint value becomes
 * cleared according to the documentation.
 * @see ImageView.setImageTintList
 * @see ImageView.setImageTintMode
 */
fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.imageTint(
    @ColorInt color: Int,
    mode: PorterDuff.Mode? = null
): ViewElement<V, LP> = imageTint(ColorStateList.valueOf(color), mode)

/**
 * Null value for the `mode` argument would not set it, otherwise tint value becomes
 * cleared according to the documentation.
 * @see ImageView.setImageTintList
 * @see ImageView.setImageTintMode
 * @see io.noties.adapt.ui.util.ColorStateListBuilder
 */
fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.imageTint(
    colorStateList: ColorStateList,
    mode: PorterDuff.Mode? = null
): ViewElement<V, LP> = onView { view ->
    view.imageTintList = colorStateList
    mode?.also { view.imageTintMode = it }
}