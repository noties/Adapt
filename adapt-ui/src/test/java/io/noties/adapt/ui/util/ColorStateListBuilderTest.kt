package io.noties.adapt.ui.util

import android.content.res.ColorStateList
import android.util.StateSet
import io.noties.adapt.ui.state.DrawableState
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ColorStateListBuilderTest {

    @Test
    fun `single - pressed`() {
        assertSingle(DrawableState.pressed) {
            setPressed(it)
        }
    }

    @Test
    fun `single - enabled`() {
        assertSingle(DrawableState.enabled) {
            setEnabled(it)
        }
    }

    @Test
    fun `single - focused`() {
        assertSingle(DrawableState.focused) {
            setFocused(it)
        }
    }

    @Test
    fun `single - activated`() {
        assertSingle(DrawableState.activated) {
            setActivated(it)
        }
    }

    @Test
    fun `single - selected`() {
        assertSingle(DrawableState.selected) {
            setSelected(it)
        }
    }

    @Test
    fun `single - checked`() {
        assertSingle(DrawableState.checked) {
            setChecked(it)
        }
    }

    @Test
    fun multiple() {
        val set = DrawableState.checked + DrawableState(999) + DrawableState.selected
        val color = 91234

        val builder = ColorStateListBuilder()
        builder.set(set, color)

        assertEquals(1, builder.entries.size)
        assertEquals(
            set to color,
            builder.entries.entries.first().let { it.key to it.value }
        )
    }

    @Test
    @Config(shadows = [ColorStateListShadow::class])
    fun create() {

        val default = 12

        val inputs = listOf(
            setOf(DrawableState.pressed) to 66,
            emptySet<DrawableState>() to default,
            DrawableState.focused + DrawableState(712) to 1
        )

        val csl = ColorStateListBuilder.create {
            for ((state, color) in inputs) {
                set(state, color)
            }
        }

        val (expectedStates, expectedColors) = inputs
            .map {
                val k = if (it.first.isEmpty()) {
                    StateSet.WILD_CARD
                } else {
                    it.first.map { it.value }.toIntArray()
                }
                k to it.second
            }
            .sortedByDescending { it.first.size }
            .toMap()
//            .associateBy { it.first to it.second }
            .let {
                it.keys.toTypedArray() to it.values.toIntArray()
            }

        assertArrayEquals(StateSet.WILD_CARD, expectedStates.last())
        assertEquals(default, expectedColors.last())

        val shadow = ColorStateListShadow.extract(csl)
        assertArrayEquals(expectedStates, shadow.states)
        assertArrayEquals(expectedColors, shadow.colors)
    }

    private fun assertSingle(
        state: DrawableState,
        self: ColorStateListBuilder.(Int) -> Unit
    ) {
        val color = 9721
        val inputs: List<ColorStateListBuilder.() -> Unit> = listOf(
            { set(state, color) },
            { set(DrawableState(state.value), color) },
            { set(setOf(DrawableState(state.value)), color) },
            { set(setOf(state), color) },
            { self.invoke(this, color) }
        )

        for (input in inputs) {
            val builder = ColorStateListBuilder()
            input.invoke(builder)
            assertEquals(1, builder.entries.size)
            assertEquals(
                setOf(state) to color,
                builder.entries.entries.first().let { it.key to it.value }
            )
        }

        val builder = ColorStateListBuilder().also {
            for (input in inputs) {
                input.invoke(it)
            }
        }
        assertEquals(1, builder.entries.size)
        assertEquals(
            setOf(state) to color,
            builder.entries.entries.first().let { it.key to it.value }
        )
    }

    @Implements(ColorStateList::class)
    class ColorStateListShadow {
        companion object {
            fun extract(csl: Any) = Shadow.extract(csl) as ColorStateListShadow
        }

        var states: Array<IntArray>? = null
        var colors: IntArray? = null

        @Implementation
        fun __constructor__(states: Array<IntArray>, colors: IntArray) {
            this.states = states
            this.colors = colors
        }
    }
}