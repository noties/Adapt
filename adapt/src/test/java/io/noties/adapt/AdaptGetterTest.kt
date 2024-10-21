package io.noties.adapt

import android.view.LayoutInflater
import android.view.ViewGroup
import io.noties.adapt.kt.getter
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime

@RunWith(RobolectricTestRunner::class)
class AdaptGetterTest {

    abstract class BaseItem(
        id: Long,
        val myOwnPropertyOfBaseItem: Int = 42
    ) : Item<Item.Holder>(id) {
        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder = TODO()
        override fun bind(holder: Holder) = TODO()
    }

    class SomeItem(
        id: Long,
        var isSelected: Boolean
    ) : BaseItem(id)

    open class SomeOtherItem(
        id: Long,
        val dateTime: LocalDateTime
    ) : BaseItem(id)

    class OtherThis(id: Long) : SomeOtherItem(id, dateTime = LocalDateTime.MIN)
    class OtherThat(id: Long) : SomeOtherItem(id, dateTime = LocalDateTime.MAX)

    @Test
    fun cast() {
        val items = generateTestItems()
        val adapt = createAdapt(items)

        // fine
        run {
            // should be fine, as all items are subtypes of this type
            val getter = adapt.getter {
                cast(BaseItem::class.java)
            }

            // ensure no runtime error: print each
            getter.items().forEach { println(it.myOwnPropertyOfBaseItem) }
        }

        run {
            // will throw, as some of items are not subtypes of this type
            val getter = adapt.getter {
                cast(SomeOtherItem::class.java)
            }

            var handled = false
            try {
                getter.items().forEach { println(it.dateTime) }
            } catch (t: Throwable) {
                t.printStackTrace()
                handled = true
            }
            assert(handled)
        }
    }

    private fun createAdapt(items: List<Item<*>>): Adapt {
        return TestAdapt(items)
    }

    private fun generateTestItems(): List<Item<*>> {
        // start with all possible types to make predictable results
        //  then generate more items randomly
        val items = mutableListOf(
            SomeItem(0L, true),
            OtherThis(0L),
            OtherThat(0L)
        )
        repeat(10) { r ->
            (0..2)
                .random()
                .let {
                    when (it) {
                        0 -> SomeItem(r.toLong(), r % 2 == 0)
                        1 -> OtherThis(r.toLong())
                        2 -> OtherThat(r.toLong())
                        else -> error("Should not have happened")
                    }
                }
                .also {
                    items.add(it)
                }
        }
        return items
    }

    private class TestAdapt(
        val initialItems: List<Item<*>>
    ) : Adapt {

        class Notifications {
            var all = 0
                private set

            private class Specific(
                val item: Item<*>,
                var isSeen: Boolean = false
            )

            private val specifics = mutableListOf<Specific>()

            // return specific events, by default `new` = true, so only _new_
            //  items are returned - new as is first time returned (first time seen)
            fun specific(new: Boolean = true): List<Item<*>> {
                return specifics
                    .let {
                        if (new) {
                            it.filter { !it.isSeen }
                        } else {
                            it
                        }
                    }
                    .onEach { it.isSeen = true }
                    .map { it.item }
            }

            fun incrementAll() {
                all += 1
            }

            fun incrementSpecific(item: Item<*>) {
                specifics.add(Specific(item))
            }
        }

        val notifications = Notifications()

        // copy initial, but do not process
        var items = initialItems.toList()

        override fun items(): List<Item<*>> = items

        override fun setItems(items: List<Item<*>>?) {
            this.items = items ?: emptyList()
        }

        override fun notifyAllItemsChanged() {
            notifications.incrementAll()
        }

        override fun notifyItemChanged(item: Item<*>) {
            notifications.incrementSpecific(item)
        }
    }
}