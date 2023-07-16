package io.noties.adapt.ui

import android.view.ViewGroup
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class AdaptElement_Extensions_Test {

    @Test
    fun `onAdapt - not-initialized`() {
        val element = newElementOfType<ViewGroup>()
            .adaptViewGroup()

        Assert.assertEquals(false, element.isInitialized)
        Assert.assertEquals(0, element.callbacks.size)
        val called = AtomicInteger(0)
        element.onAdapt { called.incrementAndGet() }
        Assert.assertEquals(0, called.get())
        Assert.assertEquals(1, element.callbacks.size)

        // trigger onView
        element.init(mockt())
        Assert.assertEquals(0, element.callbacks.size)
        Assert.assertEquals(1, called.get())
    }

    @Test
    fun `onAdapt - initialized`() {
        // when initialized already -> block is triggered immediately

        val element = newElementOfType<ViewGroup>()
            .adaptViewGroup()

        element.init(mockt())

        Assert.assertEquals(true, element.isInitialized)
        Assert.assertEquals(0, element.callbacks.size)
        val called = AtomicInteger(0)
        element.onAdapt { called.incrementAndGet() }
        Assert.assertEquals(1, called.get())
        Assert.assertEquals(0, element.callbacks.size)
    }
}