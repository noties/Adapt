package io.noties.adapt.ui.preview

import android.app.Application
import android.content.Context
import io.noties.adapt.ui.app.App

open class PreviewApplication(context: Context): Application() {

    companion object {
        fun install(context: Context) {
            App.shared = PreviewApplication(context)
        }
    }

    init {
        attachBaseContext(context)
    }
}