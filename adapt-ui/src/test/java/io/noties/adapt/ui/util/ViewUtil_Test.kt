package io.noties.adapt.ui.util

import android.view.View
import android.view.View.OnAttachStateChangeListener
import io.noties.adapt.ui.testutil.mockt
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewUtil_Test {

    @Test
    fun onAttachedOnce() {
        val view = mockt<View>()
        val listener: (View) -> Unit = mockt()
        view.onAttachedOnce(listener)

        val onAttachedListener = kotlin.run {
            val captor = ArgumentCaptor.forClass(OnAttachStateChangeListener::class.java)
            verify(view).addOnAttachStateChangeListener(captor.capture())
            captor.value
        }

        // no op
        onAttachedListener.onViewDetachedFromWindow(view)
        verifyNoInteractions(listener)

        // trigger
        onAttachedListener.onViewAttachedToWindow(view)

        verify(listener).invoke(eq(view))
        verify(view).removeOnAttachStateChangeListener(eq(onAttachedListener))
    }

    @Test
    fun onDetachedOnce() {
        val view = mockt<View>()
        val listener: (View) -> Unit = mockt()
        view.onDetachedOnce(listener)

        val onAttachedListener = kotlin.run {
            val captor = ArgumentCaptor.forClass(OnAttachStateChangeListener::class.java)
            verify(view).addOnAttachStateChangeListener(captor.capture())
            captor.value
        }

        // no op
        onAttachedListener.onViewAttachedToWindow(view)
        verifyNoInteractions(listener)

        // trigger
        onAttachedListener.onViewDetachedFromWindow(view)

        verify(listener).invoke(eq(view))
        verify(view).removeOnAttachStateChangeListener(eq(onAttachedListener))
    }
}