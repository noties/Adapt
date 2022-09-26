package io.noties.adapt.ui.util

import android.content.res.Resources

internal val Int.dip: Int get() {
    return (this * Resources.getSystem().displayMetrics.density + 0.5F).toInt()
}