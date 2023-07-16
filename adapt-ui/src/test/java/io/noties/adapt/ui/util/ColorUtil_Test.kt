package io.noties.adapt.ui.util

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ColorUtil_Test {

    @Test
    fun `hex invalid`() {
        val inputs = listOf(
            "",
            "#",
            "####",
            "#f",
            "#01",
            "#01234",
            "#0123456",
            "012345",
            "0123",
            "01234",
            "0123#",
            "#whatever",
            "#   ",
            "#fbg"
        )

        for (input in inputs) {
            try {
                hex(input)
            } catch (t: IllegalStateException) {
                Assert.assertTrue(
                    t.message,
                    t.message!!.contains("Invalid hex color format:'$input'")
                )
            }
        }
    }

    @Test
    fun hex() {
        val inputs = listOf<Pair<String, Int>>(
            "#f00" to 0xFFff0000.toInt(),
            "#a123" to 0xaa112233.toInt(),
            "#ffbbcc" to 0xFFffbbcc.toInt(),
            "#40ffaaee" to 0x40ffaaee.toInt()
        )

        for ((hex, color) in inputs) {
            Assert.assertEquals(
                hex,
                hex(hex),
                color
            )
        }
    }
}