package io.noties.adapt.ui.element

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactoryConstants
import io.noties.adapt.ui.background
import io.noties.adapt.ui.newElement
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.newElementOfTypeLayout
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ElementStyleTest {

    @Test
    fun `factory - invoke`() {
        val block: ViewFactoryConstants.(ViewElement<View, LayoutParams>) -> Unit = mockt()
        newElement()
            .style(ElementStyle(block))
            .also { element ->
                element.render()
                verify(block).invoke(eq(ViewFactoryConstants.Impl), eq(element))
            }
    }

    @Test
    fun `factory - generic`() {
        val block: ViewFactoryConstants.(ViewElement<out View, out LayoutParams>) -> Unit = mockt()
        newElement()
            .style(ElementStyle.generic(block))
            .also { element ->
                element.render()
                verify(block).invoke(eq(ViewFactoryConstants.Impl), eq(element))
            }
    }

    @Test
    fun `factory - view`() {
        val block: ViewFactoryConstants.(ViewElement<TextView, out LayoutParams>) -> Unit = mockt()
        newElementOfType<TextView>()
            .style(ElementStyle.view(block))
            .also { element ->
                element.render()
                verify(block).invoke(eq(ViewFactoryConstants.Impl), eq(element))
            }
    }

    @Test
    fun `factory - layout`() {
        val block: ViewFactoryConstants.(ViewElement<out View, LinearLayout.LayoutParams>) -> Unit =
            mockt()
        newElementOfTypeLayout<View, LinearLayout.LayoutParams>()
            .style(ElementStyle.layout(block))
            .also { element ->
                element.render()
                verify(block).invoke(eq(ViewFactoryConstants.Impl), eq(element))
            }
    }

    @Test
    fun `factory - viewLayout`() {
        val block: ViewFactoryConstants.(ViewElement<ImageView, FrameLayout.LayoutParams>) -> Unit =
            mockt()
        newElementOfTypeLayout<ImageView, FrameLayout.LayoutParams>()
            .style(ElementStyle.viewLayout(block))
            .also { element ->
                element.render()
                verify(block).invoke(eq(ViewFactoryConstants.Impl), eq(element))
            }
    }

    @Test
    fun `style overrides previous values`() {
        newElement()
            .background(1)
            .style(ElementStyle.generic { it.background(2) })
            .renderView {
                val captor = ArgumentCaptor.forClass(Int::class.java)
                verify(this, times(2)).setBackgroundColor(captor.capture())
                Assert.assertEquals(
                    listOf(1, 2),
                    captor.allValues
                )
            }
    }

    @Test
    fun `style value overridden`() {
        newElement()
            .style(ElementStyle.generic { it.background(3) })
            .background(4)
            .renderView {
                val captor = ArgumentCaptor.forClass(Int::class.java)
                verify(this, times(2)).setBackgroundColor(captor.capture())
                Assert.assertEquals(
                    listOf(3, 4),
                    captor.allValues
                )
            }
    }
}