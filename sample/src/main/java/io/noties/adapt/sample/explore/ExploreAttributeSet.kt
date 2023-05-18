package io.noties.adapt.sample.explore

import android.content.Context
import android.util.AttributeSet
import android.util.Xml
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.util.createLayoutParams
import org.xmlpull.v1.XmlPullParser

// In order to make a view-group generate default layout parameters we need AttributeSet
//  which is not really accessible to be generated. But, we could put an XML in xml directory
//  obtain it raw, manually advance until the START_TAG and then create attribite set... this does not
//  sound like an optimization and it seems to be able to just
object ExploreAttributeSet {

    private fun attrs(context: Context): AttributeSet? {
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

    // from quick measurements this version is faster (both are under 1 ms, but variant with attrs
    //  takes much more time)
    fun withDefaultLayoutParams(viewGroup: ViewGroup, view: View) {
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        viewGroup.addView(view)
    }

    fun withAttrs(viewGroup: ViewGroup, view: View) {
        view.layoutParams = viewGroup.createLayoutParams()
        viewGroup.addView(view)
    }
}