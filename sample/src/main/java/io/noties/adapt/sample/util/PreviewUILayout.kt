package io.noties.adapt.sample.util

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.App
import io.noties.adapt.ui.preview.AdaptUIPreviewLayout

abstract class PreviewUILayout(context: Context, attrs: AttributeSet?) :
    AdaptUIPreviewLayout(context, attrs) {

    override fun initialize(layout: AdaptUIPreviewLayout) {
        super.initialize(layout)

        App.mock(context)
    }
}