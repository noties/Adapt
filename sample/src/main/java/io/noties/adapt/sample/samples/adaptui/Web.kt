package io.noties.adapt.sample.samples.adaptui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element

open class WebViewPlaceholder(context: Context) : View(context) {

    var webView: WebView? = null

    private val readyCallbacks = mutableListOf<(WebView) -> Unit>()

    init {
        // TODO: maybe we can postpone until attached (or after onPreDraw/onDraw,
        //  so some content is being displayed when UI hangs to inflate a webview
        val webView = tryToInflateWebView()
        if (webView != null) {
            // no need to trigger callbacks, as we have created in constructor
            //  callbacks should be empty at this point
            this.webView = webView
            setup(webView)
        } else {
            // TODO: actually here we can also display some placeholder widget or content
            //  so, as webview is not visible, it is a good idea to actually place something (like a progress view)
            //  The same for when we are tryuing to inflate and when inflate fails, ability to retry?

            // due to the fact that webview is now a standalone (updatable) app,
            //  it can lead to crashes when this component is being updated
            triggerDelayedConstruction()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // if available now, do replace in parent
        val webView = this.webView ?: return

        replaceSelfWith(webView)
    }

    fun onWebViewReady(block: (WebView) -> Unit) {
        val webView = this.webView
        if (webView != null) {
            block(webView)
        } else {
            readyCallbacks.add(block)
        }
    }

    protected open fun tryToInflateWebView(): WebView? {
        try {
            return WebView(context)
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
        return null
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected open fun setup(webView: WebView) {
        // register listener for onStart/Stop, Pause/Resume
        webView.settings.also {
            // would it be reasonable to enabled them by default? or have any default settings?
            it.javaScriptEnabled = true
            it.domStorageEnabled = true
//            it.databaseEnabled = true // changes for all web views in a process
            it.displayZoomControls = false
            it.useWideViewPort = true
        }

        val activity = currentActivity() ?: return
        val application = activity.application
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit

            override fun onActivityResumed(a: Activity) {
                if (activity == a) {
                    webView.onResume()
                }
            }

            override fun onActivityPaused(a: Activity) {
                if (activity == a) {
                    webView.onPause()
                }
            }

            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            override fun onActivityDestroyed(a: Activity) {
                if (activity == a) {
                    webView.destroy()
                    application.unregisterActivityLifecycleCallbacks(this)
                }
            }
        })
    }

    private fun currentActivity(): Activity? {
        var context: Context? = this.context
        while (context != null) {
            if (context is Activity) {
                return context
            }
            if (context is ContextWrapper) {
                context = context.baseContext
            } else {
                break
            }
        }
        return null
    }

    private fun replaceSelfWith(webView: WebView) {
        // if we have no parent, we cannot proceed
        val parent = this.parent as? ViewGroup ?: return

        val index = parent.indexOfChild(this)
        val layoutParams = this.layoutParams

        parent.removeViewAt(index)

        webView.layoutParams = layoutParams
        parent.addView(webView, index)
    }

    private fun triggerDelayedConstruction() {
        val runnable: Runnable = object : Runnable {

            var count = 0

            override fun run() {
                val webView = tryToInflateWebView()
                if (webView != null) {
                    this@WebViewPlaceholder.webView = webView
                    setup(webView)
                    // would put proper layout params and everything
                    replaceSelfWith(webView)
                    readyCallbacks.forEach { it(webView) }
                    readyCallbacks.clear()
                } else {
                    if (count == 5) {
                        // cannot create webView, should we throw?
                    } else {
                        postDelayed(this, 750L)
                    }
                }
            }
        }
        postDelayed(runnable, 500L)
    }
}

// TODO: what about background? we cannot dircetly apply one to webView?

@Suppress("FunctionName")
fun <LP : LayoutParams> ViewFactory<LP>.Web(
    url: String? = null
) = Element {
    WebViewPlaceholder(it).also { wvp ->
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