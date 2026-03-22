package io.noties.adapt.sample.explore

import android.widget.SeekBar
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element

object ExploreSlider {

    /**
     * @see io.noties.adapt.sample.samples.adaptui.SeekBar
     */
    @Suppress("FunctionName")
    fun <LP : LayoutParams> ViewFactory<LP>.Slider(

    ) = Element { SeekBar(it) }
}