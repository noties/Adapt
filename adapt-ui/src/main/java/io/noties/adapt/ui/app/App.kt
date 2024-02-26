package io.noties.adapt.ui.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application

object App {
    var shared: Application
        get() = _shared ?: throw IllegalStateException("App.shared is not initialized")
        set(value) {
            _shared?.unregisterActivityLifecycleCallbacks(topMostActivityListener)
            _shared = value.also {
                it.registerActivityLifecycleCallbacks(topMostActivityListener)
            }
        }

    val topMostActivity: Activity? get() = topMostActivityListener.topMostActivity

    private var _shared: Application? = null

    @SuppressLint("StaticFieldLeak")
    private val topMostActivityListener = TopMostActivityListener()
}