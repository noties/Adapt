package io.noties.adapt.ui.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import androidx.annotation.AttrRes
import androidx.annotation.XmlRes
import org.xmlpull.v1.XmlPullParser

val Int.dip: Int
    get() {
        return (this * Resources.getSystem().displayMetrics.density + 0.5F).toInt()
    }

fun Resources.createAttributeSet(
    @XmlRes xmlResId: Int
): AttributeSet? = try {
    val xml = getXml(xmlResId)
    // manually advance until start tag is present
    var eventType = xml.eventType
    var found = false

    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
            found = true
            break
        }
        eventType = xml.next()
    }
    if (found && xml.attributeCount > 0) {
        Xml.asAttributeSet(xml)
    } else {
        null
    }
} catch (t: Throwable) {
    if (Log.isLoggable("adapt-ui", Log.ERROR)) {
        Log.e("adapt-ui", null, t)
    }
    null
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