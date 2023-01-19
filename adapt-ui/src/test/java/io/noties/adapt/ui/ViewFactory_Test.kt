package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Objects
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK], qualifiers = "xxhdpi")
class ViewFactory_Test {

    private lateinit var context: Context

    @Before
    fun before() {
        context = RuntimeEnvironment.getApplication()
    }

    @Test
    fun `createView - empty`() {
        try {
            ViewFactory.createView(context) {
                // no calls
            }
            Assert.fail()
        } catch (t: Throwable) {
            assertEquals(
                "Unexpected state, view must contain exactly one root element",
                t.message
            )
        }
    }

    @Test
    fun `createView - multiple`() {
        try {
            ViewFactory.createView(context) {
                Text()
                Text()
            }
            Assert.fail()
        } catch (t: Throwable) {
            assertEquals(
                "Unexpected state, view must contain exactly one root element",
                t.message
            )
        }
    }

    @Test
    fun `createView - references`() {
        class Ref {
            var textView: TextView? = null
        }

        val ref = Ref()
        val view = ViewFactory.createView(context, ref) {
            Text()
                .reference(it::textView)
        }

        assertNotNull(ref.textView)
        assertEquals(view, ref.textView)
    }

    @Test
    fun `createView - single`() {
        val called = AtomicBoolean()
        val element = ViewElement<TextView, LayoutParams> {
            called.set(true)
            TextView(it)
        }

        val view = ViewFactory.createView(context) {
            element.also { add(it) }
        }

        assertTrue(called.get())
        assertNotNull(view)
        assertEquals(TextView::class.java, view::class.java)

        // validate default layout params
        val lp = view.layoutParams
        assertEquals(LayoutParams.MATCH_PARENT, lp.width)
        assertEquals(LayoutParams.WRAP_CONTENT, lp.height)
    }

    @Test
    fun addChildren() {
        val group = FrameLayout(context)

        ViewFactory.addChildren<ViewGroup, LayoutParams>(group) {
            Text()
            Image()
        }

        assertEquals(2, group.childCount)

        assertEquals(TextView::class.java, group.getChildAt(0)::class.java)
        assertEquals(ImageView::class.java, group.getChildAt(1)::class.java)
    }

    @Test
    fun `init - no viewGroup`() {
        // when no viewgroup is specified (null)
        val factory = ViewFactory<LayoutParams>(
            mockt(),
            null
        )

        assertFalse(factory.hasViewGroup)

        assertThrows(java.lang.IllegalStateException::class.java) {
            factory.viewGroup
        }
    }

    @Test
    fun init() {
        val factory = ViewFactory<LayoutParams>(
            mockt(),
            mockt()
        )
        assertTrue(factory.hasViewGroup)

        // not throws
        assertNotNull(factory.viewGroup)
    }

    @Test
    fun `viewCreator - default LP`() {
        val viewGroup: ViewGroup = mockt {
            on { context } doReturn RuntimeEnvironment.getApplication()
        }
        val creator = ViewFactory.newView(viewGroup)

        val lp = ViewFactory.ViewCreator.defaultLayoutParams

        assertLayoutParams(
            lp,
            creator.layoutParams
        )

        val view = creator.create { View() }
        assertLayoutParams(lp, view.layoutParams)
    }

    @Test
    fun `viewCreator - layoutParams`() {
        val viewGroup: ViewGroup = mockt {
            on { context } doReturn RuntimeEnvironment.getApplication()
        }

        fun lp(): LayoutParams = FrameLayout.LayoutParams(123, LayoutParams.MATCH_PARENT)

        val view = ViewFactory.newView(viewGroup)
            .layoutParams(lp())
            .create { View() }

        assertLayoutParams(lp(), view.layoutParams)
    }

    @Test
    fun `escape direct usage (after used)`() {
        // after factory is used, `add` should throw

        val factories = mutableListOf<Pair<String, ViewFactory<LayoutParams>>>()

        ViewFactory.createView(context) {
            factories.add("createView" to this)

            Text()
        }

        val vg: ViewGroup = mock {
            on { context } doReturn context
        }
        ViewFactory.addChildren(vg) {
            factories.add("addChildren" to this)

            Image()
        }

        for ((name, factory) in factories) {
            assertEquals(true, factory.isUsed)
            assertEquals(emptyList<Any?>(), factory.inspectElements())

            try {
                factory.add(newElement())
                fail(name)
            } catch (t: IllegalStateException) {
                assertTrue(
                    "$name:${t.message}",
                    t.message!!.contains("ViewFactory has been already used")
                )
            }
        }
    }

    @Test
    fun `context-receiver escape`() {

        fun <LP : FrameLayout.LayoutParams> ViewFactory<LP>.Frame() = Element { View(it) }

        ViewFactory.createView(context) {
            // root
            ZStack {
                VStack {
                    try {
                        Frame()
                        fail()
                    } catch (t: IllegalStateException) {
                        assertTrue(
                            t.message,
                            t.message!!.contains("ViewFactory has been already used")
                        )
                    }
                }
            }
        }
    }

    @Test
    fun isUsed() {
        val factory = ViewFactory<LayoutParams>(context)
        assertEquals(false, factory.isUsed)
        factory.add(newElement())
        assertEquals(1, factory.inspectElements().size)
        assertEquals(false, factory.isUsed)
        assertEquals(1, factory.useElements().size)
        assertEquals(true, factory.isUsed)
        assertEquals(0, factory.useElements().size)
        assertEquals(0, factory.inspectElements().size)

        try {
            factory.add(newElement())
            fail()
        } catch (t: IllegalStateException) {
            assertTrue(
                t.message,
                t.message!!.contains("ViewFactory has been already used")
            )
        }
    }

    // a primitive version for equals... platform LP do not have it implemented...
    //  and most of LPs have specific properties too
    private fun assertLayoutParams(lhs: LayoutParams, rhs: LayoutParams?) {

        fun LayoutParams?.string(): String {

            if (this == null) return "null"

            fun string(value: Int): String = when (value) {
                LayoutParams.MATCH_PARENT -> "FILL"
                LayoutParams.WRAP_CONTENT -> "WRAP"
                else -> value.toString()
            }

            val name = this::class.qualifiedName ?: "<null>"

            return "$name(width:${string(width)} height:${string(height)})"
        }

        val value = Objects.equals(lhs::class, rhs?.let { it::class }) &&
                lhs.width == rhs?.width &&
                lhs.height == rhs.height

        assertTrue(
            "lhs:${lhs.string()} rhs:${rhs.string()}",
            value
        )
    }
}