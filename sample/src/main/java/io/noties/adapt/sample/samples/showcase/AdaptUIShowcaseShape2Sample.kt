package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.shape.Arc
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.LabelShape
import io.noties.adapt.ui.shape.Line
import io.noties.adapt.ui.shape.Oval
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230526030500",
    title = "[Showcase] AdaptUI, Shape",
    description = "Control graphics with a <em>Shape</em> #2",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseShape2Sample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        View()
            .layoutFill()
            // everything is drawn by Shape
            .background {

                // by default shape takes all available space
                Rectangle {

                    // Arc shape
                    Arc(0F, 320F)
                        .fill(Color.RED)
                        .size(128, 128)

                    // Unlike circle, just takes all available space
                    Oval()
                        .fill(Color.BLUE)
                        .sizeRelative(0.45F, 0.45F, Gravity.trailing.top)

                    Capsule {
                        // configuration can be done here
                        fill(Colors.black)
                        sizeRelative(1F, 0.1F, Gravity.bottom)
                        padding(16)
                    }

                    Line {
                        fromRelative(0F, 0.5F)
                        toRelative(1F, 0.75F)
                        stroke(Color.GREEN, 4)
                    }
                }
            }
    }
}

private class PreviewAdaptUIShowcaseShape2Sample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseShape2Sample()
}