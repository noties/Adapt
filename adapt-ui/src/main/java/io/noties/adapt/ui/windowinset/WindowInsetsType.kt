package io.noties.adapt.ui.windowinset

import android.os.Build
import android.view.WindowInsets
import androidx.annotation.RequiresApi

@JvmInline
value class WindowInsetsType(override val rawValue: Int) : WindowInsetsFactory {
    companion object : WindowInsetsFactory {
        // always 0, as it is root
        override val rawValue: Int
            get() = 0

        operator fun WindowInsetsType.plus(
            other: WindowInsetsType
        ) = WindowInsetsType(rawValue or other.rawValue)
    }
}

interface WindowInsetsFactory {
    val rawValue: Int

    /**
     * Backported back to SDK 23.
     * **NB!** On pre-30 includes IME (keyboard), whilst since 30 the `ime` must be handled additionally
     * and explicitly:
     * ```kotlin
     * insets = { systemBars.ime }
     * ```
     */
    val systemBars: WindowInsetsType get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowInsetsType(rawValue or WindowInsets.Type.systemBars())
    } else {
        // any value should mean systemBars, there are no alternatives
        WindowInsetsType(0)
    }

    @get:RequiresApi(Build.VERSION_CODES.R)
    val statusBar: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.statusBars())

    @get:RequiresApi(Build.VERSION_CODES.R)
    val navigationBar: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.navigationBars())

    @get:RequiresApi(Build.VERSION_CODES.R)
    val captionBar: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.captionBar())

    @get:RequiresApi(Build.VERSION_CODES.R)
    val ime: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.ime())

    @get:RequiresApi(Build.VERSION_CODES.R)
    val systemGestures: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.systemGestures())

    @get:RequiresApi(Build.VERSION_CODES.R)
    val mandatorySystemGestures: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.mandatorySystemGestures())

    @get:RequiresApi(Build.VERSION_CODES.R)
    val tappableElement: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.tappableElement())

    @get:RequiresApi(Build.VERSION_CODES.R)
    val displayCutout: WindowInsetsType get() = WindowInsetsType(rawValue or WindowInsets.Type.displayCutout())
}