package io.noties.adapt.ui

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.ui.element.Element
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.viewgroup.AdaptViewGroup
import io.noties.adapt.viewpager.AdaptViewPager
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
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
            var adaptViewGroup: AdaptViewGroup? = null
            var adaptViewPager: AdaptViewPager? = null
            var adaptRecyclerView: AdaptRecyclerView? = null
            var adaptViewPager2: AdaptRecyclerView? = null
        }

        val ref = Ref()

        ViewFactory.createView(context) {
            ZStack {
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
}