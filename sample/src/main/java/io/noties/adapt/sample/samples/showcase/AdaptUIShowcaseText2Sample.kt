package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.JustificationMode
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textBreakStrategy
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGradient
import io.noties.adapt.ui.element.textHint
import io.noties.adapt.ui.element.textHintColor
import io.noties.adapt.ui.element.textHyphenationFrequency
import io.noties.adapt.ui.element.textJustificationMode
import io.noties.adapt.ui.element.textShadow
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.ColorStateListBuilder

@AdaptSample(
    id = "20230520123338",
    title = "[Showcase] AdaptUI <em>Text</em> element #2",
    description = "<em>TextView</em> and siblings &mdash; <em>EditText</em>, <em>Button</em>, etc",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseText2Sample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        Text("This is some of the text ".repeat(10))
            .layout(FILL, WRAP)
            .padding(16)
            .textSize(20)
            // ColorStateListBuilder is utility to create stateful colors
            .textColor(ColorStateListBuilder.create {
                setPressed(Color.RED)
                setDefault(Color.BLACK)
            })
            // apply gradient to text
            .textGradient(
                LinearGradient.edges { leading to trailing }
                    .setColors(Color.CYAN, Color.MAGENTA, Color.GREEN, Color.BLACK)
            )
            // apply shadow
            .textShadow(8)
            .textHint("This is hint (shown when there is no text)")
            .textHintColor(Color.BLUE)
            // additional configurations
            .ifAvailable(Build.VERSION_CODES.O) {
                it.textHyphenationFrequency(HyphenationFrequency.full)
                    .textBreakStrategy(BreakStrategy.highQuality)
                    .textJustificationMode(JustificationMode.interWord)
            }
    }
}

private class PreviewAdaptUIShowcaseText2Sample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseText2Sample()
}