package io.noties.adapt.ui.element

import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.widget.WebViewPlaceholder

/**
 * @see WebViewPlaceholder
 */
@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Web(
    url: String? = null,
    webViewFactory: ((Context) -> WebView)? = null,
    onFailedWebViewInflation: ((WebViewPlaceholder) -> Unit)? = null,
    // can be used to show content whilst webView inflation is in progress
    placeholderContent: (ViewFactory<FrameLayout.LayoutParams>.() -> Unit)? = null
) = Element {
    WebViewPlaceholder(it).also { wvp ->
        webViewFactory?.also { factory -> wvp.webViewFactory = factory }
        wvp.onFailedWebViewInflation = onFailedWebViewInflation
        wvp.placeholderContent = placeholderContent
        if (url != null) {
            wvp.onWebViewReady { wv -> wv.loadUrl(url) }
        }
    }
}

// exposed for customizations not in layout
fun <V : WebViewPlaceholder, LP : LayoutParams> ViewElement<V, LP>.webOnElementReady(
    block: (ViewElement<WebView, LP>) -> Unit
) = onView {
    it.onWebViewReady { wv ->
        val element = ViewElement<WebView, LP> { wv }
        element.init(wv.context)
        element.render(block)
    }
}

fun <V : WebViewPlaceholder, LP : LayoutParams> ViewElement<V, LP>.webLoad(
    url: String
) = onView {
    it.onWebViewReady { wv -> wv.loadUrl(url) }
}

fun <V : WebViewPlaceholder, LP : LayoutParams> ViewElement<V, LP>.webSettings(
    settings: (WebSettings) -> Unit
) = onView {
    it.onWebViewReady { wv -> settings(wv.settings) }
}

fun <V : WebViewPlaceholder, LP : LayoutParams> ViewElement<V, LP>.webClient(
    client: WebViewClient
) = onView {
    it.onWebViewReady { wv -> wv.webViewClient = client }
}

fun <V : WebViewPlaceholder, LP : LayoutParams> ViewElement<V, LP>.webChromeClient(
    client: WebChromeClient
) = onView {
    it.onWebViewReady { wv -> wv.webChromeClient = client }
}