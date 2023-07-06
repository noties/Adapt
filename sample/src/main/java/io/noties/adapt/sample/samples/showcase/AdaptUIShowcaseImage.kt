package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.util.ColorStateListBuilder

@AdaptSample(
    id = "20230601144200",
    title = "[Showcase] AdaptUI, Image",
    description = "<em>Image</em>, <em>ImageView</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseImage : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Image(R.drawable.sample_avatar_1)
                .layout(FILL, 128)
                .imageScaleType(ImageView.ScaleType.FIT_XY)

            // by default CENTER_INSIDE is used as scaleType
            Image(R.drawable.ic_search_24)
                .layout(64, 64)
                .imageTint(Colors.black)
                .background(RectangleShape().stroke(Colors.black))

            Image(R.drawable.ic_close_24)
                .layout(64, 64)
                .imageTint(ColorStateListBuilder.create {
                    setPressed(Colors.orange)
                    setDefault(Colors.black)
                })
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
    override val sampleView: SampleView
        get() = AdaptUIShowcaseImage()
}