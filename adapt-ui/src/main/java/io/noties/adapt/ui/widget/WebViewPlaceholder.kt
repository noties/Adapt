package io.noties.adapt.ui.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import io.noties.adapt.ui.ViewFactory

/**
 * @since $UNRELEASED;
 */
open class WebViewPlaceholder(context: Context) : FrameLayout(context) {

    var webViewFactory: (Context) -> WebView = { WebView(it) }
    var webView: WebView? = null
    var onFailedWebViewInflation: ((WebViewPlaceholder) -> Unit)? = null
    var placeholderContent: ((ViewFactory<LayoutParams>).() -> Unit)? = null

    private val readyCallbacks = mutableListOf<(WebView) -> Unit>()

    @VisibleForTesting
    internal var poster: (delay: Long, Runnable) -> Unit = { d, r -> postDelayed(r, d) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val webView = tryToInflateWebView()
        if (webView != null) {
            onWebViewInflated(webView)
        } else {
            // add placeholder content if it is available
            placeholderContent?.also {
                ViewFactory.addChildren(this, it)
            }

            // due to the fact that webview is now a standalone (updatable) app,
            //  it can lead to crashes when this component is being updated
            triggerDelayedConstruction()
        }
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
            return webViewFactory(context)
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
            .takeIf { it > -1 }
            ?: return

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
                    onWebViewInflated(webView)
                } else {
                    if (++count == 5) {
                        // cannot create webView, trigger callback
                        onFailedWebViewInflation?.invoke(this@WebViewPlaceholder)
                    } else {
                        poster(750L, this)
                    }
                }
            }
        }
        poster(500L, runnable)
    }

    private fun onWebViewInflated(webView: WebView) {
        this@WebViewPlaceholder.webView = webView
        setup(webView)
        // would put proper layout params and everything
        replaceSelfWith(webView)
        readyCallbacks.forEach { it(webView) }
        readyCallbacks.clear()
    }
}