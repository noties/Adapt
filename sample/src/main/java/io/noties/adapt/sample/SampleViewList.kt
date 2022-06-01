package io.noties.adapt.sample

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.CachingHolder
import io.noties.adapt.Item
import io.noties.adapt.ItemLayout
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.sample.ui.DividerOverlay
import io.noties.adapt.sample.ui.SearchBar
import io.noties.adapt.sample.util.normalized

class SampleViewList(
    private val samples: List<Sample>,
    private val onSampleClicked: (Sample) -> Unit
) {

    fun view(parent: ViewGroup): View {
        val context = parent.context
        return LayoutInflater.from(context)
            .inflate(R.layout.view_list, parent, false).apply {

                val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
                recyclerView.also {
                    it.layoutManager = LinearLayoutManager(context)
                }

                val adapt = AdaptRecyclerView.init(recyclerView)
                val initialItems = samples.map {
                    SampleItem(it, onSampleClicked)
                }

                adapt.setItems(initialItems)

                val searchBar: SearchBar = findViewById(R.id.search_bar)
                searchBar.onTextChangedListener = { search ->
                    val items = if (search == null) {
                        initialItems
                    } else {
                        val text = search.normalized()
                        initialItems
                            .filter { filter(text, it) }
                            .let {
                                if (it.isEmpty()) {
                                    listOf(EmptyItem())
                                } else {
                                    it
                                }
                            }
                    }
                    adapt.setItems(items)
                }
            }
    }

    private fun filter(text: String, item: SampleItem): Boolean {
        val sample = item.sample
        return sample.name.normalized().contains(text)
                || true == sample.description?.toString()?.normalized()?.contains(text)
                || sample.tags.count { it.normalized().contains(text) } > 0
    }

    class SampleItem(
        val sample: Sample,
        private val onSampleClicked: (Sample) -> Unit
    ) :
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

            DividerOverlay.init(holder.itemView())

            holder.itemView().setOnClickListener {
                onSampleClicked(sample)
            }
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

    class EmptyItem : ItemLayout(99L, R.layout.widget_empty) {
        override fun bind(holder: CachingHolder) = Unit
    }
}