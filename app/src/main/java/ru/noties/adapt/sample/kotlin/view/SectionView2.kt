package ru.noties.adapt.sample.kotlin.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ru.noties.adapt.DynamicHolder
import ru.noties.adapt.ItemView
import ru.noties.adapt.sample.R
import ru.noties.adapt.sample.core.item.SectionItem

class SectionView2 : ItemView<SectionItem, DynamicHolder>() {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup) =
            DynamicHolder(inflater.inflate(R.layout.view_section, parent, false))

    override fun bindHolder(holder: DynamicHolder, item: SectionItem) {
        holder
                .on<TextView>(R.id.text, { it.text = item.name() })
                .on<TextView>(R.id.text, { /* hey, another one chained */ })
    }
}