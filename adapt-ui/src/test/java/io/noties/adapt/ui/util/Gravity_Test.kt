package io.noties.adapt.ui.util

import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.Gravity.TOP
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
// minSdk = 21, maxSdk = 30, all sdks seem to be correct (all tests pass)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Gravity_Test {

    @Test
    fun text() {

        val inputs = listOf(
            Triple("center", Gravity.center, CENTER),
            Triple("center.leading", Gravity.center.leading, CENTER or START),
            Triple("center.top", Gravity.center.top, CENTER or TOP),
            Triple("center.trailing", Gravity.center.trailing, CENTER or END),
            Triple("center.bottom", Gravity.center.bottom, CENTER or BOTTOM),

            Triple("leading", Gravity.leading, START),
            Triple("leading.top", Gravity.leading.top, START or TOP),
            Triple("leading.center", Gravity.leading.center, START or CENTER),
            Triple("leading.bottom", Gravity.leading.bottom, START or BOTTOM),

            Triple("top", Gravity.top, TOP),
            Triple("top.leading", Gravity.top.leading, TOP or START),
            Triple("top.center", Gravity.top.center, TOP or CENTER),
            Triple("top.trailing", Gravity.top.trailing, TOP or END),

            Triple("trailing", Gravity.trailing, END),
            Triple("trailing.top", Gravity.trailing.top, END or TOP),
            Triple("trailing.center", Gravity.trailing.center, END or CENTER),
            Triple("trailing.bottom", Gravity.trailing.bottom, END or BOTTOM),

            Triple("bottom", Gravity.bottom, BOTTOM),
            Triple("bottom.leading", Gravity.bottom.leading, BOTTOM or START),
            Triple("bottom.center", Gravity.bottom.center, BOTTOM or CENTER),
            Triple("bottom.trailing", Gravity.bottom.trailing, BOTTOM or END),
        )

        for (input in inputs) {
            assertEquals(
                input.first,
                input.third,
                input.second.gravityValue,
            )
        }
    }
}