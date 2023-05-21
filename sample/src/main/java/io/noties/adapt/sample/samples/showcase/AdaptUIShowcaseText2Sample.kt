package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textBold

@AdaptSample(
    id = "20230520123338",
    title = "[Showcase] AdaptUI <em>Text</em> element #2",
    description = "<em>TextView</em> and siblings &mdash; <em>EditText</em>, <em>Button</em>, etc",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseText2Sample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        Text("This is some of the text")
            .textBold()
    }
}

private class PreviewAdaptUIShowcaseText2Sample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseText2Sample()
}