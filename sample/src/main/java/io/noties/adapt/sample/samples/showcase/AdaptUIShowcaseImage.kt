package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.state.imageTintWithState

@AdaptSample(
    id = "20230601144200",
    title = "[Showcase] AdaptUI, Image",
    description = "<em>Image</em>, <em>ImageView</em>",
    tags = [Tags.adaptUi, Tags.showcase]
)
class AdaptUIShowcaseImage : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Image(R.drawable.sample_avatar_1)
                .layout(fill, 128)
                .imageScaleType { fitXY }

            // by default FIT_CENTER is used as scaleType
            Image(R.drawable.ic_search_24)
                .layout(64, 64)
                .imageTint { black }
                .background {
                    Rectangle { stroke(color = { black }) }
                }

            Image(R.drawable.ic_close_24)
                .layout(64, 64)
                .imageTintWithState {
                    pressed = orange
                    default = black
                }
                .background(RectangleShape().stroke(Colors.black))
                .ifAvailable(Build.VERSION_CODES.M) {
                    it.foregroundDefaultSelectable()
                }
                .onClick { }
        }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIShowcaseImage(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIShowcaseImage()
}