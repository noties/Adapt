package io.noties.adapt.sample.samples.adaptui

import android.os.Build
import androidx.annotation.ColorRes
import io.noties.adapt.sample.App
import io.noties.adapt.sample.R

object Colors {
    val white: Int = get(R.color.white)
    val black: Int = get(R.color.black)
    val orange: Int = get(R.color.orange)
    val primary: Int = get(R.color.primary)
    val accent: Int = get(R.color.accent)
    val yellow: Int = get(R.color.yellow)

    private fun get(@ColorRes colorResId: Int): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            App.shared.getColor(colorResId)
        } else {
            @Suppress("DEPRECATION")
            App.shared.resources.getColor(colorResId)
        }
}