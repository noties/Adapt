package io.noties.adapt.ui

import android.content.Context
import android.view.View
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElementTest {

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
            .onView { alpha = 0.42F }
            .onView { isEnabled = false }
        Assert.assertEquals(2, element.viewBlocks.size)

        element.render()

        // view blocks are cleared and view has applied properties
        Assert.assertEquals(0, element.viewBlocks.size)

        verify(element.view).alpha = eq(0.42F)
        verify(element.view).isEnabled = eq(false)
    }

    @Test
    fun layoutBlock() {
        val element = newElement()
            .onLayout { width = 88 }
            .onLayout { height = 9182 }

        Assert.assertEquals(2, element.layoutBlocks.size)

        val lp = LayoutParams(-1, -1)
        doReturn(lp).`when`(element.view).layoutParams

        element.render()

        Assert.assertEquals(0, element.layoutBlocks.size)

        Assert.assertEquals(88, lp.width)
        Assert.assertEquals(9182, lp.height)
    }
}