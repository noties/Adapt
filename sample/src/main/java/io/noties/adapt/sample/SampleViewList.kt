package io.noties.adapt.sample

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
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
) {

    fun view(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.view_list, parent, false).apply {
                val listView: ListView = findViewById(R.id.list_view)
                val adapt = AdaptListView.init(listView) {
                    it.areAllItemsEnabled(true)
                }
                adapt.setItems(samples.map(::SampleItem))
                listView.setOnItemClickListener { _, _, position, _ ->
                    onSampleClicked(samples[position])
                }
            }
    }

    class SampleItem(private val sample: Sample) :
        Item<SampleItem.Holder>(hash(sample.name)) {

        class Holder(view: View) : Item.Holder(view) {
            val name: TextView = requireView(R.id.name)
            val description: TextView = requireView(R.id.description)
            val tags: TextView = requireView(R.id.tags)
        }

        override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
            return Holder(inflater.inflate(R.layout.item_sample, parent, false))
        }

        override fun bind(holder: Holder) {
            holder.name.text = sample.name
            holder.description.textOrGone(sample.description)
            holder.tags.textOrGone(tags)
        }

        private val tags: CharSequence
            get() {
                return sample.tags.joinTo(SpannableStringBuilder(), separator = " ") { tag ->
                    SpannableString(" $tag ").apply {
                        val color = tag.hashCode()
                        setSpan(
                            BackgroundColorSpan(color),
                            0,
                            length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }

        private fun TextView.textOrGone(text: CharSequence?) {
            this.text = text
            this.visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
        }
    }
}