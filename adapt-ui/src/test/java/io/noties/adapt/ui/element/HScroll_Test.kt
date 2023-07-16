package io.noties.adapt.ui.element

import android.widget.HorizontalScrollView
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class HScroll_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun init() {
        val view = obtainView { HScroll { } }
        assertEquals(HorizontalScrollView::class.java, view::class.java)
    }

    @Test
    fun factory() {
        val mocked = mock(HorizontalScrollView::class.java, RETURNS_MOCKS)
        ElementViewFactory.HScroll = { mocked }
        assertEquals(mocked, obtainView { HScroll { } })
    }

    @Test
    fun `fillViewPort - default`() {
        newElementOfType<HorizontalScrollView>()
            .scrollFillViewPort()
            .renderView {
                verify(this).isFillViewport = eq(true)
            }
    }

    @Test
    fun fillViewPort() {
        val input = false
        newElementOfType<HorizontalScrollView>()
            .scrollFillViewPort(false)
            .renderView {
                verify(this).isFillViewport = eq(input)
            }
    }

    @Test
    fun children() {
        val view = obtainView {
            HScroll {
                Text()
            }
        } as HorizontalScrollView
        assertEquals(1, view.childCount)
    }
}