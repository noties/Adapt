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
    fun gravityValue() {

        val inputs = listOf(
            Gravity.center to CENTER,
            Gravity.center.leading to (CENTER or START),
            Gravity.center.top to (CENTER or TOP),
            Gravity.center.trailing to (CENTER or END),
            Gravity.center.bottom to (CENTER or BOTTOM),

            Gravity.leading to START,
            Gravity.leading.top to (START or TOP),
            Gravity.leading.center to (START or CENTER),
            Gravity.leading.bottom to (START or BOTTOM),

            Gravity.top to TOP,
            Gravity.top.leading to (TOP or START),
            Gravity.top.center to (TOP or CENTER),
            Gravity.top.trailing to (TOP or END),

            Gravity.trailing to END,
            Gravity.trailing.top to (END or TOP),
            Gravity.trailing.center to (END or CENTER),
            Gravity.trailing.bottom to (END or BOTTOM),

            Gravity.bottom to BOTTOM,
            Gravity.bottom.leading to (BOTTOM or START),
            Gravity.bottom.center to (BOTTOM or CENTER),
            Gravity.bottom.trailing to (BOTTOM or END),
        )

        for (input in inputs) {
            assertEquals(
                input.first.toString(),
                input.first.gravityValue,
                input.second,
            )
        }
    }

    @Test
    fun equals() {
        val inputs = listOf(
            Gravity.leading.top to Gravity.top.leading,
            Gravity.leading.center to Gravity.center.leading,
            Gravity.leading.bottom to Gravity.bottom.leading,

            Gravity.top.leading to Gravity.leading.top,
            Gravity.top.center to Gravity.center.top,
            Gravity.top.trailing to Gravity.trailing.top,

            Gravity.trailing.top to Gravity.top.trailing,
            Gravity.trailing.center to Gravity.center.trailing,
            Gravity.trailing.bottom to Gravity.bottom.trailing,

            Gravity.bottom.leading to Gravity.leading.bottom,
            Gravity.bottom.center to Gravity.center.bottom,
            Gravity.bottom.trailing to Gravity.trailing.bottom,

            Gravity.center.leading to Gravity.leading.center,
            Gravity.center.top to Gravity.top.center,
            Gravity.center.trailing to Gravity.trailing.center,
            Gravity.center.bottom to Gravity.bottom.center
        )

        for (input in inputs) {
            assertEquals(input.first, input.second)
            // check that raw equals with the same gravityValue
            assertEquals(input.first, Gravity.raw(input.first.gravityValue))
        }
    }
}