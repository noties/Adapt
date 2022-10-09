package io.noties.adapt.ui.util

import android.view.View
import android.view.ViewGroup

internal inline val ViewGroup.children: Sequence<View>
    get() {
        var index = 0
        return generateSequence {
            val i = index++
            if (i < childCount) getChildAt(i) else null
        }
    }