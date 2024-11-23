package io.noties.adapt.sample.samples.flex

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.flex.Flex
import io.noties.adapt.ui.flex.flexAlignContent
import io.noties.adapt.ui.flex.flexAlignItems
import io.noties.adapt.ui.flex.flexDirection
import io.noties.adapt.ui.flex.flexJustifyContent
import io.noties.adapt.ui.flex.flexWrap
import io.noties.adapt.ui.flex.layoutFlexGrow
import io.noties.adapt.ui.flex.layoutFlexWrapBefore
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RectangleShape

@AdaptSample(
    id = "20220612133759",
    title = "Typed Flexbox layout build with adapt-ui extensions",
    tags = [Tags.adaptUi, Tags.flex, Tags.widget]
)
class FlexboxUISample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        Flex {

            Text("1")
                .background(RectangleShape {
                    fill(Color.RED)
                })
                .padding(16)

            Text("2")
                .background(RectangleShape {
                    fill(Color.GREEN)
                })
                .padding(12)
                .layoutFlexGrow(1F)

            Text("3")
                .background(RectangleShape {
                    fill(Color.YELLOW)
                })
                .layoutFlexWrapBefore(true)
                .layoutFlexGrow(1F)
                .textGravity { center }

            Text("4")
                .layoutFlexWrapBefore(true)

        }.indent()
            .flexDirection { row }
            .flexJustifyContent { center }
            .flexAlignItems { center }
            .flexAlignContent { center }
            // no arg is wrap by default
            .flexWrap()
            // or the same as:
            .flexWrap { wrap }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__FlexboxUISample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = FlexboxUISample()
}