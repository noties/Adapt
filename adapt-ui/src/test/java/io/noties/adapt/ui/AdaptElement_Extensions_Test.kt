package io.noties.adapt.ui

import android.view.ViewGroup
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class AdaptElement_Extensions_Test {

    @Test
    fun `onAdapt - viewGroup`() {
        val element = newElementOfType<ViewGroup>()
            .adaptViewGroup()
        Assert.assertEquals(0, element.callbacks.size)
        element.onAdapt { }
        Assert.assertEquals(1, element.callbacks.size)
    }
}