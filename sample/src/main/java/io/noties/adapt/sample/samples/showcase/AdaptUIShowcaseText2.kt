package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.text
import io.noties.adapt.sample.ui.color.textSecondary
import io.noties.adapt.sample.ui.text.title3
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textBreakStrategy
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGradient
import io.noties.adapt.ui.element.textGravity
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
import io.noties.adapt.ui.state.textColorWithState

@AdaptSample(
    id = "20230520123338",
    title = "[Showcase] AdaptUI <em>Text</em> element #2",
    description = "<em>TextView</em> and siblings &mdash; <em>EditText</em>, <em>Button</em>, etc",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseText2 : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        Text("This is some of the text ".repeat(10))
            .layout(fill, wrap)
            .padding(16)
            .textSize { title3 }
            .textGravity { center.horizontal }
            .textColor { text }
            .textHintColor { textSecondary }
            // apply gradient to text
            .textGradient(
                LinearGradient.edges { leading to trailing }
                    .setColors(Color.CYAN, Color.MAGENTA, Color.GREEN, Color.BLACK)
            )
            .textColorWithState {
                pressed = Color.RED
                default = Color.BLACK
            }
            // apply shadow
            .textShadow(8)
            .textHint("This is hint (shown when there is no text)")
            // additional configurations
            .ifAvailable(Build.VERSION_CODES.O) {
                it.textHyphenationFrequency { full }
                    .textBreakStrategy { highQuality }
                    .textJustificationMode { interWord }
            }
    }
}

private class PreviewAdaptUIShowcaseText2(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseText2()
}