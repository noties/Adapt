package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.ExploreAutofill
import io.noties.adapt.sample.explore.ExploreAutofill.autofillEnabled
import io.noties.adapt.sample.explore.ExploreAutofill.autofillHint
import io.noties.adapt.sample.explore.ExploreAutofill.autofillRequestOnFocusWhenEmpty
import io.noties.adapt.sample.explore.ExplorePreviewDrawBounds.previewDrawBounds
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
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
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textOnTextChanged
import io.noties.adapt.ui.element.textSelectable
import io.noties.adapt.ui.element.textShadow
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.gradient.RadialGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RoundedRectangleShape
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.shape.copy
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.ImeOptions
import io.noties.adapt.ui.util.InputType
import io.noties.adapt.ui.util.hex
import io.noties.debug.Debug

@AdaptSample(
    id = "20221008115412",
    title = "AdaptUI - Text & TextInput",
    tags = ["adapt-ui", "ui-text", "ui-text-input"]
)
class AdaptUITextSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        val child = ViewFactory.createView(view.context) {
            VScroll {
                VStack {

                    MyTextInput()

                    MyText()
                }
            }.layoutFill()
                .previewDrawBounds()
        }
        (view as ViewGroup).addView(child)
    }

    @Suppress("FunctionName")
    private fun ViewFactory<ViewGroup.MarginLayoutParams>.MyTextInput() {
        TextInput(InputType.text)
            .textSize(16)
            .textColor(Colors.black)
            .textHint("Some phone!")
//            .textInputType(ExploreEditorInfo.InputType.text.uri.noSuggestions.capWords)
//            .textImeOptions(ExploreEditorInfo.ImeOptions.actionGo.noExactUi)
            .textImeOptions(ImeOptions.actionSearch.noExactUi) { view, _ ->
                val text = view.text.toString()
                Debug.e("triggered action! view:'$text'")
                true
            }
//            .onView {
////                it.setImeActionLabel("WHAT", R.id.divider_overlay_drawable)
//                it.setOnEditorActionListener { v, actionId, event ->
//                    val eq = ImeOptions(actionId) == ImeOptions.actionSearch
//                    Debug.i("actionId=${actionId}, event=${event} eq:$eq")
//                    true
//                }
//            }
            .padding(horizontal = 16, vertical = 12)
            .layoutMargin(horizontal = 16, vertical = 8)
            .background(StatefulShape.drawable {
                val base = RoundedRectangleShape(9)
                setFocused(base.copy {
                    stroke(Colors.orange)
                    fill(hex("#0000"))
                    padding(1)
                })
                setDefault(base.copy {
                    fill(hex("#20000000"))
                    stroke(hex("#40000000"))
                    padding(1)
                })
            })
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
            .textGravity(Gravity.center)
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
    override val sampleView: SampleView
        get() = AdaptUITextSample()
}