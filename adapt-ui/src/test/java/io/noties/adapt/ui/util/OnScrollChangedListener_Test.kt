package io.noties.adapt.ui.util

import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.View.OnScrollChangeListener
import io.noties.adapt.ui.R
import io.noties.adapt.ui.newElement
import io.noties.adapt.ui.onViewScrollChanged
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class OnScrollChangedListener_Test {

    @Test
    fun `delegate - factory`() {
        val view = View(RuntimeEnvironment.getApplication())
        val delegate = OnScrollChangedListenerDelegate.invoke(view)
        // instance is cached
        Assert.assertEquals(delegate, OnScrollChangedListenerDelegate.invoke(view))
        // persisted as a tag
        Assert.assertEquals(delegate, view.getTag(R.id.adaptui_internal_scroll_delegate))
    }

    @Test
    fun `delegate - init`() {
        val view = mockt<View>()
        val delegate = OnScrollChangedListenerDelegate.invoke(view)

        val onAttachListener = kotlin.run {
            val captor = ArgumentCaptor.forClass(OnAttachStateChangeListener::class.java)
            verify(view).addOnAttachStateChangeListener(captor.capture())
            captor.value
        }

        val onScrollChangedListener = kotlin.run {
            val captor = ArgumentCaptor.forClass(OnScrollChangeListener::class.java)
            verify(view).setOnScrollChangeListener(captor.capture())
            captor.value
        }

        val listener1: OnScrollChangeListener = mockt()
        val listener2: OnScrollChangeListener = mockt()

        delegate.add(listener1)
        delegate.add(listener2)

        class Input(val x: Int, val y: Int, val oldX: Int = 0, val oldY: Int = 0) {
            fun dispatch() {
                onScrollChangedListener.onScrollChange(view, x, y, oldX, oldY)
            }

            // MUST BE MOCK
            fun verifyMock(listener: OnScrollChangeListener) {
                verify(listener).onScrollChange(eq(view), eq(x), eq(y), eq(oldX), eq(oldY))
            }
        }

        Input(10, 20, 100, 1000).also {
            it.dispatch()
            it.verifyMock(listener1)
            it.verifyMock(listener2)
        }

        delegate.remove(listener2)

        Input(9, 19, 99, 999).also {
            it.dispatch()
            it.verifyMock(listener1)

            // already removed
            verifyNoMoreInteractions(listener2)
        }

        // now, emulate detach
        onAttachListener.onViewDetachedFromWindow(view)
        verify(view).removeOnAttachStateChangeListener(eq(onAttachListener))
        verify(view).setTag(eq(R.id.adaptui_internal_scroll_delegate), eq(null))
        verify(view).setOnScrollChangeListener(eq(null))

        // okay, at end, trigger one more time the onScrollListener (that delegate registered)
        //  to validate that listener1 is removed and not longer receives events

        Input(99, -11).also {
            it.dispatch()

            verifyNoMoreInteractions(listener1)
        }
    }

    @Test
    fun addOnScrollChangedListener() {
        val view = mockt<View>()
        val listener: (View, Int, Int) -> Unit = mockt()
        val registration =  view.addOnScrollChangedListener(listener)

        val onScrollChangedListener = kotlin.run {
            val captor = ArgumentCaptor.forClass(OnScrollChangeListener::class.java)
            verify(view).setOnScrollChangeListener(captor.capture())
            captor.value
        }

        val (x, oldX) = 100 to 12
        val (y, oldY) = 99 to 202

        onScrollChangedListener.onScrollChange(view, x, y, oldX, oldY)

        verify(listener).invoke(eq(view), eq(x - oldX), eq(y - oldY))

        registration.unregisterOnScrollChangedListener()

        onScrollChangedListener.onScrollChange(view, 1000, 2000, 3000, 4000)

        verifyNoMoreInteractions(listener)
    }
}