package io.noties.adapt.ui.testutil

import io.noties.adapt.ui.util.toHexString
import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.annotation.Config

@Suppress("ClassName")
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ColorUtil_Test {

    @Test
    fun test() {
        val input = 0xFFFFFFFF.toInt()
        val actual = input.withAlpha(0x80)
        assertEquals("#80FFFFFF", actual.toHexString())
    }
}