package io.noties.adapt.ui.element

import android.widget.LinearLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.testutil.mockt
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ElementGroup_Test {

    @Test
    fun init() {
        val mocked = mockt<LinearLayout> {
            whenever(this.mock.context).thenReturn(mockt())
        }
        val result = obtainView {
            ElementGroup<LinearLayout, _, LayoutParams>(
                { mocked },
                {  }
            )
        }
        Assert.assertEquals(mocked, result)
    }

    @Test
    fun contextWrapper() {
        // verify that element is using contextWrapper

        val context = RuntimeEnvironment.getApplication()
        ElementViewFactory.contextWrapper = { context }

        // pass mocked context (it cannot create real views)
        val view = ViewFactory.createView(mockt()) {
            ElementGroup<LinearLayout, _, LayoutParams>(
                { LinearLayout(it) },
                {  },
                {  }
            )
        }

        Assert.assertEquals(context, view.context)
    }

    @After
    fun after() {
        ElementViewFactory.reset()
    }
}