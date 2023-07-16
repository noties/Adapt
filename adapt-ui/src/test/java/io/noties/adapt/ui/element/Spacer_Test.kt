package io.noties.adapt.ui.element

import android.view.View
import android.widget.LinearLayout
import io.noties.adapt.ui.obtainView2
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Spacer_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = mock(View::class.java)
        ElementViewFactory.Spacer = { mocked }
        assertEquals(mocked, obtainView2<LinearLayout.LayoutParams> { Spacer() })
    }

    @Test
    fun init() {
        val inputs = (0..2).map { it.toFloat() }
        for (input in inputs) {
            val view = obtainView2<LinearLayout.LayoutParams> { Spacer(input) }
            val lp = view.layoutParams as LinearLayout.LayoutParams
            assertEquals(0, lp.width)
            assertEquals(0, lp.height)
            assertEquals(input, lp.weight)
        }
    }
}