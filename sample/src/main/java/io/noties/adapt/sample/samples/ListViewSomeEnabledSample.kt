package io.noties.adapt.sample.samples

import android.view.View
import android.widget.ListView
import io.noties.adapt.Adapt
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.Sample
import io.noties.adapt.sample.items.CardBigItem
import io.noties.adapt.sample.items.CardItem
import io.noties.debug.Debug

class ListViewSomeEnabledSample : AbsSampleView() {

    override val sample: Sample = Sample(
        "ListView some enabled",
        text("Some Item are enabled (are considered to be separators)"),
        ::ListViewSomeEnabledSample
    )

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {
        val listView: ListView = view.findViewById(R.id.list_view)

        val adapt: Adapt = AdaptListView.init(listView) {
            it.areAllItemsEnabled(false)
            // all items of this type will be enabled
            it.include(CardItem::class.java, true)
            it.include(CardBigItem::class.java) { item ->
                // enabled only if id is even (else disable)
                item.id() % 2L == 0L
            }
        }

        listView.setOnItemClickListener { _, _, position, id ->
            Debug.i("item: %s, id: %d", adapt.items()[position], id)
        }

        adapt.setItems(initialItems(adapt))
    }
}