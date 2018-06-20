package ru.noties.adapt.sample.kotlin.view

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.noties.adapt.Holder
import ru.noties.adapt.ItemView
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.core.item.AppendItem

class AppendView : ItemView<AppendItem, Holder>() {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder =
            Holder(inflater.inflate(R.layout.view_append, parent, false))

    override fun bindHolder(holder: Holder, item: AppendItem) {
        // no op
    }
}