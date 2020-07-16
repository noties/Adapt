package io.noties.adapt.sample.forms.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.Item

class InputItem(id: Long): Item<InputItem.Holder>(id) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(0, parent, false))
    }

    override fun render(holder: Holder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class Holder(view: View): Item.Holder(view) {

    }
}