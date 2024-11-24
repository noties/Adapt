package io.noties.adapt.sample.ui

import android.view.View
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.preview.PreviewViewElement
import io.noties.adapt.ui.preview.preview

private val isRunningScreenshotTests = try {
    @Suppress("SENSELESS_COMPARISON")
    Class.forName("io.noties.adapt.sample.test.AdaptUiSampleIsInTests") != null
} catch (t: Throwable) {
    false
}

fun <V: View, LP: LayoutParams> ViewElement<V, LP>.test(
    block: (PreviewViewElement<V, LP>) -> Unit
) = this
    .let {
        if (isRunningScreenshotTests) {
            it.preview(allowRenderingOnRealDevice = true, block = block)
        } else {
            it
        }
    }