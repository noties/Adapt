package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.magenta
import io.noties.adapt.sample.ui.color.red
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Corners
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.Text
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230526030500",
    title = "[Showcase] AdaptUI, Shape #1",
    description = "<em>RectangleShape</em>, <em>CircleShape</em>, <em>TextShape</em>, <em>CornersShape</em>, <em>RoundedRectangleShape</em>",
    tags = [Tags.adaptUi, Tags.showcase]
)
class AdaptUIShowcaseShape1 : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        View()
            .layoutFill()
            // everything is drawn by Shape
            .background {

                // by default shape takes all available space
                Rectangle {
                    // modify fill color
                    fill { yellow }

                    // add a child with a factory builder helper
                    // Circle is automatically centered (unless gravity is specified)
                    Circle { fill { red } }

                    // Text is multiline shape backed by StaticLayout
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Text("I'm at the bottom")
                            .textGravity { center.bottom }
                            .textSize(20)
                    }

                    // rounded corners
                    Corners(4, 8, 16, 32)
                        .fill { black }
                        .size(128, 96, Gravity.trailing.top)
                        .padding(4)

                    // rectangle with all corners having the same radius
                    RoundedRectangle(12)
                        .fill { magenta }
                        .sizeRelative(0.5F, 0.25F, Gravity.leading.top)
                        .translate(16, 16)
                }
            }
    }
}

@Preview
private class PreviewAdaptUIShowcaseShape1(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIShowcaseShape1()
}