package io.noties.adapt.ui

import android.content.Context
import android.view.View
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElement_Test {

    @Test
    fun init() {
        val element = ViewElement<View, LayoutParams> { mock(View::class.java) }

        // KBM: Backing field of 'var view: View' is not accessible at this point
//        Assert.assertFalse(element::view.isInitialized)

        // view is not available until `init(Context)` is called
        try {
            element.view
            fail()
        } catch (t: Throwable) {
            assertTrue(t is UninitializedPropertyAccessException)
        }

        element.init(mock(Context::class.java))

        try {
            assertNotNull(element.view)
        } catch (t: Throwable) {
            t.printStackTrace()
            fail()
        }
    }

    // KBM: Backing field of 'var view: View' is not accessible at this point
    //  nope, extension is not good enough
    //  https://stackoverflow.com/questions/47549015/isinitialized-backing-field-of-lateinit-var-is-not-accessible-at-this-point
//    private val ViewElement<*, *>.isViewInitialized: Boolean get() = ::view.isInitialized

    @Test
    fun viewBlock() {
        val element = newElement()
            .onView { it.alpha = 0.42F }
            .onView { it.isEnabled = false }
        assertEquals(2, element.viewBlocks.size)

        element.render()

        // view blocks are cleared and view has applied properties
        assertEquals(0, element.viewBlocks.size)

        verify(element.view).alpha = eq(0.42F)
        verify(element.view).isEnabled = eq(false)
    }

    @Test
    fun layoutBlock() {
        val element = newElement()
            .onLayoutParams { it.width = 88 }
            .onLayoutParams { it.height = 9182 }

        assertEquals(2, element.layoutParamsBlocks.size)

        val lp = LayoutParams(-1, -1)
        doReturn(lp).`when`(element.view).layoutParams

        element.render()

        assertEquals(0, element.layoutParamsBlocks.size)

        assertEquals(88, lp.width)
        assertEquals(9182, lp.height)
    }

    @Test
    fun isRendering() {
        // when inside rendering block, variable should be true
        val results = mutableListOf<Boolean>()

        lateinit var element: ViewElement<View, LayoutParams>

        element = ViewElement<View, LayoutParams> { mock(View::class.java, RETURNS_MOCKS) }
            .also { it.init(mock(Context::class.java)) }
            .onLayoutParams {
                results.add(element.isRendering)
            }
            .onView {
                results.add(element.isRendering)
            }

        // no rendering happens
        assertFalse("element.isRendering", element.isRendering)
        // results are not populated, render is not yet called
        assertEquals(0, results.size)

        element.render()

        assertEquals(
            listOf(true, true),
            results
        )
    }

    @Test
    fun `layoutBlock - add during rendering`() {
        val callback: LayoutParams.() -> Unit = io.noties.adapt.ui.testutil.mockt()

        val element = newElement()
            .useLayoutParams()
            .also { el ->
                el.onLayoutParams {
                    el.onLayoutParams(callback)
                }
            }

        assertEquals(1, element.layoutParamsBlocks.size)
        verify(callback, never()).invoke(any())

        element.render()

        verify(callback, times(1)).invoke(any())
    }

    @Test
    fun `layoutBlock - endless - self`() {
        val element = newElement()
            .useLayoutParams()

        lateinit var block: LayoutParams.() -> Unit
        block = {
            element.onLayoutParams(block)
        }

        try {
            element.onLayoutParams(block).render()
            fail()
        } catch (t: IllegalStateException) {
            assertTrue(true)
        }
    }

    @Test
    fun `layoutBlock - endless - gen`() {
        val element = newElement()
            .useLayoutParams()

        fun gen() {
            element.onLayoutParams { gen() }
        }

        try {
            element.onLayoutParams { gen() }.render()
            fail()
        } catch (t: IllegalStateException) {
            assertTrue(true)
        }
    }

    @Test
    fun `viewBlock - add during rendering`() {
        // if onView callback adds another callback it should be executed also
        val callback: (View) -> Unit = io.noties.adapt.ui.testutil.mockt()

        val element = newElement()
            .onElementView {
                it.onView(callback)
            }

        assertEquals(1, element.viewBlocks.size)

        verify(callback, never()).invoke(any())

        element.render()

        // not it would be called
        verify(callback, times(1)).invoke(any())
    }

    @Test
    fun `viewBlock - endless - self`() {
        // onView posts self
        val element = newElement()
        lateinit var onView: View.() -> Unit
        onView = {
            element.onView(onView)
        }

        try {
            element.onView(onView).render()
            fail()
        } catch (e: IllegalStateException) {
            assertTrue(true)
        }
    }

    @Test
    fun `viewBlock - endless - gen`() {
        val element = newElement()
        fun gen() {
            element.onView { gen() }
        }

        try {
            element.onView { gen() }.render()
            fail()
        } catch (t: IllegalStateException) {
            assertTrue(true)
        }
    }

    @Test
    fun `viewBlock - layoutBlock`() {
        // viewBlock posts layoutBlock
        //  layoutBlock posts viewBlock

        val element = newElement()
            .useLayoutParams()

        lateinit var viewBlock: View.() -> Unit
        val layoutBlock: LayoutParams.() -> Unit = {
            element.onView(viewBlock)
        }

        viewBlock = {
            element.onLayoutParams(layoutBlock)
        }

        element.onLayoutParams(layoutBlock)

        try {
            element.render()
            fail()
        } catch (t: IllegalStateException) {
            assertTrue(true)
        }
    }

    @Test
    fun `viewBlock - max permitted`() {
        val element = newElement()
        var count = 0
        fun gen() {
            if (++count < ViewElement.renderingMaxDifferenceDuringSinglePass) {
                // maximum reached
                element.onView { gen() }
            }
        }

        // does not throw, maximum amount permitted
        element.onView { gen() }.render()

        assertEquals(ViewElement.renderingMaxDifferenceDuringSinglePass, count)
    }

    @Test
    fun `render - not initialized`() {
        val element = ViewElement<View, LayoutParams> { mockt() }
        assertEquals(false, element.isInitialized)
        element.render()
    }
}