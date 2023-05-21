package io.noties.adapt.ui.shape

import android.util.StateSet
import io.noties.adapt.ui.state.DrawableState
import io.noties.adapt.ui.state.DrawableStateSet
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class StatefulShapeTest {

    @Test
    fun `single - default`() {
        val shape = Oval()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { setDefault(shape) },
            { setDefault { shape } },
            { set(emptySet(), shape) },
            { set(emptySet()) { shape } }
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)
            Assert.assertEquals(0, stateful.entries.size)
            Assert.assertEquals(shape, stateful.defaultEntry)
        }
    }

    @Test
    fun `single - pressed`() {
        val state = DrawableState.pressed
        val shape = Rectangle()

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
            Assert.assertNull(stateful.defaultEntry)
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            val entry = stateful.entries.entries.first()
            Assert.assertEquals(setOf(state), entry.key)
            Assert.assertEquals(shape, entry.value)
            Assert.assertNull(stateful.defaultEntry)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        val (k, v) = stateful.entries.entries.first()
        Assert.assertEquals(setOf(state), k)
        Assert.assertEquals(shape, v)
    }

    @Test
    fun `single - enabled`() {
        val state = DrawableState.enabled
        val shape = Arc(0F, 1F)

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
            val entry = stateful.entries.entries.first()
            Assert.assertEquals(setOf(state), entry.key)
            Assert.assertEquals(shape, entry.value)
            Assert.assertNull(stateful.defaultEntry)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        val (k, v) = stateful.entries.entries.first()
        Assert.assertEquals(setOf(state), k)
        Assert.assertEquals(shape, v)
    }

    @Test
    fun `single - focused`() {
        val state = DrawableState.focused
        val shape = Corners()

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
            val entry = stateful.entries.entries.first()
            Assert.assertEquals(setOf(state), entry.key)
            Assert.assertEquals(shape, entry.value)
            Assert.assertNull(stateful.defaultEntry)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        val (k, v) = stateful.entries.entries.first()
        Assert.assertEquals(setOf(state), k)
        Assert.assertEquals(shape, v)
    }

    @Test
    fun `single - activated`() {
        val state = DrawableState.activated
        val shape = Corners()

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
            val entry = stateful.entries.entries.first()
            Assert.assertEquals(setOf(state), entry.key)
            Assert.assertEquals(shape, entry.value)
            Assert.assertNull(stateful.defaultEntry)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        val (k, v) = stateful.entries.entries.first()
        Assert.assertEquals(setOf(state), k)
        Assert.assertEquals(shape, v)
    }

    @Test
    fun `single - selected`() {
        val state = DrawableState.selected
        val shape = Corners()

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
            val entry = stateful.entries.entries.first()
            Assert.assertEquals(setOf(state), entry.key)
            Assert.assertEquals(shape, entry.value)
            Assert.assertNull(stateful.defaultEntry)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        val (k, v) = stateful.entries.entries.first()
        Assert.assertEquals(setOf(state), k)
        Assert.assertEquals(shape, v)
    }

    @Test
    fun `single - checked`() {
        val state = DrawableState.checked
        val shape = Line()

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
            val entry = stateful.entries.entries.first()
            Assert.assertEquals(setOf(state), entry.key)
            Assert.assertEquals(shape, entry.value)
            Assert.assertNull(stateful.defaultEntry)
        }

        val stateful = StatefulShape().also { s ->
            inputs.forEach { it.invoke(s) }
        }
        Assert.assertEquals(1, stateful.entries.size)
        val (k, v) = stateful.entries.entries.first()
        Assert.assertEquals(setOf(state), k)
        Assert.assertEquals(shape, v)
    }

    @Test
    fun multiple() {
        val set = DrawableState.pressed + DrawableState.focused + DrawableState(12)
        val shape = Circle()

        val inputs: List<StatefulShape.() -> Unit> = listOf(
            { set(set, shape) },
            { set(set) { shape } }
        )

        for (input in inputs) {
            val stateful = StatefulShape()
            input.invoke(stateful)

            Assert.assertEquals(1, stateful.entries.size)
            val entry = stateful.entries.entries.first()
            Assert.assertEquals(set, entry.key)
            Assert.assertEquals(shape, entry.value)
        }
    }

    @Test
    fun drawable() {
        val defaultShape = Oval()
        val expected = listOf(
            setOf(DrawableState.pressed) to Rectangle(),
            emptySet<DrawableState>() to defaultShape,
            setOf(DrawableState.pressed, DrawableState.enabled) to Circle(),
            setOf(DrawableState(999)) to Arc(0F, 1F),
            setOf(DrawableState(1), DrawableState.enabled, DrawableState(5345)) to Line()
        )

        val stateful = StatefulShape().also {
            for ((k, v) in expected) {
                it.set(k, v)
            }
        }

        // -1, as default is store independently
        Assert.assertEquals(expected.size - 1, stateful.entries.size)
        Assert.assertEquals(defaultShape, stateful.defaultEntry)

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
}