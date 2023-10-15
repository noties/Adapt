package io.noties.adapt.ui.element

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.testutil.mockt
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Element_Test {

    @Test
    fun init() {
        val mocked = mock(RecyclerView::class.java)
        val result = obtainView {
            Element { mocked }
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
            Element { View(it) }
        }

        Assert.assertEquals(context, view.context)
    }

    @After
    fun after() {
        ElementViewFactory.reset()
    }
}