package io.noties.adapt.ui.element

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.testutil.mockt
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class Item_Test {

    lateinit var viewGroup: ViewGroup

    @Before
    fun before() {
        viewGroup = mockt {
            whenever(mock.context).thenReturn(RuntimeEnvironment.getApplication())
        }
    }

    @Test
    fun item() {
        // supplied Item would be used to create view and add to layout

        val mock = MockItem()
        val changeHandler: (View) -> Unit = mockt()

        class Ref {
            var updateItem: UpdateItem<MockItem.MyItem>? = null
        }

        val ref = Ref()

        val view  = ViewFactory.newView(viewGroup).create {
            Item(mock.item) { it.changeHandler(changeHandler) }
                .referenceUpdate(ref::updateItem)
        }

        Assert.assertEquals(mock.view, view)
        verify(mock.item).bind(eq(mock.holder))

        Assert.assertNotNull(ref.updateItem)

        ref.updateItem!!.invoke {
            // nothing, trigger update
        }

        verify(changeHandler).invoke(eq(mock.view))
        verify(mock.item, times(2)).bind(eq(mock.holder))
    }

    private class MockItem {
        abstract class MyItem : ElementItemNoRef(44L)

        val item: MyItem = mockt()
        val view: View = View(RuntimeEnvironment.getApplication())
        val holder: ElementItem.Holder<Unit> = ElementItem.Holder(view, Unit)

        init {
            whenever(item.createHolder(any(), any())).thenReturn(holder)
        }
    }
}