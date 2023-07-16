package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.App
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout

abstract class AdaptUISampleView : SampleView() {

    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            body()
        }
    }

    abstract fun ViewFactory<LayoutParams>.body()
}

abstract class AdaptUISamplePreview(
    context: Context,
    attrs: AttributeSet?
) : AdaptUIPreviewLayout(context, attrs) {

    abstract val sample: AdaptUISampleView

    override fun ViewFactory<LayoutParams>.body() {
        with(sample) {
            body()
        }
    }

    override fun initialize(layout: AdaptUIPreviewLayout) {
        super.initialize(layout)

        App.mock(context)
    }
}