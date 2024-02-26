package io.noties.adapt.sample.explore

import android.graphics.drawable.ColorDrawable
import io.noties.adapt.sample.R
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.ui.app.App
import io.noties.adapt.ui.shape.AssetShape
import io.noties.debug.Debug

object TextSizesExplore {
    fun hey() {
        val id = R.dimen.some_scaled_dimen
        val resources = App.shared.resources
        val density = resources.displayMetrics.density
        val scaledDensity = resources.displayMetrics.scaledDensity
        // all are the same :explode:
        val dimension = resources.getDimension(id)
//        val offset = resources.getDimensionPixelOffset(id)
//        val size = resources.getDimensionPixelSize(id)
        Debug.i("dimension:$dimension dp:${dimension / density} sp:${dimension / scaledDensity}")
    }

    fun asset() {
        val shape = AssetShape(ColorDrawable()) {
            tint { accent }
        }
    }
}