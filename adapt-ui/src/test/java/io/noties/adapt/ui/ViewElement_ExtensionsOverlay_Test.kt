package io.noties.adapt.ui

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.view.ViewOverlay
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.shape.CapsuleShape
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.testutil.assertDensity
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ViewElement_ExtensionsOverlay_Test {
    @Test
    fun overlay() {
        assertDensity(1F)

        val (w, h) = 99 to 911
        val state = intArrayOf(android.R.attr.state_pressed)

        val inputs = listOf<(ViewElement<View, *>) -> Unit>(
            { it.overlay { Circle() } },
            { it.overlay(RectangleShape()) },
            { it.overlay(CapsuleShape().newDrawable()) }
        )

        for (input in inputs) {
            val overlay = mockt<ViewOverlay>()
            val observer = mockt<ViewTreeObserver> {
                whenever(mock.isAlive).thenReturn(true)
            }
            newElement()
                .also {
                    whenever(it.view.overlay).thenReturn(overlay)
                    whenever(it.view.viewTreeObserver).thenReturn(observer)
                    whenever(it.view.width).thenReturn(w)
                    whenever(it.view.height).thenReturn(h)
                    whenever(it.view.drawableState).thenReturn(state)
                }
                .also(input)
                .renderView {

                    val drawable = kotlin.run {
                        val captor = ArgumentCaptor.forClass(Drawable::class.java)
                        verify(overlay).add(captor.capture())
                        captor.value
                    }

                    // add callback
                    drawable.callback = mockt()

                    // initial values should have been synced
                    Assert.assertEquals(
                        Rect(0, 0, width, height).toShortString(),
                        drawable.bounds.toShortString()
                    )
                    Assert.assertEquals(state, drawable.state)

                    val listener = kotlin.run {
                        val captor =
                            ArgumentCaptor.forClass(ViewTreeObserver.OnPreDrawListener::class.java)
                        verify(observer).addOnPreDrawListener(captor.capture())
                        captor.value
                    }

                    // update mocked values
                    val (uW, uH) = 42 to 1001
                    val uState =
                        intArrayOf(android.R.attr.state_enabled, android.R.attr.state_accelerated)

                    whenever(this.width).thenReturn(uW)
                    whenever(this.height).thenReturn(uH)
                    whenever(this.drawableState).thenReturn(uState)

                    // trigger pre-draw
                    listener.onPreDraw()

                    Assert.assertEquals(
                        Rect(0, 0, uW, uH).toShortString(),
                        drawable.bounds.toShortString()
                    )
                    Assert.assertEquals(
                        uState,
                        drawable.state
                    )

                    // now, simulate detached
                    drawable.callback = null

                    listener.onPreDraw()

                    verify(observer).removeOnPreDrawListener(eq(listener))
                }
        }
    }

    @Test
    fun overlayView() {
        val (w, h) = 12 to 9999

        val overlay = mockt<ViewGroupOverlay>()

        newElementOfType<ViewGroup>()
            .also {
                whenever(it.view.context).thenReturn(RuntimeEnvironment.getApplication())
                whenever(it.view.overlay).thenReturn(overlay)
                whenever(it.view.width).thenReturn(w)
                whenever(it.view.height).thenReturn(h)
            }
            .overlayView {
                Image()
                Text()
            }
            .renderView {

                val layout = kotlin.run {
                    val captor = ArgumentCaptor.forClass(View::class.java)
                    verify(overlay).add(captor.capture())
                    captor.value
                }

                Assert.assertEquals(FrameLayout::class.java, layout::class.java)

                layout as FrameLayout

                Assert.assertEquals(2, layout.childCount)
                Assert.assertEquals(ImageView::class.java, layout.getChildAt(0)::class.java)
                Assert.assertEquals(TextView::class.java, layout.getChildAt(1)::class.java)

                // or measured values?
                Assert.assertEquals(w, layout.width)
                Assert.assertEquals(h, layout.height)

                val listener = kotlin.run {
                    val captor = ArgumentCaptor.forClass(View.OnLayoutChangeListener::class.java)
                    verify(this).addOnLayoutChangeListener(captor.capture())
                    captor.value
                }

                // update values
                val (uW, uH) = 111 to 3
                whenever(this.width).thenReturn(uW)
                whenever(this.height).thenReturn(uH)

                // values are ignored
                listener.onLayoutChange(this, 0, 0, 0, 0, 0, 0, 0, 0)

                Assert.assertEquals(uW, layout.width)
                Assert.assertEquals(uH, layout.height)

                // simulate detached
                val listeners = Shadows.shadowOf(layout).onAttachStateChangeListeners
                Assert.assertEquals(1, listeners.size)
                listeners.first().onViewDetachedFromWindow(this)

                verify(this).removeOnLayoutChangeListener(eq(listener))
            }
    }
}