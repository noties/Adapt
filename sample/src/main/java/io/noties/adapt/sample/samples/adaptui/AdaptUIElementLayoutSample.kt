package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape

@AdaptSample(
    id = "20221006152015",
    title = "AdaptUI layout attributes",
    tags = [Tags.adaptUi]
)
class AdaptUIElementLayoutSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {

                // usage of specific to LinearLayout attributes
                Header("LinearLayout attributes")
                LinearLayoutSample()

                ZStack {
                    Header("FrameLayout attributes")
                }.layout(fill, 128)
                FrameLayoutSample()
            }
        }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.Header(text: String) {
        Text(text)
            .text(text)
            .textBold()
            .textSize(16)
            .textColor { black }
            .padding(horizontal = 16, vertical = 8)
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.LinearLayoutSample() {

        fun strokeBackground() = RectangleShape {
            stroke(Colors.black, 1)
            padding(1)
        }

        VStack {

            HStack {
                Text("HStack #1")
            }.background(strokeBackground())
                .layout(fill, 0, 1F)

            ZStack {

                VStack {

                    Text("VStack")
                        .background(strokeBackground())
                        .layout(fill, 0)
                        // standalone layoutWeight is also available
                        .layoutWeight(1F)

                    HStack {

                        Text("HStack #2")
                            .layout(0, fill, 1F)

                        View()
                            .layout(12, 12)
                            .layoutMargin(4)
                            // by default HStack has CENTER_VERTICAL
                            .layoutGravity { bottom }
                            .background(strokeBackground())

                    }.layout(fill, 32)
                        .background(strokeBackground())

                }.layoutFill()
                    .layoutMargin(4)

            }.background(strokeBackground())
                .layout(fill, 0, 3F)

        }.layout(fill, 128)
            .padding(4)
            .background(strokeBackground())
    }

    @Suppress("FunctionName")
    fun ViewFactory<LayoutParams>.FrameLayoutSample() {
        ZStack {

            Text("START|TOP")
                .layoutWrap()
                .layoutGravity { leading.top }
                .layoutMargin(top = 8)

            Text("END|TOP")
                .layoutWrap()
                .layoutGravity { trailing.top }
                .layoutMargin(trailing = 8)

            Text("CENTER")
                .layoutWrap()
                .layoutGravity { center }

            Text("CENTER_HORIZONTAL|BOTTOM")
                .layoutWrap()
                .layoutGravity { bottom.center }
                .layoutMargin(bottom = 8)

        }.layout(fill, 128)
            .padding(16)
            .background {
                Rectangle {
                    stroke(Colors.black)
                    padding(8)
                }
            }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIElementLayoutSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIElementLayoutSample()
}