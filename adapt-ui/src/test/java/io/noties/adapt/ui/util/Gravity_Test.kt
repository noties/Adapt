package io.noties.adapt.ui.util

import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.CENTER_VERTICAL
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
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Gravity_Test {

    @Test
    fun text() {
        val inputs = listOf(
            Triple("center", Gravity.center, CENTER),
            Triple("center.vertical", Gravity.center.vertical, CENTER_VERTICAL),
            Triple("center.horizontal", Gravity.center.horizontal, CENTER_HORIZONTAL),

            Triple("leading", Gravity.leading, START),
            Triple("leading.top", Gravity.leading.top, START or TOP),
            Triple("leading.bottom", Gravity.leading.bottom, START or BOTTOM),

            Triple("top", Gravity.top, TOP),
            Triple("top.leading", Gravity.top.leading, TOP or START),
            Triple("top.trailing", Gravity.top.trailing, TOP or END),

            Triple("trailing", Gravity.trailing, END),
            Triple("trailing.top", Gravity.trailing.top, END or TOP),
            Triple("trailing.bottom", Gravity.trailing.bottom, END or BOTTOM),

            Triple("bottom", Gravity.bottom, BOTTOM),
            Triple("bottom.leading", Gravity.bottom.leading, BOTTOM or START),
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