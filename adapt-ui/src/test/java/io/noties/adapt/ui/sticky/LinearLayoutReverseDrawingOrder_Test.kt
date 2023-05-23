package io.noties.adapt.ui.sticky

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class LinearLayoutReverseDrawingOrder_Test {

    @Test
    fun order() {
        val view = LinearLayoutReverseDrawingOrder(RuntimeEnvironment.getApplication())
        val count = 11
        var i = 0
        for (position in 10 downTo 0) {
            assertEquals(i, view.getChildDrawingOrder(count, position))
            i += 1
        }
    }
}