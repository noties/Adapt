package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.castView
import io.noties.adapt.ui.element.Inflated
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.inflatedView
import io.noties.adapt.ui.element.stackGravity
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.foreground
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.ifCastView
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.hex

@AdaptSample(
    id = "20230615000044",
    title = "[Explore] AdaptUI <em>Inflate</em> element",
    description = "Inflate view from XML and use in AdaptUI",
    tags = ["adapt-ui"]
)
class AdaptUIInflateSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {
            Inflated(R.layout.item_plain)
                .inflatedView(R.id.letter_view) {
                    it
                        .textColor(hex("#208F00FF"))
                        .text("42")
                        .background {
                            Circle { fill(Colors.orange) }
                        }
                }
                .inflatedView(R.id.title_view) {
                    it.text("Inflated view")
                        .padding(16)
//                        .padding(top = 0, bottom = 0)
                        .preview {
//                            it.previewLayout()
                            it.text("InPreview")
                        }
                }
                .ifAvailable(Build.VERSION_CODES.M) {
                    it.foreground { Capsule() }
                }
//                .inflatedView<View, _>(R.id.adapt_internal) {
//                    it.background(Colors.black)
//                }
                .layoutFill()
                .background(Colors.yellow)
                .ifCastView(LinearLayout::class) {
                    it.background(Colors.accent)
                }
                .castView(LinearLayout::class)
                .stackGravity(Gravity.center.vertical)
        }.layoutFill()
            .preview {
                it.previewBounds()
            }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIInflateSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIInflateSample()
}
