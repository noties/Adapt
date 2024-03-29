package io.noties.adapt.ui.util

import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.CENTER_VERTICAL
import android.view.Gravity.END
import android.view.Gravity.LEFT
import android.view.Gravity.RIGHT
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
            Gravity.center.horizontal to (CENTER_HORIZONTAL),
            Gravity.center.vertical to (CENTER_VERTICAL),

            Gravity.leading to START,
            Gravity.leading.top to (START or TOP),
            Gravity.leading.center to (START or CENTER_VERTICAL),
            Gravity.leading.bottom to (START or BOTTOM),

            Gravity.top to TOP,
            Gravity.top.leading to (TOP or START),
            Gravity.top.center to (TOP or CENTER_HORIZONTAL),
            Gravity.top.trailing to (TOP or END),

            Gravity.trailing to END,
            Gravity.trailing.top to (END or TOP),
            Gravity.trailing.center to (END or CENTER_VERTICAL),
            Gravity.trailing.bottom to (END or BOTTOM),

            Gravity.bottom to BOTTOM,
            Gravity.bottom.leading to (BOTTOM or START),
            Gravity.bottom.center to (BOTTOM or CENTER_HORIZONTAL),
            Gravity.bottom.trailing to (BOTTOM or END),
        )

        for (input in inputs) {
            assertEquals(
                toString(input.first),
                input.first.value,
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
            assertEquals(input.first, Gravity(input.first.value))
        }
    }

    @Test
    fun hasLeading() {
        val inputs = listOf(
            Gravity.leading,
            Gravity.leading.top,
            Gravity.leading.bottom,
            Gravity.leading.center,
            Gravity(LEFT),
            Gravity(START),
            Gravity(LEFT or CENTER_VERTICAL),
            Gravity(START or CENTER_VERTICAL)
        )

        for (input in inputs) {
            assertEquals(toString(input), true, input.hasLeading())
        }

        allPredefinedGravities()
            .filter { !inputs.contains(it) }
            .forEach { input ->
                assertEquals(toString(input), false, input.hasLeading())
            }
    }

    @Test
    fun hasTop() {
        val inputs = listOf(
            Gravity.top,
            Gravity.top.leading,
            Gravity.top.trailing,
            Gravity.top.center,
            Gravity(TOP),
            Gravity(TOP or START),
            Gravity(TOP or CENTER),
        )

        for (input in inputs) {
            assertEquals(toString(input), true, input.hasTop())
        }

        allPredefinedGravities()
            .filter { !inputs.contains(it) }
            .forEach { input ->
                assertEquals(toString(input), false, input.hasTop())
            }
    }

    @Test
    fun hasTrailing() {
        val inputs = listOf(
            Gravity.trailing,
            Gravity.trailing.top,
            Gravity.trailing.bottom,
            Gravity.trailing.center,
            Gravity(END),
            Gravity(RIGHT),
            Gravity(END or TOP),
            Gravity(RIGHT or CENTER),
        )

        for (input in inputs) {
            assertEquals(toString(input), true, input.hasTrailing())
        }

        allPredefinedGravities()
            .filter { !inputs.contains(it) }
            .forEach { input ->
                assertEquals(toString(input), false, input.hasTrailing())
            }
    }

    @Test
    fun hasBottom() {
        val inputs = listOf(
            Gravity.bottom,
            Gravity.bottom.leading,
            Gravity.bottom.trailing,
            Gravity.bottom.center,
            Gravity(BOTTOM),
            Gravity(BOTTOM or START),
            Gravity(BOTTOM or CENTER)
        )

        for (input in inputs) {
            assertEquals(toString(input), true, input.hasBottom())
        }

        allPredefinedGravities()
            .filter { !inputs.contains(it) }
            .forEach { input ->
                assertEquals(toString(input), false, input.hasBottom())
            }
    }

    @Test
    fun hasCenter() {
        val inputs = listOf(
            Gravity.center,
            Gravity.center.horizontal,
            Gravity.center.vertical,
            Gravity.center.leading,
            Gravity.center.trailing,
            Gravity.center.top,
            Gravity.center.bottom,
            Gravity(CENTER),
            Gravity(CENTER_VERTICAL or CENTER_HORIZONTAL),
            Gravity(CENTER or TOP),
            Gravity(CENTER or END),
            Gravity(CENTER or LEFT),
        )

        for (input in inputs) {
            assertEquals(toString(input), true, input.hasCenter())
        }

        allPredefinedGravities()
            .filter { !inputs.contains(it) }
            .forEach { input ->
                assertEquals(toString(input), false, input.hasCenter())
            }
    }

    private fun allPredefinedGravities() = listOf(
        Gravity.center,
        Gravity.center.leading,
        Gravity.center.top,
        Gravity.center.trailing,
        Gravity.center.bottom,
        Gravity.center.horizontal,
        Gravity.center.vertical,

        Gravity.leading,
        Gravity.leading.top,
        Gravity.leading.center,
        Gravity.leading.bottom,

        Gravity.top,
        Gravity.top.leading,
        Gravity.top.center,
        Gravity.top.trailing,

        Gravity.trailing,
        Gravity.trailing.top,
        Gravity.trailing.center,
        Gravity.trailing.bottom,

        Gravity.bottom,
        Gravity.bottom.leading,
        Gravity.bottom.center,
        Gravity.bottom.trailing,
    )

    private val toString: (Gravity) -> String = kotlin.run {
        val method = try {
            android.view.Gravity::class.java.getDeclaredMethod(
                "toString",
                java.lang.Integer.TYPE
            ).also { it.isAccessible = true }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
            null
        }

        { gravity ->
            val text = method?.invoke(null, gravity.value)?.toString() ?: ""
            "Gravity(${gravity.value} = [$text])"
        }
    }
}