package io.noties.adapt.ui.util

import android.view.ViewGroup
import io.noties.adapt.Item
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.item.ElementItemNoRef
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("ClassName")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Config.TARGET_SDK])
class ItemUtil_Test {

    class MyItem : ElementItemNoRef(0L) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            TODO("Not yet implemented")
        }
    }

    class Divider: ElementItemNoRef(0L) {
        override fun ViewFactory<ViewGroup.LayoutParams>.body() {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun withIndexAsItemId() {
        val items = listOf(
            MyItem(),
            MyItem(),
            MyItem(),
            MyItem(),
        )
        assertEquals(true, items.all { it.id() == 0L })

        items.withIndexAsItemId()
            .withIndex()
            .forEach { (index, item) ->
                assertEquals(index.toLong(), item.id())
            }
    }

    @Test
    fun divided() {
        val item = MyItem()
        val divider = Divider()

        val inputs = listOf(
            listOf(),
            listOf(item),
            listOf(item, item)
        )

        for (items in inputs) {
            val result = items.divided { divider }

            val size = items.size
            if (size == 0) {
                assertEquals(result.toString(), 0, result.size)
            } else {
                val expected = size + (size - 1)
                assertEquals(result.toString(), expected, result.size)
            }
        }
    }
}