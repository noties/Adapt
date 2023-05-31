package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.shape.LabelShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230526022755",
    title = "LabelShape",
    description = "Shape that draws <i>simple</i> single line text",
    tags = ["adapt-ui", "shape", "text"]
)
class AdaptUiShapeLabelSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            View()
                .layout(FILL, 128)
                .background {
                    LabelShape {
                        text("Hello there! \uD83D\uDE18")
                            .textSize(20)
                            .textColor(Colors.black)
                            .textGravity(Gravity.center)
                            .textBold()
//                        textRotation(45F, 1F, 0F)
                            .textShadow(4, Colors.accent)
                            .textLetterSpacing(-0.05F)

                        // add a child that would fit actual text bounds
                        Rectangle {
                            fill(Colors.orange)
                            padding(-8)
                            shadow(8)
                        }

                        padding(8)
                    }
                }
        }.layoutFill()
    }
}

private class PreviewAdaptUiShapeLabelSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUiShapeLabelSample()
}