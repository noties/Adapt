package io.noties.adapt.sample.util

import android.view.View
import android.view.ViewGroup

fun View.activate(isActivated: Boolean) {
    if (this is ViewGroup) {
        this.children.forEach { it.activate(isActivated) }
    } else {
        this.isActivated = isActivated
    }
}

val ViewGroup.children: List<View>
    get() {
        val count = childCount.takeIf { it > 0 } ?: return emptyList()
        return List(count) {
            getChildAt(it)
        }
    }