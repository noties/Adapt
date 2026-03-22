package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.isRunningScreenshotTests
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
    tags = [Tags.adaptUi, Tags.showcase]
)
class AdaptUIShowcaseControlFlow : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        // NB! code is executed ONCE when run
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

            if (isRunningScreenshotTests) {
                Text("if, current time is even")
                    .textColor { salmonRed }
                Text("else, current time is odd")
                    .textColor { steelBlue }
            } else {
                // if check for some condition
                if (System.currentTimeMillis() % 2 == 0L) {
                    Text("if, current time is even")
                        .textColor { salmonRed }
                } else {
                    Text("else, current time is odd")
                        .textColor { steelBlue }
                }
            }

            // version check
            Text()
                .ifAvailable(Build.VERSION_CODES.M) {
                    it.text("M is available! :)")
                }

        }.layoutFill()
    }
}

@Preview
private class PreviewAdaptUIShowcaseControlFlow(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIShowcaseControlFlow()
}