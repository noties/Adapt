package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layoutFill

@AdaptSample(
    id = "20230520005529",
    title = "[Showcase] AdaptUI control flow",
    description = "<em>For loop</em>, <em>forEach</em>, <em>if-else</em>, <em>while</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseControlFlow : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            // Normal control flow, this creates 3 text views
            for (i in 0..2) {
                Text("for-loop:$i")
                    .textSize(16 + i)
            }

            // Another 3 views
            (3 until 6).forEach {
                Text("forEach:$it")
                    .textSize(16 + it)
            }

            // if check for some condition
            if (System.currentTimeMillis() % 2 == 0L) {
                Text("if, current time is even")
                    .textColor(Color.RED)
            } else {
                Text("else, current time is odd")
                    .textColor(Color.BLUE)
            }

            // version check
            Text()
                .ifAvailable(Build.VERSION_CODES.M) {
                    it.text("M is available! :)")
                }

        }.layoutFill()
    }
}

private class PreviewAdaptUIShowcaseControlFlow(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseControlFlow()
}