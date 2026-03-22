package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.green
import io.noties.adapt.sample.ui.color.red
import io.noties.adapt.sample.ui.color.yellow
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.shape.CapsuleShape
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230601100213",
    title = "[Showcase] AdaptUI shape composition",
    tags = [Tags.adaptUi, Tags.showcase, Tags.shape]
)
class AdaptUIShowcaseShapeComposition : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        View()
            .layoutFill()
            .background {
                // root rectangle, by default receives whole view bounds
                Rectangle {
                    fill { black }

                    // add a child circle
                    Circle {
                        fill { red }
                        // Circle by default uses Gravity.center, but if it has explicit bounds
                        //  it would need additional Gravity argument to be centered
                        size(48, 48, Gravity.center)
                        // padding reduces available space (goes inward)
                        padding(4)
                    }

                    // or create a shape and add manually
                    val capsule = CapsuleShape {
                        sizeRelative(0.75F, 0.2F, Gravity.center.top)
                        translate(y = 48)
                        fill { green }

                        // child would be restricted to parent bounds
                        RoundedRectangle(12) {
                            // negative padding is available
                            padding(-24)
                            fill { yellow }
                            alpha(0.25F)
                        }
                    }
                    add(capsule)
                }
            }
    }
}

@Preview
private class PreviewAdaptUIShowcaseShapeComposition(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIShowcaseShapeComposition()
}