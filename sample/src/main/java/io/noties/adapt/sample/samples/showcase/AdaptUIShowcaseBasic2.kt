package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.emeraldGreen
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.HScroll
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.scrollFillViewPort
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230520002917",
    title = "[Showcase] AdaptUI basic2",
    description = "Shows basic concepts and usage &mdash; <em>VScroll</em>, <em>HScroll</em>",
    tags = [Tags.adaptUi, Tags.showcase]
)
class AdaptUIShowcaseBasic2 : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        // There 2 scroll containers:
        //  - VScroll (ScrollView)
        //  - HScroll (HorizontalScrollView)
        VScroll {

            // The  rule applies - ScrollView must have a single child
            VStack {

                // a row inside parent VStack
                HScroll {

                    // HStack and VStack accepts gravity for its children
                    HStack(Gravity.center) {
                        Text("1")
                            .padding(72, 48)
                            .backgroundColor { salmonRed }
                        Text("2")
                            .padding(72, 32)
                            .backgroundColor { emeraldGreen }
                        Text("3")
                            .padding(72, 24)
                            .backgroundColor { steelBlue }
                    }
                }

                Text("This will fill")
                    .layout(fill, 0, 1F)
                    .backgroundColor { yellow }
            }
        }.layoutFill()
            // fillViewPort
            .scrollFillViewPort()
    }
}

@Preview
private class PreviewAdaptUIShowcaseBasic2(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIShowcaseBasic2()
}