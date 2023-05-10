package io.noties.adapt.ui.util

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.R

internal inline val ViewGroup.children: List<View>
    get() = (0 until childCount)
        .map { getChildAt(it) }


fun ViewGroup.createLayoutParams(): LayoutParams? {
    fun attrs(): AttributeSet? {
        return context.resources.createAttributeSet(R.xml.internal_adaptui_default_layout_params)
    }

    val attrs = attrs() ?: return null

    return try {
        generateLayoutParams(attrs)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}