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
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230601101434",
    title = "[Showcase] AdaptUI, Shape #4",
    description = "<em>LabelShape</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseShape4 : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        View()
            .layoutFill()
            .background {
                // parent shape container
                Rectangle {
                    // LabelShape, allows drawing a single line of text
                    //  by default uses text content bounds.
                    Label {
                        text("Hello \uD83D\uDE18!")
                        // text size in SP
                        textSize(22)
                        // gravity for text inside parent bounds
                        textGravity(Gravity.center)
                        textColor(Color.WHITE)
                        // control letter spacing (negative make narrow)
                        textLetterSpacing(0.1F)
                        // apply italic style
                        textItalic()
                        // apply bold style
                        textBold()
                        // drop shadow for text
                        textShadow(4, Color.GREEN)

                        // text shapes have a difference - their children are drawn before actual text
                        //  this allows adding a background for actual text content
                        Capsule {
                            fill(Color.BLUE)
                            // negative padding to grow around text
                            padding(-16, -8)
                            shadow(8, Color.RED)
                        }
                    }
                }
            }
    }
}

private class PreviewAdaptUIShowcaseShape4(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseShape4()
}