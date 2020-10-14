package io.noties.adapt.sample

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.listview.AdaptListView

class SampleViewList(
    private val samples: List<Sample>,
    private val onSampleClicked: (Sample) -> Unit
) : SampleView {

    override fun view(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.view_list, parent, false).apply {
                val listView: ListView = findViewById(R.id.list_view)
                val adapt = AdaptListView.init(listView)
                adapt.setItems(samples.map(::SampleItem))
                listView.setOnItemClickListener { _, _, position, _ ->
                    onSampleClicked(samples[position])
                }
            }
    }

    class SampleItem(private val sample: Sample) :
        Item<SampleItem.Holder>(sample.name.hashCode().toLong()) {

        class Holder(view: View) : Item.Holder(view) {
            val name: TextView = requireView(R.id.name)
            val description: TextView = requireView(R.id.description)
        }

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return Holder(inflater.inflate(R.layout.item_sample, parent, false))
        }

        override fun render(holder: Holder) {
            holder.name.text = sample.name
            holder.description.text = sample.description

            holder.description.visibility =
                if (TextUtils.isEmpty(sample.description)) View.GONE else View.VISIBLE
        }
    }
}