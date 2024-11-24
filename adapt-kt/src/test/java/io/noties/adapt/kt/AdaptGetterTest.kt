package io.noties.adapt.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptGetterTest {

    @Test
    fun empty() {
        val adapt = mockAdapt(emptyList())

        val getter = adapt.getter<Item<*>>()
        assertEquals(
            adapt.items(),
            getter.items()
        )
    }

    @Test
    fun filter() {
        // 3 instances of each type
        val adapt = mockAdapt(
            (0 until 9)
                .map {
                    val id = it.toLong()
                    when (val rem = it % 3) {
                        0 -> MockItem1(id)
                        1 -> MockItem2(id)
                        2 -> UnrelatedItem(id)
                        else -> error("Unexpected remainder:$rem from:$it")
                    }
                }
        )

        // ------------
        // filter by id
        // ------------

        // 0, 3, 6
        run {
            val getter = adapt.getter {
                filter { it.id() % 3L == 0L }
            }

            getter.items()
                .map { it.id() }
                .also {
                    assertEquals(listOf(0L, 3L, 6L), it)
                }

            getter.items()
                .map { it.javaClass }
                .also {
                    assertEquals((0 until 3).map { MockItem1::class.java }, it)
                }
        }

        // 1, 4, 7
        run {
            val getter = adapt.getter {
                filter { it.id() % 3L == 1L }
            }

            getter.items()
                .map { it.id() }
                .also {
                    assertEquals(listOf(1L, 4L, 7L), it)
                }

            getter.items()
                .map { it.javaClass }
                .also {
                    assertEquals((0 until 3).map { MockItem2::class.java }, it)
                }
        }

        // 2, 5, 8
        run {
            val getter = adapt.getter {
                filter { it.id() % 3L == 2L }
            }

            getter.items()
                .map { it.id() }
                .also {
                    assertEquals(listOf(2L, 5L, 8L), it)
                }

            getter.items()
                .map { it.javaClass }
                .also {
                    assertEquals((0 until 3).map { UnrelatedItem::class.java }, it)
                }
        }

        // -----
        // even id, mixture of item types
        // -----
        run {
            val getter = adapt.getter {
                filter { it.id() % 2L == 0L }
            }

            assertEquals(
                listOf(
                    MockItem1(0L),
                    UnrelatedItem(2L),
                    MockItem2(4L),
                    MockItem1(6L),
                    UnrelatedItem(8L)
                ),
                getter.items()
            )
        }
    }

    @Test
    fun filterIsInstance() {
        val adapt = mockAdapt(
            (0 until 9L)
                .map {
                    when (val rem = it % 3L) {
                        0L -> MockItem1(it)
                        1L -> MockItem2(it)
                        2L -> UnrelatedItem(it)
                        else -> error("Unexpected remainder:$rem id:$it")
                    }
                }
        )

        // mock-item-1
        run {
            val getter = adapt.getter {
                filterIsInstance<MockItem1>()
            }

            assertEquals(
                listOf(MockItem1(0L), MockItem1(3L), MockItem1(6L)),
                getter.items()
            )
        }

        // mock-item-2
        run {
            val getter = adapt.getter {
                filterIsInstance<MockItem2>()
            }

            assertEquals(
                listOf(MockItem2(1L), MockItem2(4L), MockItem2(7L)),
                getter.items()
            )
        }

        // unrelated
        run {
            val getter = adapt.getter {
                filterIsInstance<UnrelatedItem>()
            }

            assertEquals(
                listOf(UnrelatedItem(2L), UnrelatedItem(5L), UnrelatedItem(8L)),
                getter.items()
            )
        }

        // base-mock
        run {
            val getter = adapt.getter {
                filterIsInstance<BaseMockItem>()
            }

            assertEquals(
                listOf(
                    MockItem1(0L),
                    MockItem2(1L),
                    MockItem1(3L),
                    MockItem2(4L),
                    MockItem1(6L),
                    MockItem2(7L),
                ),
                getter.items()
            )
        }
    }

    @Test
    fun cast() {
        val adapt = mockAdapt(
            (0 until 9L)
                .map {
                    when (val rem = it % 3L) {
                        0L -> MockItem1(it)
                        1L -> MockItem2(it)
                        2L -> UnrelatedItem(it)
                        else -> error("Unexpected remainder:$rem id:$it")
                    }
                }
        )

        val getter = adapt.getter { this.cast<BaseMockItem>() }

        // cast succeeds, but fails when iterating
        val items = getter.items()

        try {
            for (item in items) {
                item.baseMockItemSpecificProperty
            }
            assertTrue(false)
        } catch (t: Throwable) {
            assertTrue(true)
        }
    }

    private sealed class BaseMockItem(id: Long) : Item<BaseMockItem.Holder>(id) {
        val baseMockItemSpecificProperty: Boolean = true

        private class Holder(view: View) : Item.Holder(view)

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            error("Stub!!")
        }

        override fun bind(holder: Holder) = Unit
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is BaseMockItem) return false

            if (id() != other.id()) return false

            return true
        }

        override fun hashCode(): Int {
            return id().hashCode()
        }
    }

    private class MockItem1(id: Long) : BaseMockItem(id)
    private class MockItem2(id: Long) : BaseMockItem(id)

    private class UnrelatedItem(id: Long) : Item<Item.Holder>(id) {
        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            error("Stub!!")
        }

        override fun bind(holder: Holder) = Unit

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UnrelatedItem) return false

            if (id() != other.id()) return false

            return true
        }

        override fun hashCode(): Int {
            return id().hashCode()
        }
    }

    private fun mockAdapt(items: List<Item<*>>): Adapt = object : Adapt {

        val items = items.toMutableList()

        override fun items() = items

        override fun setItems(items: MutableList<Item<*>>?) {
            this.items.clear()
            this.items.addAll(items ?: emptyList())
        }

        override fun notifyAllItemsChanged() = Unit
        override fun notifyItemChanged(item: Item<*>) = Unit
    }
}