package io.noties.adapt.ui.util

import android.content.Context
import io.noties.adapt.ui.testutil.mockt
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class CachingContextWrapper_Test {
    @After
    fun after() {
        CachingContextWrapper.cache.clear()
    }

    @Test
    fun `test - invoke`() {
        val context = mock<Context>()
        val cached = mock<Context>()

        val contextWrapper = CachingContextWrapper {
            cached
        }

        Assert.assertEquals(0, CachingContextWrapper.cache.size)

        val result = contextWrapper(context)
        Assert.assertEquals(cached, result)
        Assert.assertEquals(1, CachingContextWrapper.cache.size)

        val entry = CachingContextWrapper.cache.entries.first()
        Assert.assertEquals(context, entry.key)
        Assert.assertEquals(cached, entry.value)
    }

    @Test
    fun `test - contextWrapper`() {
        val context = mock<Context>()
        val cached = mock<Context>()

        val contextWrapper = CachingContextWrapper.contextWrapper {
            cached
        }

        Assert.assertEquals(0, CachingContextWrapper.cache.size)

        val result = contextWrapper(context)
        Assert.assertEquals(cached, result)
        Assert.assertEquals(1, CachingContextWrapper.cache.size)

        val entry = CachingContextWrapper.cache.entries.first()
        Assert.assertEquals(context, entry.key)
        Assert.assertEquals(cached, entry.value)
    }

    @Test
    fun `test - alreadyWrapped`() {
        val original = mockt<Context>()
        val wrapped = mockt<Context>()

        val provider: (Context) -> Context = mockt {
            whenever(mock.invoke(any())).thenReturn(wrapped)
        }

        val wrapper = CachingContextWrapper(provider)

        val result = wrapper(original)
        Assert.assertEquals(wrapped, result)
        Assert.assertEquals(1, CachingContextWrapper.cache.size)

        val wrappedResult = wrapper(result)
        Assert.assertEquals(wrapped, wrappedResult)
        Assert.assertEquals(2, CachingContextWrapper.cache.size)

        Assert.assertEquals(wrapped, CachingContextWrapper.cache[original])
        Assert.assertEquals(wrapped, CachingContextWrapper.cache[wrapped])

        verify(provider, times(1)).invoke(eq(original))
    }
}