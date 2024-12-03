package io.noties.adapt.sample.samples.windowsinsets

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.salmonRed
import io.noties.adapt.sample.ui.color.steelBlue
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textHint
import io.noties.adapt.ui.element.textInputType
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Label
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.windowinset.WindowInsetsType
import io.noties.adapt.ui.windowinset.onWindowInsetsChanged

@AdaptSample(
    id = "20241203023735",
    title = "WindowInsets usage",
    description = "Usage of <tt>WindowInsets</tt> (with limited pre-30 support)",
    tags = [Tags.adaptUi]
)
class WindowInsetsSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        ZStack {
            VScroll {
                ZStack {
                    ZStack {

                        TextInput()
                            .layout(fill, wrap)
                            .textHint("Edit me...")
                            .textInputType { text }
                            .layoutMargin(top = 48)
                            .layoutMargin(horizontal = 16)
                            .layoutGravity { leading.top }
                            .padding(horizontal = 12)
                            .background {
                                RoundedRectangle(8) {
                                    fill { text.withAlphaComponent(0.1F) }
                                }
                            }
                    }.indent()
                        .layout(fill, 2048)
                        .background {
                            Rectangle {
                                fill(Gradient.create {
                                    linear {
                                        edges { top to bottom }
                                            .setColors(steelBlue.withAlphaComponent(0F), steelBlue)
                                    }
                                })
                                Label("AT THE BOTTOM") {
//                                    gravity { bottom.center }
                                    textSize(17)
                                    textGravity { bottom.center }
                                }
                            }
                        }
                }
            }.layoutFill()
        }.indent()
            .layoutFill()
            .backgroundColor { salmonRed }
            .also {

                // pre-30: systemBars + ime, after 30: systemBars only (ime requested explicitly)
                val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowInsetsType.systemBars.ime
                } else {
                    WindowInsetsType.systemBars
                }

                it.onWindowInsetsChanged(insets = { type }) {
                    // would apply all reported insets as padding, including statusBar,
                    //  which does not overlap this view (thus there will be empty space on top)
//                    it.applyWindowInsetsPadding()

                    it.padding(bottom = insetsBottom)
                }
            }
    }
}

@Preview
private class PreviewWindowInsetsSample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView: SampleView get() = WindowInsetsSample()
}