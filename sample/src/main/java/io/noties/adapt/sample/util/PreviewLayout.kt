package io.noties.adapt.sample.util

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.noties.adapt.sample.App

abstract class PreviewLayout(
    context: Context,
    attributeSet: AttributeSet
) : FrameLayout(context, attributeSet) {
    init {
        App.mock(context)
    }
}