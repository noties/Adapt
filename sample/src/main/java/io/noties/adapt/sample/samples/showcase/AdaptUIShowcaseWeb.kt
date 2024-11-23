package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Progress
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.Web
import io.noties.adapt.ui.element.webChromeClient
import io.noties.adapt.ui.element.webClient
import io.noties.adapt.ui.element.webLoad
import io.noties.adapt.ui.element.webOnElementReady
import io.noties.adapt.ui.element.webSettings
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.overlay
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.util.withAlphaComponent

@AdaptSample(
    id = "20230716140149",
    title = "[Showcase] AdaptUI Web element",
    description = "Usage of a <em>WebView</em>",
    tags = [Tags.adaptUi, Tags.showcase]
)
class AdaptUIShowcaseWeb : SampleViewUI() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            // Takes care of WebView inflation (which can fail when it is being updated)
            //  Also, automatically handles lifecycle events (start/stop, pause/resume, destroy)
            Web("https://example.org")
                .layout(fill, 0, 1F)

            // More advanced
            Web(
                // callback will be delivered when WebView inflation fails
                onFailedWebViewInflation = {},
                // placeholder content is being displayed until web-view is being constructed
                placeholderContent = {
                    Progress()
                        .layoutGravity { center }
                }
            ).layout(fill, 0, 1F)
                .webSettings {
                    it.useWideViewPort = true
                }
                .webClient(object : WebViewClient() {})
                .webChromeClient(object : WebChromeClient() {})
                .webOnElementReady {
                    // element with WebView is ready
                    it.overlay {
                        Circle().fill { black.withAlphaComponent(0.2F) }
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
    override val sampleView
        get() = AdaptUIShowcaseWeb()
}