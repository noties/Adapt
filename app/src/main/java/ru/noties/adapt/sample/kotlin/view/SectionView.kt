package ru.noties.adapt.sample.kotlin.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.noties.adapt.Holder
import ru.noties.adapt.ItemView
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.core.item.SectionItem

class SectionView : ItemView<SectionItem, SectionHolder>() {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): SectionHolder =
            SectionHolder(inflater.inflate(R.layout.view_section, parent, false))

    override fun bindHolder(holder: SectionHolder, item: SectionItem) {
        holder.text.text = item.name()
    }
}

class SectionHolder(view: View) : Holder(view) {
    val text: TextView = requireView(R.id.text)
}