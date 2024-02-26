package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.imageTint
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.overlay
import io.noties.adapt.ui.overlayView
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.withAlphaComponent

@AdaptSample(
    id = "20230715153700",
    title = "Overlay with shape and view",
    description = "Add another <em>view</em>, <em>shape</em> or <em>drawable</em> as an <em>overlay</em>",
    tags = ["adapt-ui"]
)
class AdaptUIOverlaySample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            HStack {
                TextElement("LEFT", Colors.orange)
                TextElement("RIGHT", Colors.primary)
            }.indent()
                // adds a FrameLayout, to which multiple views/elements can be added
                .overlayView {
                    // this view would be added as an overlay, on-top of current views in layout
                    Image(R.drawable.ic_search_24)
                        .layout(24, 24)
                        .padding(4)
                        .layoutGravity(Gravity.center)
                        .imageTint(Colors.overlay)
                        .background {
                            Circle().fill(Colors.overlay.withAlphaComponent(0.2F))
                        }

                    // can add multiple views to overlay, this would be at bottom end
                    Text("At the bottom")
                        .layoutWrap()
                        .layoutGravity(Gravity.trailing.bottom)
                        .textColor(Colors.overlay)
                }
        }.layoutFill()
            // adds a shape or drawable overlay
            .overlay {
                Circle {
                    sizeRelative(0.1F, 0.1F, Gravity.center)
                    fill(Colors.overlay)
                }
            }
    }

    @Suppress("FunctionName")
    private fun <LP : LinearLayout.LayoutParams> ViewFactory<LP>.TextElement(
        text: String,
        @ColorInt backgroundColor: Int
    ) = Text(text)
        .layout(0, WRAP, 1F)
        .padding(16)
        .textGravity(Gravity.center)
        .textSize(21)
        .textColor { black }
        .backgroundColor(backgroundColor.withAlphaComponent(0.2F))

    @Suppress("unused")
    private val Colors.overlay: Int get() = hex("#f00")
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIOverlaySample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIOverlaySample()
}