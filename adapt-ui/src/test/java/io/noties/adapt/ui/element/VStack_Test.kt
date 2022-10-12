package io.noties.adapt.ui.element

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.util.Gravity
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
class VStack_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = Mockito.mock(LinearLayout::class.java, Mockito.RETURNS_MOCKS)
        ElementViewFactory.VStack = { mocked }
        assertEquals(mocked, obtainView { VStack { } })
    }

    @Test
    fun init() {
        val inputs: List<Pair<Gravity, ViewFactory<LayoutParams>.() -> Unit>> = listOf(
            // default no-arg init
            Gravity.center.top to { VStack { } },
            Gravity.bottom.leading to { VStack(Gravity.bottom.leading) { } }
        )
        for ((gravity, block) in inputs) {
            val view = obtainView(block) as LinearLayout
            assertEquals(LinearLayout.VERTICAL, view.orientation)
            assertEquals(gravity.value, view.gravity)
        }
    }

    @Test
    fun children() {
        val view = obtainView {
            VStack {
                Text()
                Progress()
                Image()
            }
        } as LinearLayout
        assertEquals(3, view.childCount)

        assertEquals(TextView::class.java, view.getChildAt(0)::class.java)
        assertEquals(ProgressBar::class.java, view.getChildAt(1)::class.java)
        assertEquals(ImageView::class.java, view.getChildAt(2)::class.java)
    }
}