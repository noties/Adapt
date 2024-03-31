package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textHint
import io.noties.adapt.ui.element.textInputType
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.util.InputType

@AdaptSample(
    id = "20240326221901",
    title = "Text.textInputType",
    tags = ["adapt-ui"]
)
class AdaptUITextInputTypeSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            // specify normally
            TextInput()
                .textHint("Normally")
                .layoutMargin(16)
                .textInputType(InputType.text.password)

            // specify fluently
            TextInput()
                .textHint("Fluently")
                .layoutMargin(16)
                // no need to specify "InputType"
                //  (it is the receiver => `InputType.() -> InputType`
                //  hit `CONTROL + SPACE` (on mac) to show the list of possible values
                .textInputType { text.password }

        }.layoutFill()
    }
}

private class PreviewAdaptUITextInputType(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUITextInputTypeSample()
}