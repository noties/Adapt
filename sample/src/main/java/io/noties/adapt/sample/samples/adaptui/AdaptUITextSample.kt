package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.ExploreAutofill
import io.noties.adapt.sample.explore.ExploreAutofill.autofillEnabled
import io.noties.adapt.sample.explore.ExploreAutofill.autofillHint
import io.noties.adapt.sample.explore.ExploreAutofill.autofillRequestOnFocusWhenEmpty
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textAutoSize
import io.noties.adapt.ui.element.textBreakStrategy
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textEllipsize
import io.noties.adapt.ui.element.textGradient
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textHideIfEmpty
import io.noties.adapt.ui.element.textHint
import io.noties.adapt.ui.element.textHyphenationFrequency
import io.noties.adapt.ui.element.textImeOptions
import io.noties.adapt.ui.element.textInputType
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textOnTextChanged
import io.noties.adapt.ui.element.textSelectable
import io.noties.adapt.ui.element.textShadow
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textTypeface
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.shape.RoundedRectangleShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.state.backgroundWithState
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.InputType
import io.noties.debug.Debug

@AdaptSample(
    id = "20221008115412",
    title = "AdaptUI - Text & TextInput",
    tags = [Tags.adaptUi, Tags.text]
)
class AdaptUITextSample : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VScroll {
            VStack {

                MyTextInput()

                MyText()
            }
        }.layoutFill()
            .preview { it.previewBounds() }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<ViewGroup.MarginLayoutParams>.MyTextInput() {
        TextInput(InputType.text)
            .textSize(16)
            .textColor { black }
            .textHint("Some phone!")
            .textImeOptions {
                noExtractUi
                    .noFullScreen
                    .forceAscii
                    .actionSearch {
                        Debug.w("Triggered search action!")
                    }
            }
            .padding(horizontal = 16, vertical = 12)
            .layoutMargin(horizontal = 16, vertical = 8)
            .backgroundWithState {
                val base = RoundedRectangleShape(9) { padding(1) }
                focused = base.copy {
                    fill { hex("#0000") }
                    stroke(color = { orange })
                }
                default = base.copy {
                    fill { hex("#20000000") }
                    stroke(color = { hex("#40000000") })
                }
            }
            .ifAvailable(Build.VERSION_CODES.O) {
                it
                    .autofillEnabled(true)
                    .autofillHint(ExploreAutofill.AutofillHint.name)
                    .autofillRequestOnFocusWhenEmpty()
            }

    }

    @Suppress("FunctionName")
    private fun ViewFactory<ViewGroup.MarginLayoutParams>.MyText() {
        Text("Can be sent via constructor")
            .text("Or as an argument")
            .text(R.string.app_name)
            .text("A very very very long long text text 3456789")
            .textColor(Colors.orange)
            .textGradient(
                RadialGradient.center().setColors(
                    Colors.orange,
                    Colors.primary,
                )
            )
            .textShadow(4)
            // value is SP, so it would be automatically converted to proper value
            .textSize(42)
//            .textFont(Typeface.DEFAULT_BOLD, Typeface.BOLD)
//            .textItalic()
//            .textBold()
//            .textUnderline()
//            .textStrikeThrough()
            // if supplied text is null or empty, this TextView is going to be GONE
            .textHideIfEmpty()
            .textAllCaps()
            .textEllipsize(TextUtils.TruncateAt.END)
            .textTypeface(Typeface.DEFAULT) { bold.italic }
            .textGravity(Gravity.center)
            .textGravity { center }
            .textInputType { text.password.multiline.autoComplete }
            .textImeOptions { noExtractUi.noFullScreen.actionSearch { /*action triggered*/ } }
            .textSelectable()
//            .textSingleLine(true)
            .textMaxLines(1)
            .ifAvailable(Build.VERSION_CODES.M) {
                it.textHyphenationFrequency(HyphenationFrequency.full)
                it.textBreakStrategy(BreakStrategy.highQuality)
            }
            .ifAvailable(
                Build.VERSION_CODES.O
            ) {
                it.textAutoSize(8)
            }
            .textOnTextChanged {
                Debug.i("text:'$it'")
            }
            .layoutMargin(16)
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUITextSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUITextSample()
}