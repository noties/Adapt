package io.noties.adapt.ui.util

import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class TextWatcherHideIfEmpty_Test {

    @Test
    fun `remove - none`() {
        // nothing should happen if there is no watcher register before
        val textView = mock(TextView::class.java)
        TextWatcherHideIfEmpty.remove(textView)

        verify(textView, times(1)).getTag(eq(TextWatcherHideIfEmpty.id))
        verify(
            textView,
            never()
        ).removeTextChangedListener(any(TextWatcher::class.java))
    }

    @Test
    fun init() {
        val textView = mock(TextView::class.java)
        val watcher = mock(TextWatcher::class.java)
        `when`(textView.getTag(eq(TextWatcherHideIfEmpty.id))).thenReturn(watcher)

        TextWatcherHideIfEmpty.init(textView)

        val captor = ArgumentCaptor.forClass(TextWatcher::class.java)

        // old one is removed
        verify(textView).removeTextChangedListener(eq(watcher))
        verify(textView).addTextChangedListener(captor.capture())

        Assert.assertNotNull(captor.value)
        Assert.assertNotEquals(watcher, captor.value)

        verify(textView).setTag(eq(TextWatcherHideIfEmpty.id), eq(captor.value))
        verify(textView).text = eq(null)
    }

    @Test
    fun watcher() {
        val textView = mock(TextView::class.java)
        val watcher = TextWatcherHideIfEmpty(textView)
        watcher.afterTextChanged(null)
        verify(textView).visibility = eq(View.GONE)
        watcher.afterTextChanged(SpannableStringBuilder("not-empty"))
        verify(textView).visibility = eq(View.VISIBLE)
    }
}