package io.noties.adapt.ui.util

import android.content.res.Resources
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import io.noties.adapt.ui.newElement
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class DrawableState_Test {

    @Test
    fun onDrawableStateChange() {
        val ref = kotlin.run {
            class Ref(var view: View? = null, var stateSet: DrawableStateSet? = null)
            Ref()
        }

        val vto = mockt<ViewTreeObserver>().also {
            whenever(it.isAlive).thenReturn(true)
        }

        newElement()
            .also { it.render() }
            .also {
                whenever(it.view.viewTreeObserver).thenReturn(vto)
            }
            .onDrawableStateChange { v, s ->
                ref.view = v
                ref.stateSet = s
            }
            .renderView {
                Assert.assertNull(ref.view)
                Assert.assertNull(ref.stateSet)

                val listener = kotlin.run {
                    val captor = ArgumentCaptor.forClass(OnPreDrawListener::class.java)
                    verify(vto).addOnPreDrawListener(captor.capture())
                    captor.value
                }

                val firstState = intArrayOf(android.R.attr.state_focused)
                whenever(this.drawableState).thenReturn(firstState)

                listener.onPreDraw()

                Assert.assertEquals(this, ref.view)
                Assert.assertEquals(DrawableStateSet(firstState), ref.stateSet)

                val secondState =
                    intArrayOf(android.R.attr.state_checked, android.R.attr.state_accelerated)
                whenever(this.drawableState).thenReturn(secondState)

                listener.onPreDraw()

                Assert.assertEquals(this, ref.view)
                Assert.assertEquals(DrawableStateSet(secondState), ref.stateSet)
            }
    }

    @Test
    fun values() {
        val inputs = listOf(
            android.R.attr.state_pressed to DrawableState.pressed,
            android.R.attr.state_focused to DrawableState.focused,
            android.R.attr.state_selected to DrawableState.selected,
            android.R.attr.state_enabled to DrawableState.enabled,
            android.R.attr.state_activated to DrawableState.activated,
            android.R.attr.state_checked to DrawableState.checked,
            0 to DrawableState(0)
        )

        for ((expected, state) in inputs) {
            Assert.assertEquals(expected, state.value)
        }
    }

    @Test
    fun plus() {
        val f = DrawableState.pressed
        val s = DrawableState.activated

        val result = f + s
        Assert.assertEquals("pressed, result:$result", true, result.contains(f))
        Assert.assertEquals("activated, result:$result", true, result.contains(s))
    }

    @Test
    fun attrResourceName() {
        val inputs = listOf(
            "android:attr/state_pressed" to android.R.attr.state_pressed,
            "android:attr/state_focused" to android.R.attr.state_focused,
            "android:attr/state_selected" to android.R.attr.state_selected,
            "android:attr/state_enabled" to android.R.attr.state_enabled,
            "android:attr/state_activated" to android.R.attr.state_activated,
            "android:attr/state_checked" to android.R.attr.state_checked,
            // not included in predefined, still name is resolved
            "android:attr/state_accelerated" to android.R.attr.state_accelerated,
            // fail, name is not resolved
            "1234" to 1234
        )

        for ((expected, attr) in inputs) {
            Assert.assertEquals(expected, DrawableState.attrResourceName(attr))
        }
    }

    @Test
    fun `attrResourceName - resources`() {
        // supplied resources are used
        val resources = mockt<Resources>()
        val inputs = listOf(
            android.R.attr.state_pressed,
            1,
            2,
            -1,
            Int.MIN_VALUE,
            Int.MAX_VALUE
        )

        inputs.forEach { DrawableState.attrResourceName(it, resources) }

        val captor = ArgumentCaptor.forClass(Int::class.java)
        verify(resources, times(inputs.size)).getResourceName(captor.capture())

        Assert.assertEquals(
            inputs,
            captor.allValues
        )
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class DrawableStateSetTest {

    @Test
    fun none() {
        val inputs = listOf(
            intArrayOf(),
            intArrayOf(android.R.attr.state_window_focused),
            intArrayOf(android.R.attr.state_accelerated, android.R.attr.state_above_anchor)
        )
        for (input in inputs) {
            assertState(DrawableStateSet(input), emptySet())
        }
    }

    @Test
    fun single() {
        val inputs = listOf(
            DrawableState.pressed,
            DrawableState.focused,
            DrawableState.selected,
            DrawableState.enabled,
            DrawableState.activated,
            DrawableState.checked,
            DrawableState(android.R.attr.state_above_anchor),
            DrawableState(1)
        )

        for (input in inputs) {
            // exact match
            assertState(
                DrawableStateSet(intArrayOf(input.value)),
                setOf(input)
            )
            // one of many
            assertState(
                DrawableStateSet(intArrayOf(0, 1, 2, input.value)),
                setOf(input)
            )
        }
    }

    @Test
    fun multiple() {
        val inputs = listOf(
            DrawableState.pressed,
            DrawableState.focused,
            DrawableState.selected,
            DrawableState.enabled,
            DrawableState.activated,
            DrawableState.checked,
            DrawableState(android.R.attr.state_above_anchor),
            DrawableState(1)
        )

        for (window in 2..(inputs.size)) {

            val windowed = inputs.windowed(window)
                .map { it.toSet() }

            for (set in windowed) {

                // all exact
                val all = set.map { it.value }.toIntArray()

                Assert.assertEquals(
                    "containsAll:$set in [${all.contentToString()}]",
                    true,
                    DrawableStateSet(all).containsAll(set)
                )

                // all with additional entries (still true)
                val allWithAdditional = all + 98
                Assert.assertEquals(
                    "containsAllAdditional:$set in [${allWithAdditional.contentToString()}]",
                    true,
                    DrawableStateSet(allWithAdditional).containsAll(set)
                )

                Assert.assertEquals(
                    "containsAny:$set in [${allWithAdditional.contentToString()}]",
                    true,
                    DrawableStateSet(allWithAdditional).containsAny(set)
                )

                for (state in set) {
                    Assert.assertEquals(
                        "containsAny.individual:$state in [${allWithAdditional.contentToString()}]",
                        true,
                        DrawableStateSet(allWithAdditional).containsAny(setOf(state))
                    )
                }

                val missing = all.dropLast(1).toIntArray()
                Assert.assertEquals(
                    "missing-all:$set in [${missing.contentToString()}]",
                    false,
                    DrawableStateSet(missing).containsAll(set)
                )
                Assert.assertEquals(
                    "missing-any:$set in [${missing.contentToString()}]",
                    true,
                    DrawableStateSet(missing).containsAny(set)
                )
            }
        }
    }

    private fun assertState(stateSet: DrawableStateSet, expected: Set<DrawableState>) {

        val predefined: List<Pair<DrawableState, DrawableStateSet.() -> Boolean>> = listOf(
            DrawableState.pressed to { pressed },
            DrawableState.focused to { focused },
            DrawableState.selected to { selected },
            DrawableState.enabled to { enabled },
            DrawableState.activated to { activated },
            DrawableState.selected to { selected }
        )

        for ((state, property) in predefined) {
            val contains = expected.contains(state)
            Assert.assertEquals(contains, property(stateSet))
            Assert.assertEquals(
                contains,
                stateSet.contains(state)
            )
            Assert.assertEquals(
                contains,
                stateSet.contains(DrawableState(state.value))
            )
            Assert.assertEquals(
                contains,
                stateSet.containsAll(setOf(state))
            )
            Assert.assertEquals(
                contains,
                stateSet.containsAny(setOf(state))
            )
        }

        Assert.assertEquals(false, stateSet.contains(DrawableState(-1)))
        Assert.assertEquals(false, stateSet.containsAll(DrawableState(-1) + DrawableState(-1)))
        Assert.assertEquals(false, stateSet.containsAny(DrawableState(-1) + DrawableState(-1)))
    }
}