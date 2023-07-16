package io.noties.adapt.ui.shape

import android.content.res.Resources
import io.noties.adapt.ui.util.dip
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], qualifiers = "xxhdpi")
class Dimension_Test {

    @Test
    fun exact() {
        // verify density value
        val density = Resources.getSystem().displayMetrics.density
        assertEquals(3F, density)

        val exact = Dimension.Exact(22)
        // dimension is ignored, when value is exact
        val value = exact.resolve(10001)
        assertEquals(22.dip, value)
    }

    @Test
    fun relative() {
        val relative = Dimension.Relative(0.5F)
        val value = relative.resolve(1000)
        assertEquals(500, value)
    }
}