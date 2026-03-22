package io.noties.adapt.sample.explore

import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.Item.Holder
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.items.CollectionItem

object ExploreTyped {
    interface TypedAdapt<A: Adapt, T: Item<out Holder>> {
        fun adapt(): A

        fun items(): List<T>
        fun setItems(items: List<T>)
        fun notifyAllItemsChanged()
        fun notifyItemChanged(item: T)
    }

    fun hey(adapt: AdaptRecyclerView) {
        val t = adapt.typed<_, CollectionItem>()
    }

    inline fun <A: Adapt, reified T: Item<out Holder>> A.typed(): TypedAdapt<A, T> {
        return TypedAdaptImpl(this)
    }

    class TypedAdaptImpl<A: Adapt, T: Item<out Holder>>(
        val adapt: A
    ): TypedAdapt<A, T> {
        override fun adapt(): A {
            return adapt
        }

        override fun items(): List<T> {
            @Suppress("UNCHECKED_CAST")
            return adapt.items() as List<T>
        }

        override fun notifyAllItemsChanged() {
            adapt.notifyAllItemsChanged()
        }

        override fun notifyItemChanged(item: T) {
            adapt.notifyItemChanged(item)
        }

        override fun setItems(items: List<T>) {
            adapt.setItems(items)
        }
    }
}