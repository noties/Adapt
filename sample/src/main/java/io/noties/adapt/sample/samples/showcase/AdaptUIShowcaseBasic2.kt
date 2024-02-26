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
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseBasic2 : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        // There 2 scroll containers:
        //  - VScroll (ScrollView)
        //  - HScroll (HorizontalScrollView)
        VScroll {

            // The  rule applies - ScrollView must have a single child
            VStack {

                // a row inside parent scroll-view
                HScroll {

                    // HStack and VStack accepts gravity for its children
                    HStack(Gravity.center) {
                        Text("1")
                            .padding(72, 48)
                            .backgroundColor(Color.RED)
                        Text("2")
                            .padding(72, 32)
                            .backgroundColor(Color.GREEN)
                        Text("3")
                            .padding(72, 24)
                            .backgroundColor(Color.BLUE)
                    }
                }

                Text("This will fill")
                    .layout(FILL, 0, 1F)
                    .backgroundColor(Color.YELLOW)
            }
        }.layoutFill()
            // fillViewPort
            .scrollFillViewPort()
    }
}

private class PreviewAdaptUIShowcaseBasic2(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseBasic2()
}