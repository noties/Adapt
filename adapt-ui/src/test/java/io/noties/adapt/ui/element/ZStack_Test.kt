package io.noties.adapt.ui.element

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import io.noties.adapt.ui.obtainView
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ZStack_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = Mockito.mock(FrameLayout::class.java, Mockito.RETURNS_MOCKS)
        ElementViewFactory.ZStack = { mocked }
        assertEquals(mocked, obtainView { ZStack {  } })
    }

    @Test
    fun children() {
        val view = obtainView {
            ZStack {
                Image()
                Text()
                Progress()
            }
        } as FrameLayout

        assertEquals(3, view.childCount)

        assertEquals(ImageView::class.java, view.getChildAt(0)::class.java)
        assertEquals(TextView::class.java, view.getChildAt(1)::class.java)
        assertEquals(ProgressBar::class.java, view.getChildAt(2)::class.java)
    }
}