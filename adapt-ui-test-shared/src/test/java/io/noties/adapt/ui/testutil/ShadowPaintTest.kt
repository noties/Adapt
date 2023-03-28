package io.noties.adapt.ui.testutil

import android.graphics.Color
import io.noties.adapt.ui.util.toHexString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ShadowPaintTest {

    @Test
    fun setColor() {
        // color with alpha component updates alpha
        val inputs = listOf(
            0,
            0xFFFFFF,
            0xFF000000.toInt(),
            0x80000000.toInt(),
            0x01000000,
        )

        for (input in inputs) {
            val alpha = Color.alpha(input)
            val paint = ShadowPaint()
            paint.color = input
            Assert.assertEquals(input.toHexString(), input, paint.color)
            Assert.assertEquals("alpha", alpha, paint.alpha)
        }
    }

    @Test
    fun setAlpha() {
        // set alpha updates current paint color
        val inputs = listOf(
            0,
            0xFFFFFF,
            0xFF000000.toInt(),
            0x80000000.toInt(),
            0x01000000,
        )

        for (input in inputs) {
            val paint = ShadowPaint()
            paint.color = input

            val alpha = 0x40
            paint.alpha = alpha

            Assert.assertEquals("alpha", alpha, paint.alpha)
            val expected = input.withAlpha(alpha)
            Assert.assertEquals(expected.toHexString(), paint.color.toHexString())
        }
    }
}