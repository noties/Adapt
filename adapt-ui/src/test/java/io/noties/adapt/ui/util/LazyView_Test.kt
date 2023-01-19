package io.noties.adapt.ui.util

import android.content.Context
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.ImageView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.newElement
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class LazyView_Test {

    private val context: Context get() = RuntimeEnvironment.getApplication()

    @Test
    fun `measure - noop`() {
        // any layout params should be ignored and size of 0-0 used

        val specs = listOf(
            MeasureSpec.makeMeasureSpec(777, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(1080, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(61, MeasureSpec.AT_MOST),
        )

        for (spec in specs) {
            val view = LazyView(context) {}
            view.layoutParams = LayoutParams(400, 725)

            view.measure(spec, spec)

            assertEquals(0, view.measuredWidth)
            assertEquals(0, view.measuredHeight)
        }
    }

    @Test
    fun noop() {
        // no children?
        // no parent
        // no parent index
        // isInjected?

        fun assertNoop(name: String, view: LazyView) {
            assertNotNull(name, view.children)
            assertEquals(name, false, view.isInjected)
            assertEquals(name, View.GONE, view.visibility)
        }

        val views = listOf(
//            "no-children" to LazyView(context) {}.also {
//                it.children = null
//
//                // but create parent
//                val vg: ViewGroup = mockt {
//                    on { indexOfChild(eq(it)) } doReturn 0
//                }
//                Shadows.shadowOf(it).setMyParent(vg)
//            },
            "no-parent" to LazyView(context) {},
            "no-parent-index" to LazyView(context) {}.also {
                val vg: ViewGroup = mockt {
                    // not found
                    on { indexOfChild(any()) } doReturn -1
                }
                Shadows.shadowOf(it).setMyParent(vg)
            }
        )

        for ((name, view) in views) {
            assertNoop(name, view)
            try {
                view.inject()
            } catch (t: Throwable) {
                println("name:$name message:${t.message}")
                throw t
            }
            assertNoop("$name-injected", view)
        }
    }

    @Test
    fun visibility() {
        // gone visibility does nothing (does not show, does not affect anything)
        val inputs = listOf(
            "VISIBLE" to View.VISIBLE to true,
            "INVISIBLE" to View.INVISIBLE to true,
            "GONE" to View.GONE to false
        )

        for ((input, result) in inputs) {
            val view = LazyView(context) {}
            view.also {
                val parent: ViewGroup = mockt {
                    on { context } doReturn context
                    on { indexOfChild(eq(it)) } doReturn 0
                }
                Shadows.shadowOf(it).setMyParent(parent)
            }
            // initial isDisplayed is false
            assertEquals(input.first, false, view.isInjected)
            view.visibility = input.second
            assertEquals(input.first, result, view.isInjected)
        }
    }

    @Test
    fun `inject - no parent`() {
        val element = newElement()

        // when LazyView is not attached, it won't display
        val view = LazyView(context) {
            add(element)
        }

        // not attached
        assertEquals(null, view.parent)
        assertEquals(false, view.isInjected)

        view.inject()

        assertEquals(false, view.isInjected)
    }

    @Test
    fun inject() {
        // after successful inject, internal children are cleared

        val inputs: List<Pair<Boolean, (LazyView) -> Unit>> = listOf(
            false to { it.visibility = View.GONE },
            true to { it.visibility = View.VISIBLE },
            true to { it.visibility = View.INVISIBLE },
            true to { it.inject() },
        )

        for (input in inputs) {

            val elements = listOf(
                newElement(),
                newElementOfType<ImageView>()
            )

            val view = LazyView(context) {
                elements.forEach { add(it) }
            }

            val index = 33
            val parent: ViewGroup = mockt {
                on { context } doReturn context
                on { indexOfChild(eq(view)) } doReturn index
            }

            Shadows.shadowOf(view).setMyParent(parent)

            assertNotNull("children", view.children)
            assertEquals("parent", parent, view.parent)

            input.second(view)

            if (!input.first) {
                // nothing happened
                assertNotNull("children", view.children)
                assertEquals("parent", parent, view.parent)
                continue
            }

            assertNull("children", view.children)
            verify(parent).removeViewAt(eq(index))
            elements
                .withIndex()
                .forEach { (i, value) ->
//                    verify(value).init(any())
                    verify(parent).addView(eq(value.view), eq(index + i))
//                    verify(value).render()
                }
        }
    }
}