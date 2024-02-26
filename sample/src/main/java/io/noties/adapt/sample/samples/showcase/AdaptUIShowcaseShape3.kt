package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.shape.Asset
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230530152155",
    title = "[Showcase] AdaptUI, Shape #3",
    description = "<em>AssetShape</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseShape3 : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            View()
                .layoutFill()
                .background {
                    // asset accepts Drawable
                    Asset(
                        RippleDrawable(
                            ColorStateList.valueOf(Colors.orange),
                            null,
                            // actual shape is ignored, ripple just uses
                            //   it to clip rect bounds
                            RectangleShape().newDrawable()
                        )
                    ) {
                        size(128, 128, Gravity.center)

                        // a child of Asset will receive parent bounds
                        Rectangle {
                            stroke(Colors.black, 1)
                        }
                    }
                }
                .onClick { }
        }.layoutFill()
    }
}

private class PreviewAdaptUIShowcaseShape3(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseShape3()
}