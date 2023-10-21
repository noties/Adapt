package io.noties.adapt.ui.util

import io.noties.adapt.ui.testutil.assertDensity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ResourcesUtil_Test {

    @Test
    @Config(qualifiers = "xhdpi")
    fun dip() {
        // using Resources.getSystem
        val density = 2F
        assertDensity(density)

        val input = 2
        val result = input.dip
        Assert.assertEquals((input * density + 0.5F).toInt(), result)
    }

    @Test
    fun `dip - density`() {
        val inputs = listOf(
            0.1F,
            0.235F,
            0.9F,
            1F,
            2F,
            3F,
            10F,
            777F,
            100026F
        )

        for (density in inputs) {
            val values = listOf(1, 2, 3, 10, 1000, 235734)
            for (value in values) {
                val expected = (value * density + 0.5F).toInt()
                val result = value.dip(density)
                Assert.assertEquals("density:$density value:$value", expected, result)
            }
        }
    }
}