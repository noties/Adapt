package io.noties.adapt.ui.item

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ElementItem_Test {

    class MyItem : ElementItem<MyItem.Ref>(0L, { Ref() }) {
        class Ref {
            lateinit var view: View

            val isInitialized: Boolean get() = this::view.isInitialized
        }

        override fun bind(holder: Holder<Ref>) {
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            View().reference(ref::view)
        }
    }

    @Test
    fun `createHolder assigns ref`() {
        val viewGroup: ViewGroup = mockt {
            whenever(mock.context).thenReturn(RuntimeEnvironment.getApplication())
        }

        val item = MyItem()
        val holder = item.createHolder(mockt(), viewGroup)
        println(holder.ref.view)
        Assert.assertEquals(true, holder.ref.isInitialized)
    }
}