package io.noties.adapt.ui.element

import android.view.inputmethod.EditorInfo
import android.widget.EditText
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
class TextInput_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = Mockito.mock(EditText::class.java, Mockito.RETURNS_MOCKS)
        ElementViewFactory.TextInput = { mocked }
        assertEquals(mocked, obtainView { TextInput(0) })
    }

    @Test
    fun init() {
        val inputs = listOf(
            1,
            2,
            EditorInfo.TYPE_CLASS_PHONE
        )
        for (input in inputs) {
            val view = obtainView { TextInput(input) } as EditText
            assertEquals(input, view.inputType)
        }
    }
}