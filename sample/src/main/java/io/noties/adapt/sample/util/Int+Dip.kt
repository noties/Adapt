package io.noties.adapt.sample.util

import android.content.res.Resources
import kotlin.math.roundToInt

val Int.dip: Int get() = (Resources.getSystem().displayMetrics.density * this + 0.5F).toInt()

val Int.fromPxToDp: Int get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()