package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R

class ControlItem(
    private val onAdd: () -> Unit,
    private val onShuffle: () -> Unit
) : Item<ControlItem.Holder>(hash(ControlItem::class)) {

    class Holder(view: View) : Item.Holder(view) {
        val add: View = requireView(R.id.add)
        val shuffle: View = requireView(R.id.shuffle)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_control, parent, false))
    }

    override fun bind(holder: Holder) {
        holder.add.setOnClickListener {
            onAdd()
        }

        holder.shuffle.setOnClickListener {
            onShuffle()
        }
    }

    companion object {

        fun init(adapt: Adapt): ControlItem {

            fun onAdd() = adapt.setItems(addedItems(adapt.items()))
            fun onShuffle() = adapt.setItems(shuffledItems(adapt.items()))

            return ControlItem(::onAdd, ::onShuffle)
        }

        fun init(onAdd: () -> Unit, onShuffle: () -> Unit): ControlItem =
            ControlItem(onAdd, onShuffle)

        fun addedItems(items: List<Item<*>>): List<Item<*>> = items.toMutableList().apply {
            addAll(ItemGenerator.next(size))
        }

        fun shuffledItems(items: List<Item<*>>): List<Item<*>> = items.shuffled()

    }
}