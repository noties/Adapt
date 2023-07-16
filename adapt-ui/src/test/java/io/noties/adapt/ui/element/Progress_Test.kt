package io.noties.adapt.ui.element

import android.content.res.ColorStateList
import android.widget.ProgressBar
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Progress_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = mock(ProgressBar::class.java, Mockito.RETURNS_MOCKS)
        ElementViewFactory.Progress = { mocked }
        assertEquals(mocked, obtainView { Progress() })
    }

    @Test
    fun init() {
        val view = obtainView {
            Progress()
        }
        assertEquals(ProgressBar::class.java, view::class.java)
        assertEquals("indeterminate", true, (view as ProgressBar).isIndeterminate)
    }

    @Test
    fun `progressTint - color`() {
        val inputs = listOf(
            4567,
            9876512
        )
        for (color in inputs) {
            newElementOfType<ProgressBar>()
                .progressTint(color)
                .renderView {
                    val value = ColorStateList.valueOf(color)
                    verify(this).indeterminateTintList = eq(value)
                }
        }
    }

    @Test
    fun `progressTint - colorStateList`() {
        val inputs = listOf(
            ColorStateList.valueOf(1234),
            ColorStateList.valueOf(9876512)
        )
        for (colorStateList in inputs) {
            newElementOfType<ProgressBar>()
                .progressTint(colorStateList)
                .renderView {
                    verify(this).indeterminateTintList = eq(colorStateList)
                }
        }
    }
}