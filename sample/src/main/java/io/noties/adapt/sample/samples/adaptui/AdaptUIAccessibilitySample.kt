package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.accessibilityDescription
import io.noties.adapt.ui.accessibilityLabelFor
import io.noties.adapt.ui.accessibilityTraversalBefore
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.focusable
import io.noties.adapt.ui.gradient.Gradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.util.InputType

@AdaptSample(
    id = "20221009162741",
    title = "AdaptUI - Accessibility (a11y)",
    tags = [Tags.adaptUi, Tags.accessibility]
)
class AdaptUIAccessibilitySample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {

                val inputGroup = HStack {

                    // input element that would be added later
                    lateinit var input: ViewElement<out View, *>

                    Text("Your name")
                        .padding(4)
                        .layoutWrap()
                        .accessibilityLabelFor { input }

                    Spacer()

                    input = TextInput(InputType.text.personName)
                        .textSize(16)
                        .layoutWrap()

                }.focusable(focusable = true, focusableInTouchMode = false)
                    .padding(horizontal = 16, vertical = 8)

                Text("Text")
                    .accessibilityDescription("A text view")
                    .padding(16)
                    .ifAvailable(Build.VERSION_CODES.LOLLIPOP_MR1) {
                        it.accessibilityTraversalBefore { inputGroup }
                    }

                View()
                    .layout(fill, 256)
                    .background {
                        Rectangle {
                            fill(Gradient.linear {
                                edges { top to bottom }
                                    .setColors(primary, orange)
                            })
                        }
                    }
            }
        }.layoutFill()
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIAccessibilitySample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIAccessibilitySample()
}