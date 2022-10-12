package io.noties.adapt.ui.util

import android.view.View
import android.view.ViewGroup

internal inline val ViewGroup.children: List<View>
    get() = (0 until childCount)
        .map { getChildAt(it) }