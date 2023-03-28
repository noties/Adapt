package io.noties.adapt.ui.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.AttrRes

val Int.dip: Int
    get() {
        return (this * Resources.getSystem().displayMetrics.density + 0.5F).toInt()
    }

internal fun resolveDrawableAttr(context: Context, @AttrRes attr: Int): Drawable? {
    val array = context.obtainStyledAttributes(intArrayOf(attr))
    try {
        return array.getDrawable(0)
    } catch (t: Throwable) {
        if (Log.isLoggable("adapt-ui", Log.ERROR)) {
            Log.e("adapt-ui", null, t)
        }
    } finally {
        array.recycle()
    }
    return null
}

internal fun resolveDefaultSelectableDrawable(context: Context): Drawable? = resolveDrawableAttr(
    context,
    android.R.attr.selectableItemBackground
)