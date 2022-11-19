package io.noties.adapt.ui.util

import android.util.AttributeSet
import android.util.Xml
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.R
import org.xmlpull.v1.XmlPullParser

internal inline val ViewGroup.children: List<View>
    get() = (0 until childCount)
        .map { getChildAt(it) }


fun ViewGroup.createLayoutParams(): LayoutParams? {
    fun attrs(): AttributeSet? {
        val xml = context.resources.getXml(R.xml.internal_adaptui_default_layout_params)

        // manually advance until start tag is present
        var eventType = xml.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                break
            }
            eventType = xml.next()
        }

        // something went wrong, 2 required attributes are not found
        if (xml.attributeCount != 2) {
            return null
        }

        return Xml.asAttributeSet(xml)
    }

    val attrs = attrs() ?: return null

    return try {
        generateLayoutParams(attrs)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}