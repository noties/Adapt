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
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.LabelShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230526030500",
    title = "[Showcase] AdaptUI, Shape",
    description = "Control graphics with a <em>Shape</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseShape1Sample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        View()
            .layoutFill()
            // everything is drawn by Shape
            .background {

                // by default shape takes all available space
                Rectangle {
                    // modify fill color
                    fill(Color.YELLOW)

                    // add a child with a factory builder helper
                    // Circle is automatically centered (unless gravity is specified)
                    Circle {
                        fill(Color.RED)
                    }

                    // Text is multiline shape backed by StaticLayout
                    Text("I'm at the bottom")
                        .textGravity(Gravity.center.bottom)
                        .textSize(20)

                    // rounded corners
                    Corners(4, 8, 16, 32)
                        .fill(Color.BLACK)
                        .size(128, 96, Gravity.trailing.top)
                        .padding(4)

                    // rectangle with all corners having the same radius
                    RoundedRectangle(12)
                        .fill(Color.MAGENTA)
                        .sizeRelative(0.5F, 0.25F, Gravity.leading.top)
                        .translate(16, 16)
                }
            }
    }
}

private class PreviewAdaptUIShowcaseShape1Sample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseShape1Sample()
}