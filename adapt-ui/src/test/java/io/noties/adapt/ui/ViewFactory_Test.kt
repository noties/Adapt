package io.noties.adapt.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
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
            element.also { this.elements.add(it) }
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