package io.noties.adapt.ui.element

import android.widget.ScrollView
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class VScroll_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = Mockito.mock(ScrollView::class.java, Mockito.RETURNS_MOCKS)
        ElementViewFactory.VScroll = { mocked }
        assertEquals(mocked, obtainView { VScroll { } })
    }

    @Test
    fun children() {
        val view = obtainView {
            VScroll {
                Text()
            }
        } as ScrollView
        assertEquals(1, view.childCount)
    }

    @Test
    fun fillViewPort() {
        val input = false
        newElementOfType<ScrollView>()
            .fillViewPort(input)
            .renderView {
                Mockito.verify(this).isFillViewport = eq(input)
            }
    }
}