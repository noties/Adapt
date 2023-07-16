package io.noties.adapt.ui.util

import android.graphics.Typeface
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class TypefaceStyle_Test {

    @Test
    fun values() {
        val inputs = listOf(
            Typeface.NORMAL to TypefaceStyle.normal,
            Typeface.BOLD to TypefaceStyle.bold,
            Typeface.ITALIC to TypefaceStyle.italic,
            Typeface.BOLD_ITALIC to TypefaceStyle.bold.italic,
            Typeface.BOLD_ITALIC to TypefaceStyle.italic.bold,
            99 to TypefaceStyle(99)
        )

        for ((expected, style) in inputs) {
            Assert.assertEquals(style.toString(), expected, style.value)
            Assert.assertEquals(style, TypefaceStyle(expected))
        }
    }
}