package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230520005529",
    title = "[Showcase] AdaptUI control flow",
    description = "<em>For loop</em>, <em>forEach</em>, <em>if-else</em>, <em>while</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseControlFlowSample : AdaptUISampleView() {
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

            var i = 7
            while (i < 10) {
                Text("while:$i")
                    .textGravity(Gravity.center.horizontal)
                    .textAllCaps()
                    .background(Color.YELLOW)
                i += 1
            }

        }.layoutFill()
    }
}

private class PreviewAdaptUIShowcaseControlFlowSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseControlFlowSample()
}