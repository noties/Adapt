package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Progress
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.visible
import io.noties.adapt.ui.widget.Web
import io.noties.adapt.ui.widget.WebViewPlaceholder
import io.noties.adapt.ui.widget.webChromeClient
import io.noties.adapt.ui.widget.webClient
import io.noties.adapt.ui.widget.webOnElementReady
import io.noties.debug.Debug

@AdaptSample(
    id = "20230510172026",
    title = "WebView element",
    tags = ["webview", "web"]
)
class AdaptUIWebSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        ZStack {

            val web = Web(
                "https://example.org",
                // we use web-view factory to emulate error inflation
                webViewFactory = webViewFactory(),
                onFailedWebViewInflation = ::onFailedWebViewInflation
            ) {

                // this is the placeholder content that is being displayed when first error
                //  inflating webView occurs
                ZStack {
                    Text("WebView inflation failed for the first time, please stand-by")
                        .textSize(16)
                        .textColor(Colors.black)
                        .layoutWrap()
                        .layoutGravity(Gravity.center)
                        .padding(16)
                }.layoutFill()
                    .background(Colors.orange)

            }.layoutFill()
                .webOnElementReady {
                    // process created web view
                    it.background(Colors.accent)
                }

            val progress = Progress()
                .layout(36, 36)
                .layoutGravity(Gravity.center)

//            Element {
//                ProgressBar(it, null, android.R.attr.progressBarStyleHorizontal)
//            }.layout(FILL, WRAP)
//                .onView { it.isIndeterminate = true }
//                .progressTint(Colors.orange)

            web
//                .webLoad("https://example.org")
                .webChromeClient(object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        progress.visible(newProgress != 100)
                    }

                })
                .webClient(object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        view?.evaluateJavascript("document.querySelectorAll('*')") {
                            Debug.e("s:$it")
                        }
                    }
                })

        }.layoutFill()
    }

    private fun onFailedWebViewInflation(webViewPlaceholder: WebViewPlaceholder) {
        Toast.makeText(
            webViewPlaceholder.context,
            "Failed to inflate webView",
            Toast.LENGTH_LONG
        ).show()

        webViewPlaceholder.removeAllViews()

        ViewFactory.addChildren<WebViewPlaceholder, FrameLayout.LayoutParams>(webViewPlaceholder) {
            Text("Failed to inflate WebView")
                .textSize(21)
                .textColor(Colors.primary)
                .layoutWrap()
                .layoutGravity(Gravity.center)
                .padding(16)
        }
    }

    private fun webViewFactory(): (Context) -> WebView {
        var attempts = 0
        return {
            Debug.i("attempts:$attempts")
            if (attempts++ < 3) {
                throw IllegalStateException("Too early")
            } else {
                WebView(it)
            }
        }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIWebSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIWebSample()
}