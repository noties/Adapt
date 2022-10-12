package io.noties.adapt.ui.element

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.util.Gravity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class HStack_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun init() {
        val view = obtainView { HStack { } }
        assertEquals(LinearLayout::class.java, view::class.java)

        // additionally has orientation set to HORIZONTAL
        assertEquals(LinearLayout.HORIZONTAL, (view as LinearLayout).orientation)

        // default gravity
        assertEquals(Gravity.leading.center.value, view.gravity)
    }

    @Test
    fun `init - gravity`() {
        val gravity = Gravity.trailing.top
        val view = obtainView { HStack(gravity) { } }
        assertEquals(LinearLayout::class.java, view::class.java)

        // additionally has orientation set to HORIZONTAL
        assertEquals(LinearLayout.HORIZONTAL, (view as LinearLayout).orientation)

        // default gravity
        assertEquals(gravity.value, view.gravity)
    }

    @Test
    fun factory() {
        val mocked = mock(LinearLayout::class.java, RETURNS_MOCKS)
        ElementViewFactory.HStack = { mocked }
        assertEquals(mocked, obtainView { HStack { } })
    }

    @Test
    fun children() {
        val view = obtainView {
            HStack {
                Text()
                Image()
                Progress()
            }
        } as LinearLayout
        assertEquals(3, view.childCount)
        assertEquals(TextView::class.java, view.getChildAt(0)::class.java)
        assertEquals(ImageView::class.java, view.getChildAt(1)::class.java)
        assertEquals(ProgressBar::class.java, view.getChildAt(2)::class.java)
    }
}