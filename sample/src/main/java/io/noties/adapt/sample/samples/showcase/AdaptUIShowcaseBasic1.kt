package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import io.noties.adapt.sample.R
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.CapsuleShape
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230519160703",
    title = "[Showcase] AdaptUI basic",
    description = "Shows basic concepts and usage &mdash; <em>VStack</em>, <em>HStack</em>, <em>ZStack</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseBasic1 : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        // There are 3 basic containers:
        //  - VStack (Vertical LinearLayout)
        //  - HStack (Horizontal LinearLayout)
        //  - ZStack (FrameLayout)
        ZStack {

            // Horizontal Linearlayout
            HStack {

                // ImageView
                Image(R.drawable.ic_search_24)
                    // utility to specify layout(WRAP, WRAP)
                    .layoutWrap()
                    // ALL values in Adapt-UI is dip (density independent)
                    .padding(16)
                    .imageTint(Color.MAGENTA)
                // TextView
                Text("My text")
                    .layout(0, WRAP)
                    // layout_weight only available when inside a VStack or HStack (LinearLayout)
                    //  can be shortened to `.layout(0, WRAP, 1F)`
                    .layoutWeight(1F)
                    // layout gravity
                    .layoutGravity(Gravity.leading.center)
                    .textSize(16)
                    .textColor(Color.BLACK)

            }.layout(FILL, WRAP)
                .layoutMargin(16, 8)
                .background(CapsuleShape().fill(Color.LTGRAY))

        }.layout(FILL, FILL) // specifies layout width/height (default: FILL/WRAP)
            // utility function to specify layout(FILL, FILL)
            .layoutFill()
    }
}

private class PreviewAdaptUIShowcaseBasic1(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseBasic1()
}