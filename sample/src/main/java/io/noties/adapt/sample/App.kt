package io.noties.adapt.sample

import android.app.Application
import android.content.Context
import io.noties.debug.AndroidLogDebugOutput
import io.noties.debug.Debug

class App : Application() {

    companion object {
        lateinit var shared: App

        fun mock(context: Context) {
            shared = App()
            shared.attachBaseContext(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        shared = this
        Debug.init(AndroidLogDebugOutput(BuildConfig.DEBUG))
    }
}