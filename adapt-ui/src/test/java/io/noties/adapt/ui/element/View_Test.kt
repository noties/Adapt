package io.noties.adapt.ui.element

import android.view.View
import io.noties.adapt.ui.obtainView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class View_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = Mockito.mock(View::class.java)
        ElementViewFactory.View = { mocked }
        assertEquals(mocked, obtainView { View() })
    }

}