package io.noties.adapt.sample.samples.adaptui

import android.content.res.ColorStateList
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element
import kotlin.math.roundToInt

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.SeekBar() = Element {
    SeekBar(it).also { sb ->
        sb.max = 100
    }
}

// hm, if we could add multiple listeners, this would be ok
//  plus, we need a way to set value, maybe accept 0f-1f as ration?
fun <V : SeekBar, LP : LayoutParams> ViewElement<V, LP>.seekBarOnChanged(
    listener: (/*@FloatRange(from = 0.0, to = 1.0)*/ Float) -> Unit
) = onView {
    setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            listener(progress / 100F)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    })
}

fun <V : SeekBar, LP : LayoutParams> ViewElement<V, LP>.seekBarTint(@ColorInt tint: Int) = onView {
    progressTintList = ColorStateList.valueOf(tint)
    thumbTintList = ColorStateList.valueOf(tint)
}

fun <V : SeekBar, LP : LayoutParams> ViewElement<V, LP>.seekBarValue(
    @FloatRange(from = 0.0, to = 1.0) ratio: Float
) = onView {
    progress = (ratio * 100).roundToInt()
    postInvalidate()
}