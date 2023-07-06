package io.noties.adapt.ui.element

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Inflated_Test {

    @Test
    fun `element - no-layout-inflater`() {
        val id = 8
        val layoutInflater = mockt<LayoutInflater>(Mockito.RETURNS_MOCKS)
        val context = mockt<Context> {
            whenever(mock.getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE))).thenReturn(
                layoutInflater
            )
        }

        @Suppress("UNUSED_VARIABLE")
        val view = ViewFactory.createView(context) {
            val element = Inflated(id)
            Assert.assertEquals(InflatedViewElement::class, element::class)
        }
        verify(context).getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE))
        verify(layoutInflater).inflate(eq(id), eq(null), eq(false))
    }

    @Test
    fun `element - layout-inflater`() {
        val id = 98
        val layoutInflater = mockt<LayoutInflater>(Mockito.RETURNS_MOCKS)
        val context = mockt<Context>()

        @Suppress("UNUSED_VARIABLE")
        val view = ViewFactory.createView(context) {
            Inflated(id, layoutInflater)
        }
        verify(context, never()).getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE))
        verify(layoutInflater).inflate(eq(id), eq(null), eq(false))
    }

    @Test
    fun `element - parent`() {
        val id = 33
        val layoutInflater = mockt<LayoutInflater>(Mockito.RETURNS_MOCKS)
        val context = mockt<Context> {
            whenever(mock.getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE))).thenReturn(
                layoutInflater
            )
        }
        val viewGroup = mockt<ViewGroup> {
            whenever(mock.context).thenReturn(context)
        }

        @Suppress("UNUSED_VARIABLE")
        val view = ViewFactory.newView(viewGroup).create {
            Inflated(id)
        }
        verify(context).getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE))
        verify(viewGroup).context
        verify(layoutInflater).inflate(eq(id), eq(viewGroup), eq(false))
    }

    @Test
    fun inflatedView() {
        val layoutId = 99
        val viewId = 101
        val context = mockt<Context>()
        val view = mockt<TextView> {
            whenever(mock.context).thenReturn(context)
        }
        val inflatedView = mockt<View> {
            whenever(mock.findViewById<View?>(eq(viewId))).thenReturn(view)
        }
        val layoutInflater = mockt<LayoutInflater> {
            whenever(mock.inflate(eq(layoutId), eq(null), eq(false))).thenReturn(inflatedView)
        }
        val factory = ViewFactory<LayoutParams>(context)
        val element = factory.Inflated(layoutId, layoutInflater)
        factory.consumeElements().forEach { it.init(context) }

        var viewElement: ViewElement<in TextView, *>? = null
        element
            .inflatedView(viewId) {
                viewElement = it
            }
            .render()
        Assert.assertNotNull(viewElement)
        Assert.assertEquals(view, viewElement!!.view)
    }

    @Test
    fun `inflatedView - not-found`() {
        val layoutId = 45
        val viewId = 9

        val resources = mockt<Resources> {
            whenever(mock.getResourceName(any())).thenThrow(Resources.NotFoundException::class.java)
        }
        val context = mockt<Context> {
            whenever(mock.resources).thenReturn(resources)
        }
        val view = mockt<View> {
            whenever(mock.context).thenReturn(context)
        }
        val layoutInflater = mockt<LayoutInflater> {
            whenever(mock.inflate(eq(layoutId), eq(null), eq(false))).thenReturn(view)
        }
        val factory = ViewFactory<LayoutParams>(context)
        val element = factory.Inflated(layoutId, layoutInflater)
        factory.consumeElements().forEach { it.init(context) }

        try {
            element.inflatedView<View, _>(viewId) {
                throw IllegalStateException("Unexpected, should not be called")
            }.render()
            Assert.fail()
        } catch (t: IllegalStateException) {
            Assert.assertEquals("View with id:'$viewId' not found in layout:'$layoutId'", t.message)
        }
    }
}