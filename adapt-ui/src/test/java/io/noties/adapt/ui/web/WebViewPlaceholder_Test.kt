package io.noties.adapt.ui.web

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class WebViewPlaceholder_Test {

    private val context: Context get() = RuntimeEnvironment.getApplication()

    @Test
    fun `init - no parent`() {
        val wvp = WebViewPlaceholder(context)

        val webView: WebView = mockt()
        val settings: WebSettings = mockt()
        whenever(webView.settings).thenReturn(settings)

        wvp.webViewFactory = { webView }

        val callback: (WebView) -> Unit = mockt()
        wvp.onWebViewReady(callback)

        // triggers webview inflation
        Shadows.shadowOf(wvp).callOnAttachedToWindow()

        assertEquals(webView, wvp.webView)

        // settings
        verify(settings).javaScriptEnabled = eq(true)
        verify(settings).domStorageEnabled = eq(true)
        verify(settings).displayZoomControls = eq(false)
        verify(settings).useWideViewPort = eq(true)

        verify(callback).invoke(eq(webView))
    }

    @Test
    fun `init - replace in parent`() {
        val webView: WebView = mockt(Mockito.RETURNS_MOCKS)
        val wvp = WebViewPlaceholder(context)
        wvp.webViewFactory = { webView }

        val shadow = Shadows.shadowOf(wvp)

        val parent = mockt<ViewGroup>()
        whenever(parent.indexOfChild(eq(wvp))).thenReturn(2)
        shadow.setMyParent(parent)

        val lp: LayoutParams = mockt()
        wvp.layoutParams = lp

        shadow.callOnAttachedToWindow()

        verify(parent).indexOfChild(eq(wvp))
        verify(parent).removeViewAt(eq(2))

        verify(webView).layoutParams = eq(lp)
        verify(parent).addView(eq(webView), eq(2))
    }

    @Test
    fun `inflate failure - placeholder`() {
        val factory: (Context) -> WebView = { throw IllegalStateException("WebView unavailable") }
        val wvp = WebViewPlaceholder(context)
        wvp.webViewFactory = factory

        val placeholder: ViewFactory<FrameLayout.LayoutParams>.() -> Unit = mockt()
        wvp.placeholderContent = placeholder

        Shadows.shadowOf(wvp).callOnAttachedToWindow()

        verify(placeholder).invoke(any())
    }

    @Test
    fun `inflate failure`() {
        // hm.. how to emulate the holder post delayed?
        val factory: (Context) -> WebView = { throw IllegalStateException("WebView unavailable") }
        val wvp = WebViewPlaceholder(context)
        wvp.webViewFactory = factory

        lateinit var runnable: Runnable
        val poster: (Long, Runnable) -> Unit = { _, r -> runnable = r }
        wvp.poster = poster

        val onFailure: (WebViewPlaceholder) -> Unit = mockt()
        wvp.onFailedWebViewInflation = onFailure

        Shadows.shadowOf(wvp).callOnAttachedToWindow()

        var count = 0

        while (true) {
            runnable.run()

            count += 1

            if (count == 5) {
                // at this point it must be triggered
                verify(onFailure).invoke(eq(wvp))
                break
            } else {
                verify(onFailure, never()).invoke(any())
            }
        }
    }

    @Test
    fun element() {
        val url = "data:nah"

        val webView: WebView = mockt(Mockito.RETURNS_MOCKS)
        val factory: (Context) -> WebView = { webView }
        val onFailure: (WebViewPlaceholder) -> Unit = {}
        val placeholder: ViewFactory<FrameLayout.LayoutParams>.() -> Unit = {}

        val wvp = ViewFactory.createView(context) {
            Web(
                url,
                factory,
                onFailure,
                placeholder
            )
        } as WebViewPlaceholder

        assertEquals(factory, wvp.webViewFactory)
        assertEquals(onFailure, wvp.onFailedWebViewInflation)
        assertEquals(placeholder, wvp.placeholderContent)

        Shadows.shadowOf(wvp).callOnAttachedToWindow()

        verify(webView).loadUrl(eq(url))
    }

    @Test
    fun `element - webLoad`() {
        val input = "no-way$1223"
        newWebViewPlaceholderElement()
            .webLoad(input)
            .renderWebView {
                verify(it).loadUrl(eq(input))
            }
    }

    @Test
    fun `element - webSettings`() {
        val settings: WebSettings = mockt()

        lateinit var arg: WebSettings

        newWebViewPlaceholderElement()
            .onView { whenever(it.webView!!.settings).thenReturn(settings) }
            .webSettings {
                arg = it
            }
            .renderWebView {
                verify(it, atLeast(1)).settings
                assertEquals(settings, arg)
            }
    }

    @Test
    fun `element - webClient`() {
        val webClient: WebViewClient = mockt()

        newWebViewPlaceholderElement()
            .webClient(webClient)
            .renderWebView {
                verify(it).webViewClient = eq(webClient)
            }
    }

    @Test
    fun `element - webChromeClient`() {
        val webChromeClient: WebChromeClient = mockt()

        newWebViewPlaceholderElement()
            .webChromeClient(webChromeClient)
            .renderWebView {
                verify(it).webChromeClient = eq(webChromeClient)
            }
    }

    private fun newWebViewPlaceholderElement(): ViewElement<WebViewPlaceholder, LayoutParams> {
        val webView: WebView = mockt(Mockito.RETURNS_MOCKS)
        return ViewElement<WebViewPlaceholder, LayoutParams> {
            WebViewPlaceholder(it).also { wvp ->
                wvp.webViewFactory = { webView }
                Shadows.shadowOf(wvp).callOnAttachedToWindow()
            }
        }.also { it.init(context) }
    }

    private fun <LP : LayoutParams> ViewElement<WebViewPlaceholder, LP>.renderWebView(
        block: (WebView) -> Unit
    ) {
        render()
        block(view.webView!!)
    }
}