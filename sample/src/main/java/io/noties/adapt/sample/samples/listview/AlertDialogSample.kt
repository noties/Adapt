package io.noties.adapt.sample.samples.listview

import android.app.AlertDialog
import android.view.View
import android.widget.ListView
import io.noties.adapt.Adapt
import io.noties.adapt.Item
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.CardBigItem
import io.noties.adapt.sample.items.CardItem
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.items.PlainItem
import io.noties.debug.Debug

@AdaptSample(
    id = "20210122143224",
    title = "AlertDialog",
    description = "Use Adapt with <tt>AlertDialog</tt>",
    tags = ["alertdialog", "listview"]
)
class AlertDialogSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {

        val listView: ListView = view.findViewById(R.id.list_view)
        val adapt1 = AdaptListView.init(listView)
        val adapt2 = AdaptListView.create(view.context) {
            // all items must be explicitly registered
            // if there is only one item, then it is not required
            it.include(CardBigItem::class.java)
            it.include(CardItem::class.java)
            it.include(ControlItem::class.java)
            it.include(PlainItem::class.java)
        }

        val adapt = object : Adapt {
            override fun items(): List<Item<out Item.Holder>> = adapt1.items()
            override fun setItems(items: List<Item<out Item.Holder>>?) {
                adapt1.setItems(items)
                adapt2.setItems(items)
            }

            override fun notifyAllItemsChanged() {
                adapt1.notifyAllItemsChanged()
                adapt2.notifyAllItemsChanged()
            }

            override fun notifyItemChanged(item: Item<out Item.Holder>) {
                adapt1.notifyItemChanged(item)
                adapt2.notifyItemChanged(item)
            }
        }

        initSampleItems(adapt)

        AlertDialog.Builder(view.context)
            .setAdapter(adapt2.adapter()) { _, position ->
                Debug.i(position)
            }
            .show()
    }
}