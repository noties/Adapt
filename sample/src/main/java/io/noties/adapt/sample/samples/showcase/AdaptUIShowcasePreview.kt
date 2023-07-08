package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.image
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.preview.preview
import io.noties.adapt.ui.preview.previewBounds
import io.noties.adapt.ui.visible

@AdaptSample(
    id = "20230708163019",
    title = "[Showcase] Layout preview",
    description = "Control how elements are rendering in layout preview window",
    tags = ["showcase", "adapt-ui"]
)
class AdaptUIShowcasePreview : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text("Some text")
                .padding(4, 8, 12, 16)
                .textSize(24)
                .preview {
                    // change text that is being displayed in preview
                    it.text("This is preview text")
                }

            Image()
                // for example we have an extension to fetch an image from network
                .imageLoadAsync("https://my-image.url")
                .preview {
                    // change what is being displayed in preview
                    it.image(R.drawable.sample_avatar_1)
                }

            Text("Some invisible text")
                .visible(false)
                .preview {
                    it.visible(true)
                }

        }.layoutFill()
            .preview {
                // preview layout bounds
                it.previewBounds()
            }
    }

    private fun <V : ImageView, LP : LayoutParams> ViewElement<V, LP>.imageLoadAsync(@Suppress("UNUSED_PARAMETER") url: String) =
        onView {
            // emulate some image loading
        }
}

private class PreviewAdaptUIShowcasePreview(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcasePreview()
}