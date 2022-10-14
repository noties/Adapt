package io.noties.adapt.ui

import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.ui.util.Gravity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElement_ExtensionsCast_Test {

    @Test
    fun `castView - initialized`() {
        // casting and reporting classCastException happens synchronously

        @Suppress("UNCHECKED_CAST")
        val element = newElementOfType<CheckBox>() as ViewElement<TextView, *>
        assertTrue(element.isInitialized)

        // success
        try {
            element.castView(CheckBox::class.java)
        } catch (t: AdaptClassCastException) {
            fail(t.message)
        }

        // failure
        try {
            element.castView(EditText::class.java)
            fail()
        } catch (t: AdaptClassCastException) {
            val message = t.message!!
            assertTrue(message, message.contains(EditText::class.java.name))
            assertTrue(message, message.contains(CheckBox::class.java.name))
            assertNull(t.cause)
        }
    }

    @Test
    fun `castView - non-initialized`() {
        val success = ViewElement<TextView, LayoutParams> {
            mock(CheckBox::class.java)
        }
        val failure = ViewElement<TextView, LayoutParams> {
            mock(CheckBox::class.java)
        }

        assertFalse(success.isInitialized)
        assertFalse(failure.isInitialized)

        success.castView(CheckBox::class.java)
            .checked(true)

        failure.castView(EditText::class.java)

        success.init(RuntimeEnvironment.getApplication())
        failure.init(RuntimeEnvironment.getApplication())

        assertTrue(success.isInitialized)
        assertTrue(failure.isInitialized)

        try {
            success.render()
            verify(success.view as CheckBox).isChecked = eq(true)
        } catch (t: AdaptClassCastException) {
            t.printStackTrace()
            fail(t.message)
        }

        try {
            failure.render()
            fail()
        } catch (t: AdaptClassCastException) {
            val message = t.message!!
            assertTrue(message, message.contains(EditText::class.java.name))
            assertTrue(message, message.contains(CheckBox::class.java.name))
            assertNotNull(t.cause)
        }
    }

    // ifCastView initialized
    @Test
    fun `ifCastView - non-initialized - success`() {
        val element = ViewElement<TextView, LayoutParams> {
            mock(CheckBox::class.java)
        }
        assertFalse(element.isInitialized)

        // success
        element.ifCastView(CheckBox::class.java) {
            it.checked(true)
        }

        element.init(RuntimeEnvironment.getApplication())
        assertTrue(element.isInitialized)

        element.render()
        val captor = ArgumentCaptor.forClass(Runnable::class.java)

        verify(element.view).post(captor.capture())

        val action = captor.value
        assertNotNull(action)

        action.run()

        verify(element.view as CheckBox).isChecked = eq(true)
    }

    @Test
    fun `ifCastView - non-initialized - failure`() {

        val element = ViewElement<TextView, LayoutParams> {
            mock(CheckBox::class.java)
        }
        assertFalse(element.isInitialized)

        element.ifCastView(EditText::class.java) {
            it.background(12)
        }

        element.init(RuntimeEnvironment.getApplication())
        assertTrue(element.isInitialized)

        element.render()

        verify(element.view, never()).post(any(Runnable::class.java))
        verify(element.view, never()).setBackgroundColor(anyInt())
    }

    private fun <V : CheckBox> ViewElement<V, LayoutParams>.checked(
        checked: Boolean
    ) = onView {
        isChecked = checked
    }

    @Test
    fun `castLayout - initialized`() {
        // casting and reporting classCastException happens synchronously

        @Suppress("UNCHECKED_CAST")
        val element = ViewElement<View, LayoutParams> {
            View(it).also { v ->
                v.layoutParams = FrameLayout.LayoutParams(1, 1)
            }
        }.also { it.init(RuntimeEnvironment.getApplication()) }
        assertTrue(element.isInitialized)

        // success
        try {
            element.castLayout(FrameLayout.LayoutParams::class.java)
        } catch (t: AdaptClassCastException) {
            fail(t.message)
        }

        // failure
        try {
            element.castLayout(LinearLayout.LayoutParams::class.java)
            fail()
        } catch (t: AdaptClassCastException) {
            val message = t.message!!
            assertTrue(message, message.contains(FrameLayout.LayoutParams::class.java.name))
            assertTrue(message, message.contains(LinearLayout.LayoutParams::class.java.name))
            assertNull(t.cause)
        }
    }

    @Test
    fun `castLayout - non-initialized`() {
        val success = ViewElement<View, LayoutParams> {
            View(it).also { v -> v.layoutParams = FrameLayout.LayoutParams(1, 1) }
        }
        val failure = ViewElement<View, LayoutParams> {
            View(it).also { v -> v.layoutParams = FrameLayout.LayoutParams(1, 1) }
        }

        assertFalse(success.isInitialized)
        assertFalse(failure.isInitialized)

        success.castLayout(FrameLayout.LayoutParams::class.java)
            .layoutGravity(Gravity.bottom)


        failure.castLayout(LinearLayout.LayoutParams::class.java)
            .layoutWeight(3F)

        success.init(RuntimeEnvironment.getApplication())
        failure.init(RuntimeEnvironment.getApplication())

        assertTrue(success.isInitialized)
        assertTrue(failure.isInitialized)

        try {
            success.render()
            assertEquals(
                Gravity.bottom.value,
                (success.view.layoutParams as FrameLayout.LayoutParams).gravity
            )
        } catch (t: AdaptClassCastException) {
            t.printStackTrace()
            fail(t.message)
        }

        try {
            failure.render()
            fail()
        } catch (t: AdaptClassCastException) {
            val message = t.message!!
            assertTrue(message, message.contains(FrameLayout.LayoutParams::class.java.name))
            assertTrue(message, message.contains(LinearLayout.LayoutParams::class.java.name))
            assertNotNull(t.cause)
        }
    }

    @Test
    fun `ifCastLayout - non-initialized - success`() {
        val element = ViewElement<View, LayoutParams> {
            mock(View::class.java).also { v ->
                `when`(v.layoutParams).thenReturn(FrameLayout.LayoutParams(1, 1))
            }
        }
        assertFalse(element.isInitialized)

        // success
        element.ifCastLayout(FrameLayout.LayoutParams::class.java) {
            it.layoutGravity(Gravity.bottom)
        }

        element.init(RuntimeEnvironment.getApplication())
        assertTrue(element.isInitialized)

        element.render()
        val captor = ArgumentCaptor.forClass(Runnable::class.java)

        verify(element.view).post(captor.capture())

        val action = captor.value
        assertNotNull(action)

        // action posted to exit render phrase
        action.run()

        assertEquals(
            Gravity.bottom.value,
            (element.view.layoutParams as FrameLayout.LayoutParams).gravity
        )
    }

    @Test
    fun `ifCastLayout - non-initialized - failure`() {

        val element = ViewElement<View, LayoutParams> {
            mock(View::class.java).also { v ->
                `when`(v.layoutParams).thenReturn(
                    FrameLayout.LayoutParams(
                        1,
                        1
                    )
                )
            }
        }
        assertFalse(element.isInitialized)

        element.ifCastLayout(LinearLayout.LayoutParams::class.java) {
            it.layoutWeight(2F)
        }

        element.init(RuntimeEnvironment.getApplication())
        assertTrue(element.isInitialized)

        element.render()

        verify(element.view, never()).post(any(Runnable::class.java))
    }
}