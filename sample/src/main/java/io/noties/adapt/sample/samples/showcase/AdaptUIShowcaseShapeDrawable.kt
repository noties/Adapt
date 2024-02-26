package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.pxToDip
import kotlin.math.roundToInt

@AdaptSample(
    id = "20230530151621",
    title = "[Showcase] ShapeDrawable usage",
    description = "stateful hotspot",
    tags = ["showcase", "adapt-ui"]
)
class AdaptUIShowcaseShapeDrawable : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            View()
                .layoutFill()
                .also { element ->
                    // radius of circle
                    val radius = 24
                    // by default circle is using Gravity.center
                    val hotspot = CircleShape()
                        .size(radius * 2, radius * 2, Gravity.leading.top)
                        .fill { orange }
                        // hidden until pressed event is received
                        .hidden(true)
                    val drawable = ShapeDrawable {
                        Rectangle {
                            fill { black }
                            add(hotspot)
                        }
                    }.stateful {
                        hotspot.hidden(!it.pressed)
                    }.hotspot { x, y ->
                        // invalidate drawable after a change occurs
                        invalidate {
                            // NB! we need to convert from pixels to dip
                            hotspot.translate(
                                x.roundToInt().pxToDip - radius,
                                y.roundToInt().pxToDip - radius
                            )
                        }
                    }
                    // still needs onClick in order to receive pressed event
                    element.background(drawable).onClick { }
                }
        }.layoutFill()
    }
}

private class PreviewAdaptUIShowcaseShape(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseShapeDrawable()

}