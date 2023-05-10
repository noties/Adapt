package io.noties.adapt.ui.sticky

import android.content.Context
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import io.noties.adapt.ui.R
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class StickyVerticalScrollTest {

    val tagContainer = R.id.adaptui_internal_sticky_scroll_container
//    val tagSticky = R.id.adaptui_internal_sticky_scroll_view

    val context: Context get() = RuntimeEnvironment.getApplication()

    @Test
    fun `instance cached`() {
        val container = LinearLayout(context)

        val instance = StickyVerticalScroll.invoke(container)
        assertEquals(instance, container.getTag(tagContainer))

        val other = StickyVerticalScroll.invoke(container)
        assertEquals(instance, other)
    }

    @Test
    fun `instance cleared when detached`() {
        val view = mockt<ViewGroup>()
        val sticky = StickyVerticalScroll.invoke(view)

        val onAttachListeners = kotlin.run {
            val captor = ArgumentCaptor.forClass(View.OnAttachStateChangeListener::class.java)
            verify(view, atLeast(2)).addOnAttachStateChangeListener(captor.capture())
            captor.allValues
        }

        verify(view).setTag(eq(tagContainer), eq(sticky))

        // NB! might fail if run for SDK < 23 (M)
        verify(view).setOnScrollChangeListener(any())

        onAttachListeners.forEach {
            it.onViewDetachedFromWindow(view)
            verify(view).removeOnAttachStateChangeListener(eq(it))
        }

        verify(view).setOnScrollChangeListener(eq(null))
        verify(view).setTag(eq(tagContainer), eq(null))
    }

    @Test
    fun `findStickyScrollFromChild - no parent`() {
        // no parent -> returns null
        val view = mockt<View>()
        val sticky = StickyVerticalScroll.findStickyScrollFromChild(view)
        assertNull(sticky)
    }

    @Test
    fun `findStickyScrollFromChild - has parent - no sticky tag`() {
        // view is attached, has parent, but parent is not initialized with sticky tag
        val view = mockt<View>()
        val parent1 = mockt<ViewGroup>()
        val parent2 = mockt<ViewGroup>()
        whenever(view.parent).thenReturn(parent1)
        whenever(parent1.parent).thenReturn(parent2)

        val sticky = StickyVerticalScroll.findStickyScrollFromChild(view)
        assertNull(sticky)

        verify(view).parent
        verify(parent1).parent
        verify(parent2).parent
    }

    @Test
    fun `findStickyScrollFromChild - has parent - has sticky tag`() {
        // view is attached, has parent, but parent is not initialized with sticky tag
        val view = mockt<View>()
        val parent1 = mockt<ViewGroup>()
        val parent2 = mockt<ViewGroup>()
        whenever(view.parent).thenReturn(parent1)
        whenever(parent1.parent).thenReturn(parent2)

        val sticky = StickyVerticalScroll(parent1)
        whenever(parent1.getTag(eq(tagContainer))).thenReturn(sticky)

        val instance = StickyVerticalScroll.findStickyScrollFromChild(view)

        verify(view).parent
        verify(parent1, atLeast(1)).getTag(eq(tagContainer))
        assertEquals(sticky, instance)

        verify(parent1, never()).parent
        verify(parent2, never()).parent
    }

    @Test
    fun stickyVerticalScrollContainer() {
        // applies tag
        newElementOfType<ViewGroup>()
            .stickyVerticalScrollContainer()
            .renderView {
                val sticky = kotlin.run {
                    val captor = ArgumentCaptor.forClass(StickyVerticalScroll::class.java)
                    verify(this).setTag(eq(tagContainer), captor.capture())
                    captor.value
                }
                assertEquals(StickyVerticalScroll::class.java, sticky::class.java)
            }
    }

    @Test
    fun `stickyView - attached`() {
        // properly identifies parent
        val view = mockt<View>()
        whenever(view.isAttachedToWindow).thenReturn(true)

        val container = LinearLayout(context)
        val sticky = StickyVerticalScroll.invoke(container)
        container.addView(view)

        whenever(view.parent).thenReturn(container)

        lateinit var element: ViewElement<in View, *>

        ViewFactory.createView(context) {
            element = Element { view }
        }

        element.stickyView()

        assertEquals(true, sticky.containsStickyView(view))
    }

    @Test
    fun stickyView() {
        // properly identifies parent

        val view = mockt<View>()

        val scrollView = ViewFactory.createView(context) {
            VScroll {
                VStack {
                    Element { view }
                        .stickyView()
                }
            }.stickyVerticalScrollContainer()
        } as ScrollView

        whenever(view.parent).thenReturn(scrollView)

        // do we need to send attach event?
        val onAttachListeners = kotlin.run {
            val captor = ArgumentCaptor.forClass(OnAttachStateChangeListener::class.java)
            verify(view).addOnAttachStateChangeListener(captor.capture())
            captor.allValues
        }

        val sticky = scrollView.getTag(tagContainer) as StickyVerticalScroll

        assertEquals(false, sticky.containsStickyView(view))

        // after attached, another listener will be added to listen for detach events
        // mark as attached
        whenever(view.isAttachedToWindow).thenReturn(true)
        onAttachListeners.forEach { it.onViewAttachedToWindow(view) }

        assertEquals(true, sticky.containsStickyView(view))

        // when view is detached, it will be automatically removed from being referenced by sticky
        //   query for new listeners
        kotlin.run {
            val captor = ArgumentCaptor.forClass(OnAttachStateChangeListener::class.java)
            verify(view, atLeast(1)).addOnAttachStateChangeListener(captor.capture())
            captor.allValues
        }.forEach { it.onViewDetachedFromWindow(view) }

        assertEquals(false, sticky.containsStickyView(view))
    }

    @Test
    fun `addStickyView - different parent`() {
        val container = LinearLayout(context)
        val sticky = StickyVerticalScroll.invoke(container)

        val view = mockt<View>()
        whenever(view.isAttachedToWindow).thenReturn(true)
        whenever(view.parent).thenReturn(mockt())

        try {
            sticky.addStickyView(view)
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
            assertTrue(t.message!!, t.message!!.contains("Supplied view is not a child of"))
        }
    }

    @Test
    fun removeStickyView() {
        val container = LinearLayout(context)
        val sticky = StickyVerticalScroll.invoke(container)

        val view = mockt<View>()
        whenever(view.isAttachedToWindow).thenReturn(true)
        whenever(view.parent).thenReturn(container)

        sticky.addStickyView(view)

        assertEquals(true, sticky.containsStickyView(view))

        sticky.removeStickyView(view)

        assertEquals(false, sticky.containsStickyView(view))
    }
}