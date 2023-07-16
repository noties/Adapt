package io.noties.adapt.ui.util

import android.view.ViewGroup
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

    @Test
    fun test() {
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
}