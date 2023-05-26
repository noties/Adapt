package io.noties.adapt.ui.element

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.ShapeDrawable
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Pager_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = mock(ViewPager::class.java, RETURNS_MOCKS)
        ElementViewFactory.Pager = { mocked }
        assertEquals(mocked, obtainView { Pager { } })
    }

    @Test
    fun `init - empty`() {
        val vp = obtainView { Pager { } } as ViewPager
        val adapter = vp.adapter
        assertNotNull(adapter)
        assertEquals(0, adapter!!.count)
    }

    @Test
    fun `init - decor only`() {
        val mocked = mock(ViewPager::class.java, RETURNS_MOCKS)
        ElementViewFactory.Pager = { mocked }
        val listener = mock(ViewPagerOnPageChangeListener::class.java)

        val vp = obtainView {
            Pager {

                Text("DECOR")
                    .pagerDecor()
                    // decor does not use this listener
                    .pagerOnPageSelectedListener { }

                Text("DECOR WITH LISTENER")
                    .pagerDecor()
                    .pagerOnPageChangedListener(listener)
                    .pagerOnPageSelectedListener { }
            }
        } as ViewPager

        assertEquals(mocked, vp)

        // verify adapter has no items
        val adapter = kotlin.run {
            val captor = ArgumentCaptor.forClass(PagerAdapter::class.java)
            verify(mocked).adapter = captor.capture()
            captor.value
        }
        assertNotNull(adapter)
        assertEquals(0, adapter.count)

        // verify layout params for decor views
        // verify page listener from decor is added
        val children = kotlin.run {
            val captor = ArgumentCaptor.forClass(View::class.java)
            verify(vp, times(2)).addView(captor.capture())
            captor.allValues
        }
        assertEquals(2, children.size)
        children
            .withIndex()
            .forEach { (i, view) ->
                val lp = view.layoutParams
                assertNotNull(lp)
                assertTrue(lp is ViewPagerLayoutParams)
                assertEquals(vp, (lp as ViewPagerLayoutParams).viewPager)
                assertNull(lp.onPageSelectedListener)
                assertTrue(lp.isDecor)

                if (i == 0) {
                    assertNull(lp.onPageChangeListener)
                } else {
                    assertEquals(listener, lp.onPageChangeListener)
                }
            }

        verify(listener).onPageSelected(eq(0))
        verify(vp).addOnPageChangeListener(eq(listener))
    }

    @Test
    fun `init - only items`() {
        val mocked = mock(ViewPager::class.java, RETURNS_MOCKS)
        `when`(mocked.context).thenReturn(RuntimeEnvironment.getApplication())
        ElementViewFactory.Pager = { mocked }

        val onPageSelectedListener: ViewPagerOnPageSelectedListener = {}
        val onPageChangedListener: ViewPagerOnPageChangeListener =
            object : ViewPagerOnPageChangeListener() {}

        val vp = obtainView {
            Pager {
                Text("First")
                    .pagerPageWidthRatio(1 / 3F)
                    .pagerOnPageSelectedListener(onPageSelectedListener)
                    .pagerOnPageChangedListener(onPageChangedListener)
                Image()
                    .pagerPageWidthRatio(2 / 3F)
                    .pagerOnPageSelectedListener(onPageSelectedListener)
                    .pagerOnPageChangedListener(onPageChangedListener)
                Progress()
                    .pagerPageWidthRatio(3 / 3F)
                    .pagerOnPageSelectedListener(onPageSelectedListener)
                    .pagerOnPageChangedListener(onPageChangedListener)
            }
        } as ViewPager

        assertEquals(mocked, vp)

        // assert no decor views were added
        val children = run {
            val captor = ArgumentCaptor.forClass(View::class.java)
            verify(vp, never()).addView(captor.capture())
            captor.allValues
        }
        assertEquals(children.toString(), 0, children.size)

        val adapter = run {
            val captor = ArgumentCaptor.forClass(PagerAdapter::class.java)
            verify(vp, times(1)).adapter = captor.capture()
            captor.value
        }
        assertEquals(3, adapter.count)

        (0..2)
            .forEach {
                assertEquals(it.toString(), (it + 1) / 3F, adapter.getPageWidth(it))
                adapter.instantiateItem(vp, it)
            }

        val views = run {
            val captor = ArgumentCaptor.forClass(View::class.java)
            verify(vp, times(3)).addView(captor.capture())
            captor.allValues
        }
        assertEquals(3, views.size)

        for (view in views) {
            val lp = view.layoutParams as ViewPagerLayoutParams
            assertEquals(vp, lp.viewPager)
            assertEquals(onPageChangedListener, lp.onPageChangeListener)
            assertEquals(onPageSelectedListener, lp.onPageSelectedListener)
            assertFalse(lp.isDecor)
        }

        // 3 elements with 2 listeners
        verify(
            vp,
            times(6)
        ).addOnPageChangeListener(any(ViewPager.OnPageChangeListener::class.java))
    }

    @Test
    fun `init - items+decor`() {
        val mocked = mock(ViewPager::class.java, RETURNS_MOCKS)
        `when`(mocked.context).thenReturn(RuntimeEnvironment.getApplication())
        ElementViewFactory.Pager = { mocked }

        val vp = obtainView {
            Pager {
                Text()
                    .pagerDecor()
                Text()
            }
        } as ViewPager

        verify(vp, times(1)).addView(any(View::class.java))

        val adapter = kotlin.run {
            val captor = ArgumentCaptor.forClass(PagerAdapter::class.java)
            verify(vp, times(1)).adapter = captor.capture()
            captor.value
        }
        assertEquals(1, adapter.count)
    }

    @Test
    fun viewPager() {
        val vp = mock(ViewPager::class.java)
        `when`(vp.context).thenReturn(RuntimeEnvironment.getApplication())

        ElementViewFactory.Pager = { vp }

        lateinit var element: ViewElement<out View, ViewPagerLayoutParams>

        obtainView {
            Pager {
                // mark as decor, as regular views would be rendered by adapter
                element = Text()
                    .pagerDecor()
            }
        }

        assertEquals(vp, element.viewPager)
    }

    @Test
    fun pagerCurrentItem() {
        val inputs = listOf(
            1 to false,
            2 to true
        )
        for ((page, smooth) in inputs) {
            newViewPager()
                .pagerCurrentItem(page, smooth)
                .renderView {
                    verify(this).setCurrentItem(eq(page), eq(smooth))
                }
        }
    }

    @Test
    fun pagerPageTransformer() {
        val inputs = listOf(
            Triple(false, 1, null),
            Triple(true, 2, mock(ViewPager.PageTransformer::class.java))
        )
        for ((reverse, type, transformer) in inputs) {
            newViewPager()
                .pagerPageTransformer(reverse, type, transformer)
                .renderView {
                    verify(this).setPageTransformer(
                        eq(reverse),
                        eq(transformer),
                        eq(type),
                    )
                }
        }
    }

    @Test
    fun pagerOffscreenPageLimit() {
        val input = 99
        newViewPager()
            .pagerOffscreenPageLimit(input)
            .renderView {
                verify(this).offscreenPageLimit = eq(input)
            }
    }

    @Test
    fun `pagerPageMargin - drawable`() {
        val inputs = listOf(
            1 to null,
            2 to mock(Drawable::class.java)
        )
        for ((margin, drawable) in inputs) {
            newViewPager()
                .pagerPageMargin(margin, drawable)
                .renderView {
                    verify(this).pageMargin = eq(margin.dip)
                    verify(this).setPageMarginDrawable(eq(drawable))
                }
        }
    }

    @Test
    fun `pagerPagerMargin - shape`() {
        val (margin, shape) = 3 to RectangleShape()
        newViewPager()
            .pagerPageMargin(margin, shape)
            .renderView {
                verify(this).pageMargin = eq(margin)

                val captor = ArgumentCaptor.forClass(Drawable::class.java)
                verify(this).setPageMarginDrawable(captor.capture())

                val d = captor.value
                assertTrue(d is ShapeDrawable<*>)
                assertEquals(shape, (d as ShapeDrawable<*>).shape)
            }
    }

    @Test
    fun `pagerPagerMargin - shape-factory`() {
        val (margin, shape) = 3 to RectangleShape()
        newViewPager()
            .pagerPageMargin(margin) { shape }
            .renderView {
                verify(this).pageMargin = eq(margin)

                val captor = ArgumentCaptor.forClass(Drawable::class.java)
                verify(this).setPageMarginDrawable(captor.capture())

                val d = captor.value
                assertTrue(d is ShapeDrawable<*>)
                assertEquals(shape, (d as ShapeDrawable<*>).shape)
            }
    }

    @Test
    fun `pagerOnPageChangedListener - no adapter`() {
        // if we have adapter, page listener would receive initial callback with
        //  currently selected page
        val mocked = mock(ViewPager::class.java).also {
            `when`(it.context).thenReturn(RuntimeEnvironment.getApplication())
        }
        ElementViewFactory.Pager = { mocked }

        val listener = mock(ViewPagerOnPageChangeListener::class.java)

        val vp = obtainView {
            Pager {}
                .pagerOnPageChangedListener(listener)
        } as ViewPager

        assertEquals(mocked, vp)

        // not triggered
        verify(listener, never()).onPageSelected(anyInt())
        verify(mocked).addOnPageChangeListener(eq(listener))
        assertEquals(mocked, listener.viewPager)
        assertEquals(0, listener.pagesCount)
    }

    @Test
    fun `pagerOnPageChangedListener - adapter`() {
        // if we have adapter, page listener would receive initial callback with
        //  currently selected page
        val mocked = mock(ViewPager::class.java).also {
            `when`(it.adapter).thenReturn(mock(PagerAdapter::class.java))
            `when`(it.context).thenReturn(RuntimeEnvironment.getApplication())
        }
        ElementViewFactory.Pager = { mocked }

        val listener = mock(ViewPagerOnPageChangeListener::class.java)

        val vp = obtainView {
            Pager {}
                .pagerOnPageChangedListener(listener)
        } as ViewPager

        assertEquals(mocked, vp)

        // not triggered
        verify(listener).onPageSelected(eq(0))
        verify(mocked).addOnPageChangeListener(eq(listener))
        assertEquals(mocked, listener.viewPager)
        assertEquals(0, listener.pagesCount)
    }

    @Test
    fun pagerDecor() {
        val mocked = mock(ViewPager::class.java, RETURNS_MOCKS).also {
            `when`(it.context).thenReturn(RuntimeEnvironment.getApplication())
        }
        ElementViewFactory.Pager = { mocked }

        val vp = obtainView {
            Pager {
                Text()
                    .pagerDecor()
            }
        } as ViewPager

        assertEquals(mocked, vp)

        val view = kotlin.run {
            val captor = ArgumentCaptor.forClass(View::class.java)
            verify(vp).addView(captor.capture())
            captor.value
        }

        assertTrue(view is TextView)

        val lp = view.layoutParams as ViewPagerLayoutParams
        assertTrue(lp.isDecor)
    }

    @Test
    fun `pagerDecor - gravity`() {
        val mocked = mock(ViewPager::class.java, RETURNS_MOCKS).also {
            `when`(it.context).thenReturn(RuntimeEnvironment.getApplication())
        }
        ElementViewFactory.Pager = { mocked }

        val gravity = Gravity.center.trailing

        val vp = obtainView {
            Pager {
                Text()
                    .pagerDecor(gravity)
            }
        } as ViewPager

        assertEquals(mocked, vp)

        val view = kotlin.run {
            val captor = ArgumentCaptor.forClass(View::class.java)
            verify(vp).addView(captor.capture())
            captor.value
        }

        assertTrue(view is TextView)

        val lp = view.layoutParams as ViewPagerLayoutParams
        assertTrue(lp.isDecor)
        assertEquals(gravity.value, lp.gravity)
    }

    @Test
    fun pagerPageWidthRatio() {
        val vp = obtainView {
            Pager {
                (0..2).forEach {
                    Text()
                        .pagerPageWidthRatio((it + 1) / 3F)
                }
            }
        } as ViewPager

        val adapter = vp.adapter
        assertNotNull(adapter)

        assertEquals(3, adapter!!.count)

        // run binding
        (0..2).forEach {
            val view = adapter.instantiateItem(vp, it) as View
            val lp = view.layoutParams as ViewPagerLayoutParams

            val ratio = (it + 1) / 3F
            assertEquals(ratio, lp.pageWidthRatio)
            assertEquals(ratio, adapter.getPageWidth(it))
        }
    }

    private fun newViewPager() = newElementOfType<ViewPager>()
}