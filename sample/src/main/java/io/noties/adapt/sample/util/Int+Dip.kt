package io.noties.adapt.sample.util

import android.content.res.Resources

val Int.dip: Int get() = (Resources.getSystem().displayMetrics.density * this + 0.5F).toInt()