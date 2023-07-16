package io.noties.adapt.sample.util

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.noties.adapt.sample.App
import io.noties.adapt.sample.SampleView

abstract class PreviewSampleView(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs) {
    abstract val sampleView: SampleView

    init {
        App.mock(context)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        initialize(this)

        val sampleView = this.sampleView

        val name = sampleView::class.java.name

        val sample = SampleUtil.readSamples(context)
            .first { name == it.javaClassName }

        val view = sampleView.view(sample, this)
        addView(view)
    }

    protected open fun initialize(view: PreviewSampleView) = Unit
}