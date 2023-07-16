package io.noties.adapt.ui.element

import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.ui.obtainView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
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
}