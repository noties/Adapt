package io.noties.adapt.ui.util

import android.view.ViewGroup
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewGroupUtil_Test {

    @Test
    fun test() {
        val inputs = listOf(
            0,
            1,
            9,
            10,
            100,
            999,
            1001
        )
        for (input in inputs) {
            val group = mock(ViewGroup::class.java)
            `when`(group.childCount).thenReturn(input)
            val value = group.children
            Assert.assertEquals(input, value.size)
        }
    }
}