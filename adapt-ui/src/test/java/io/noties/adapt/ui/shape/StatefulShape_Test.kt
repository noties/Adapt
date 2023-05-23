package io.noties.adapt.ui.shape

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import io.noties.adapt.ui.state.DrawableState
import io.noties.adapt.ui.state.DrawableStateSet
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class StatefulShape_Test {

    @Test
    fun `single - default`() {
        val shape = OvalShape()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setDefault(shape) },
            { setDefault { shape } },
            { set(emptySet(), shape) },
            { set(emptySet()) { shape } }
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)
            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(
                emptySet(),
                shape,
                stateful
            )
        }
    }

    @Test
    fun `single - pressed`() {
        val state = DrawableState.pressed
        val shape = RectangleShape()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setPressed(shape) },
            { setPressed { shape } },
            { set(state, shape) },
            { set(state) { shape } },
            { set(setOf(state), shape) },
            { set(setOf(state)) { shape } },
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            Assert.assertEquals(0, stateful.entries.size)
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(setOf(state), shape, stateful)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        assertFirstEntry(setOf(state), shape, stateful)
    }

    @Test
    fun `single - enabled`() {
        val state = DrawableState.enabled
        val shape = ArcShape(0F, 1F)

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setEnabled(shape) },
            { setEnabled { shape } },
            { set(state, shape) },
            { set(state) { shape } },
            { set(setOf(state), shape) },
            { set(setOf(state)) { shape } },
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(setOf(state), shape, stateful)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        assertFirstEntry(setOf(state), shape, stateful)
    }

    @Test
    fun `single - focused`() {
        val state = DrawableState.focused
        val shape = CornersShape()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setFocused(shape) },
            { setFocused { shape } },
            { set(state, shape) },
            { set(state) { shape } },
            { set(setOf(state), shape) },
            { set(setOf(state)) { shape } },
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(setOf(state), shape, stateful)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        assertFirstEntry(setOf(state), shape, stateful)
    }

    @Test
    fun `single - activated`() {
        val state = DrawableState.activated
        val shape = CornersShape()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setActivated(shape) },
            { setActivated { shape } },
            { set(state, shape) },
            { set(state) { shape } },
            { set(setOf(state), shape) },
            { set(setOf(state)) { shape } },
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(setOf(state), shape, stateful)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        assertFirstEntry(setOf(state), shape, stateful)
    }

    @Test
    fun `single - selected`() {
        val state = DrawableState.selected
        val shape = CornersShape()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setSelected(shape) },
            { setSelected { shape } },
            { set(state, shape) },
            { set(state) { shape } },
            { set(setOf(state), shape) },
            { set(setOf(state)) { shape } },
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(setOf(state), shape, stateful)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        assertFirstEntry(setOf(state), shape, stateful)
    }

    @Test
    fun `single - checked`() {
        val state = DrawableState.checked
        val shape = LineShape()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setChecked(shape) },
            { setChecked { shape } },
            { set(state, shape) },
            { set(state) { shape } },
            { set(setOf(state), shape) },
            { set(setOf(state)) { shape } }
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(setOf(state), shape, stateful)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        assertFirstEntry(setOf(state), shape, stateful)
    }

    @Test
    fun multiple() {
        val set = DrawableState.pressed + DrawableState.focused + DrawableState(12)
        val shape = CircleShape()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { set(set, shape) },
            { set(set) { shape } }
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            assertFirstEntry(set, shape, stateful)
        }
    }

    @Test
    fun drawable() {
        val defaultShape = OvalShape()
        val expected = listOf(
            setOf(DrawableState.pressed) to RectangleShape(),
            emptySet<DrawableState>() to defaultShape,
            setOf(DrawableState.pressed, DrawableState.enabled) to CircleShape(),
            setOf(DrawableState(999)) to ArcShape(0F, 1F),
            setOf(DrawableState(1), DrawableState.enabled, DrawableState(5345)) to LineShape()
        )

        val stateful = StatefulShape().also {
            for ((k, v) in expected) {
                it.set(k, v)
            }
        }

        Assert.assertEquals(expected.size, stateful.entries.size)

        val drawable = stateful.drawable()
        val shadow = Shadows.shadowOf(drawable)

        fun assertShape(shape: Shape, state: IntArray) {
            val d = shadow.getDrawableForState(state)
            require(d is ShapeDrawable<*>) {
                "d must be ShapeDrawable:$d state:${DrawableStateSet(state)}"
            }
            Assert.assertEquals(shape, d.shape)
        }

        for ((k, v) in expected) {
            val state = if (k.isEmpty()) {
                StateSet.WILD_CARD
            } else {
                k.map { it.value }.toIntArray()
            }
            assertShape(v, state)
        }
    }

    @Test
    @Config(shadows = [StateListDrawableShadow2::class])
    fun sorted() {
        // entries are sorted based on number of states, with WILDCARD always the last

        val wildcard = RectangleShape()

        val inputs = listOf(
            setOf(DrawableState.pressed) to OvalShape(),
            emptySet<DrawableState>() to wildcard,
            DrawableState.focused + DrawableState.activated to CircleShape()
        )

        val stateful = StatefulShape.drawable {
            for ((set, shape) in inputs) {
                set(set, shape)
            }
        }

        val shadow = StateListDrawableShadow2.extract(stateful)
        val expected = inputs
            .map {
                val k = if (it.first.isEmpty()) {
                    StateSet.WILD_CARD
                } else {
                    it.first.map { it.value }.toIntArray()
                }
                k to it.second
            }
            .sortedByDescending { it.first.size }
        Assert.assertEquals(StateSet.WILD_CARD to wildcard, expected.last())

        for ((i, entry) in expected.withIndex()) {
            val (state, shape) = entry

            val (stateEntry, drawable) = shadow.states[i]
            Assert.assertArrayEquals(state, stateEntry)
            require(drawable is ShapeDrawable<*>) {
                "drawable must be of ShapeDrawable type, drawable:$drawable"
            }
            Assert.assertEquals(shape, drawable.shape)
        }
    }

    private fun assertFirstEntry(
        set: Set<DrawableState>,
        shape: Shape,
        statefulShape: StatefulShape
    ) {
        val (key, value) = statefulShape.entries.entries.first()
        Assert.assertEquals(set, key)
        Assert.assertEquals(shape, value)
    }

    @Implements(StateListDrawable::class)
    class StateListDrawableShadow2 {

        companion object {
            fun extract(shadow: Any): StateListDrawableShadow2 {
                return Shadow.extract(shadow) as StateListDrawableShadow2
            }
        }

        val states = mutableListOf<Pair<IntArray, Drawable>>()

        @Implementation
        fun addState(stateSet: IntArray, drawable: Drawable) {
            states.add(stateSet to drawable)
        }
    }
}