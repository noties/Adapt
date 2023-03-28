package io.noties.adapt.ui.element

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import io.noties.adapt.ui.newElementOfType
import io.noties.adapt.ui.obtainView
import io.noties.adapt.ui.renderView
import io.noties.adapt.ui.testutil.mockt
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Recycler_Test {

    @After
    fun after() {
        ElementViewFactory.reset()
    }

    @Test
    fun factory() {
        val mocked = Mockito.mock(RecyclerView::class.java, Mockito.RETURNS_MOCKS)
        ElementViewFactory.Recycler = { mocked }
        Assert.assertEquals(mocked, obtainView { Recycler() })
    }

    @Test
    fun init() {
        val view = obtainView {
            Recycler(true)
        }
        Assert.assertEquals(RecyclerView::class.java, view::class.java)
        Assert.assertEquals("hasFixedSize", true, (view as RecyclerView).hasFixedSize())
    }

    @Test
    fun recyclerLinearLayoutManager() {
        val inputs = listOf(
            true to false,
            false to true,
            true to true,
            false to false
        )

        for ((vertical, reverse) in inputs) {
            newElementOfType<RecyclerView>()
                .recyclerLinearLayoutManager(vertical, reverse)
                .renderView {
                    val captor = ArgumentCaptor.forClass(LayoutManager::class.java)
                    verify(this).layoutManager = captor.capture()
                    val lm = captor.value as LinearLayoutManager

                    Assert.assertEquals(
                        "orientation",
                        if (vertical) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL,
                        lm.orientation
                    )
                    Assert.assertEquals(
                        "reverseLayout",
                        reverse,
                        lm.reverseLayout
                    )
                }
        }
    }

    @Test
    fun recyclerGridLayoutManager() {
        val positions = listOf(1, 2, 3, 4, 3, 2, 1, 999)
        val inputs: List<Pair<Int, ((RecyclerView, spanCount: Int, position: Int) -> Int)?>> = listOf(
            2 to null,
            5 to { _, spanCount, position ->
                Assert.assertEquals("spanCount", 5, spanCount)
                positions[position]
            }
        )

        for ((spanCount, spanSizeLookup) in inputs) {
            newElementOfType<RecyclerView>()
                .recyclerGridLayoutManager(spanCount, spanSizeLookup)
                .renderView {
                    val captor = ArgumentCaptor.forClass(LayoutManager::class.java)
                    verify(this).layoutManager = captor.capture()
                    val lm = captor.value as GridLayoutManager

                    Assert.assertEquals(
                        "spanCount",
                        spanCount,
                        lm.spanCount
                    )

                    if (spanSizeLookup == null) {
                        // all positions report 1 as span (default impl)
                        for ((index, _) in positions.withIndex()) {
                            Assert.assertEquals(
                                "span-$index",
                                1,
                                lm.spanSizeLookup.getSpanSize(index)
                            )
                        }
                    } else {
                        for ((index, spans) in positions.withIndex()) {
                            Assert.assertEquals(
                                "span-$index",
                                spans,
                                lm.spanSizeLookup.getSpanSize(index)
                            )
                        }
                    }
                }
        }
    }

    @Test
    fun recyclerLayoutManager() {
        val lm = mockt<LayoutManager>()
        newElementOfType<RecyclerView>()
            .recyclerLayoutManager(lm)
            .renderView {
                verify(this).layoutManager = eq(lm)
            }
    }

    @Test
    fun recyclerDefaultItemAnimator() {
        newElementOfType<RecyclerView>()
            .recyclerDefaultItemAnimator()
            .renderView {
                val captor = ArgumentCaptor.forClass(ItemAnimator::class.java)
                verify(this).itemAnimator = captor.capture()
                Assert.assertEquals(
                    DefaultItemAnimator::class.java,
                    captor.value::class.java
                )
            }
    }

    @Test
    fun recyclerItemAnimator() {
        val ia = mockt<ItemAnimator>()
        newElementOfType<RecyclerView>()
            .recyclerItemAnimator(ia)
            .renderView {
                verify(this).itemAnimator = eq(ia)
            }
    }

    @Test
    fun recyclerOnScrollChanged() {
        val called = AtomicBoolean()

        val (dx, dy) = 101 to 987

        newElementOfType<RecyclerView>()
            .recyclerOnScrollChanged { _, deltaX, deltaY ->
                called.set(true)
                Assert.assertEquals(
                    "dx",
                    dx,
                    deltaX
                )
                Assert.assertEquals(
                    "dy",
                    dy,
                    deltaY
                )
            }
            .renderView {
                val captor = ArgumentCaptor.forClass(RecyclerView.OnScrollListener::class.java)
                verify(this).addOnScrollListener(captor.capture())

                captor.value.onScrolled(this, dx, dy)
            }

        Assert.assertEquals(true, called.get())
    }
}