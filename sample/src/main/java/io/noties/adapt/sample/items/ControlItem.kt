package io.noties.adapt.sample.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R

class ControlItem(private val adapt: Adapt) : Item<ControlItem.Holder>(42L) {

    class Holder(view: View) : Item.Holder(view) {
        val add: View = requireView(R.id.add)
        val shuffle: View = requireView(R.id.shuffle)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_control, parent, false))
    }

    override fun render(holder: Holder) {
        holder.add.setOnClickListener {
            adapt.setItems(adapt.items().toMutableList().apply {
                addAll(ItemGenerator.next(size))
            })
        }

        holder.shuffle.setOnClickListener {
            adapt.setItems(adapt.items().shuffled())
        }
    }
}