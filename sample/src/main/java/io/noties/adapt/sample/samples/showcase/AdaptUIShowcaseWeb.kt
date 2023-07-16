package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.samples.adaptui.Colors
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Progress
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.overlay
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.adapt.ui.widget.Web
import io.noties.adapt.ui.widget.webChromeClient
import io.noties.adapt.ui.widget.webClient
import io.noties.adapt.ui.widget.webLoad
import io.noties.adapt.ui.widget.webOnElementReady
import io.noties.adapt.ui.widget.webSettings

@AdaptSample(
    id = "20230716140149",
    title = "[Showcase] AdaptUI Web element",
    description = "Usage of a <em>WebView</em>",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseWeb : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            // Takes care of WebView inflation (which can fail when it is being updated)
            //  Also, automatically handles lifecycle events (start/stop, pause/resume, destroy)
            Web("https://example.org")
                .layout(FILL, 0, 1F)

            // More advanced
            Web(
                // callback will be delivered when WebView inflation fails
                onFailedWebViewInflation = {},
                // placeholder content is being displayed until web-view is being constructed
                placeholderContent = {
                    Progress()
                        .layoutGravity(Gravity.center)
                }
            ).layout(FILL, 0, 1F)
                .webSettings {
                    it.useWideViewPort = true
                }
                .webClient(object : WebViewClient() {})
                .webChromeClient(object : WebChromeClient() {})
                .webOnElementReady {
                    // element with WebView is ready
                    it.overlay {
                        Circle().fill(Colors.black.withAlphaComponent(0.2F))
                    }
                }
                .webLoad("https://example.org")
        }.layoutFill()
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIShowcaseWeb(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIShowcaseWeb()
}