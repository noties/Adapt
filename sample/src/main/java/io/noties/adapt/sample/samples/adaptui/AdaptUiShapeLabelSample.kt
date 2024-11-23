package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.shape.LabelShape
import io.noties.adapt.ui.shape.Rectangle

@AdaptSample(
    id = "20230526022755",
    title = "LabelShape",
    description = "Shape that draws <i>simple</i> single line text",
    tags = [Tags.adaptUi, Tags.shape, Tags.text]
)
class AdaptUiShapeLabelSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            View()
                .layout(FILL, 128)
                .background {
                    LabelShape {
                        text("Hello there! \uD83D\uDE18")
                            .textSize(20)
                            .textColor { black }
                            .textGravity { center }
                            .textBold()
//                        textRotation(45F, 1F, 0F)
                            .textShadow(4, Colors.accent)
                            .textLetterSpacing(-0.05F)

                        // add a child that would fit actual text bounds
                        Rectangle {
                            fill { orange }
                            padding(-8)
                            shadow(8)
                        }

                        padding(8)
                    }
                }
        }.layoutFill()
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUiShapeLabelSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUiShapeLabelSample()
}