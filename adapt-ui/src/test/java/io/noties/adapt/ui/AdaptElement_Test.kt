package io.noties.adapt.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.Item
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.testutil.mockt
import io.noties.adapt.view.AdaptView
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewpager.AdaptViewPager
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class AdaptElement_Test {

    private val context: Context get() = RuntimeEnvironment.getApplication()

    @Test
    fun viewGroup() {

        class Ref {
            var adaptView: AdaptView? = null
            var adaptViewGroup: AdaptViewGroup? = null
            var adaptViewPager: AdaptViewPager? = null
            var adaptRecyclerView: AdaptRecyclerView? = null
            var adaptViewPager2: AdaptRecyclerView? = null
        }

        val ref = Ref()

        ViewFactory.createView(context) {
            ZStack {
                ZStack { }
                    .adaptView()
                    .reference(ref::adaptView)

                VStack {}
                    .adaptViewGroup()
                    .reference(ref::adaptViewGroup)

                Element(::ViewPager)
                    .adaptViewPager()
                    .reference(ref::adaptViewPager)

                Element(::RecyclerView)
                    .adaptRecyclerView()
                    .reference(ref::adaptRecyclerView)

                Element(::ViewPager2)
                    .adaptViewPager2()
                    .reference(ref::adaptViewPager2)
            }
        }

        Assert.assertNotNull(ref.adaptView)
        Assert.assertEquals(FrameLayout::class.java, ref.adaptView!!.viewGroup()::class.java)

        Assert.assertNotNull(ref.adaptViewGroup)
        Assert.assertEquals(LinearLayout::class.java, ref.adaptViewGroup!!.viewGroup()::class.java)

        Assert.assertNotNull(ref.adaptViewPager)
        Assert.assertEquals(ViewPager::class.java, ref.adaptViewPager!!.viewPager()::class.java)

        Assert.assertNotNull(ref.adaptRecyclerView)
        Assert.assertEquals(
            RecyclerView::class.java,
            ref.adaptRecyclerView!!.recyclerView()!!::class.java
        )

        Assert.assertNotNull(ref.adaptViewPager2)
        // recycler adapter is used and view-pager2 is not persisted
        Assert.assertNull(ref.adaptViewPager2?.recyclerView())
    }

    @Test
    fun adaptView() {
        // if created on view-group -> this view-group is container,
        // otherwise view is considered a placeholder and its parent is used as container

        class Ref {
            lateinit var viewGroupContainer: ViewGroup
            lateinit var viewGroupAdapt: AdaptView

            lateinit var viewContainer: ViewGroup
            lateinit var viewContainerView: View
            lateinit var viewAdapt: AdaptView
        }

        val view: View = io.noties.adapt.ui.testutil.mockt()
        val item: Item<*> = io.noties.adapt.ui.testutil.mockt {
            on { createHolder(any(), any()) } doReturn Item.Holder(view)
        }

        val ref = Ref()

        @Suppress("NAME_SHADOWING")
        ViewFactory.createView(context, ref) { ref ->
            ZStack {

                // this view-group is container
                VStack {}
                    .reference(ref::viewGroupContainer)
                    .adaptView()
                    .reference(ref::viewGroupAdapt)

                // this is container for the adapt-view created with placeholder view
                HStack {

                    // this view would be immediately removed, must not be present
                    View()
                        .reference(ref::viewContainerView)
                        .adaptView(item)
                        .reference(ref::viewAdapt)

                }.reference(ref::viewContainer)

            }
        }

        // validate view-group
        Assert.assertEquals(ref.viewGroupContainer, ref.viewGroupAdapt.viewGroup())

        // validate view
        // - placeholder is not present in view
        // - view is equals to item
        Assert.assertEquals(1, ref.viewContainer.childCount)
        Assert.assertNotEquals(ref.viewContainerView, ref.viewContainer.getChildAt(0))
        Assert.assertEquals(view, ref.viewContainer.getChildAt(0))
    }
}