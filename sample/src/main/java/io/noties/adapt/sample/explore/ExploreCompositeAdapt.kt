package io.noties.adapt.sample.explore

import io.noties.adapt.Adapt
import io.noties.adapt.Item

@Suppress("StopShip")
class ExploreCompositeAdapt {

    fun Adapt.decorate(
        other: Adapt,
        decorateItem: (Item<*>)
    ): Adapt {
        val adapt = this
        return object: Adapt {
            override fun items(): List<Item<*>> {
                return adapt.items()
            }

            override fun setItems(items: List<Item<*>>?) {
//                val decorated = processItems(items ?: emptyList())
//                adapt.setItems(items)
//                other.setItems(decorated)
            }

            override fun notifyAllItemsChanged() {
                TODO("Not yet implemented")
            }

            override fun notifyItemChanged(item: Item<*>) {
                TODO("Not yet implemented")
            }
        }
    }
}