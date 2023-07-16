package io.noties.adapt.sample.samples.listview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ListView
import io.noties.adapt.Adapt
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.CardBigItem
import io.noties.adapt.sample.items.CardItem
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.debug.Debug

@AdaptSample(
    id = "20210122143242",
    title = "ListView some enabled",
    description = "Some Item are enabled (are considered to be separators)",
    tags = ["listview"]
)
class ListViewSomeEnabledSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {
        val listView: ListView = view.findViewById(R.id.list_view)

        val adapt: Adapt = AdaptListView.init(listView) {
            it.areAllItemsEnabled(false)
            // all items of this type will be enabled
            it.include(CardItem::class.java, true)
            // and these are disabled (default, can be omitted unless explicit
            //  registration is required, for ex. in an AleryDialog)
            it.include(CardBigItem::class.java, false)
        }

        listView.setOnItemClickListener { _, _, position, id ->
            Debug.i("item: %s, id: %d", adapt.items()[position], id)
        }

        initSampleItems(adapt)
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__ListViewSomeEnabledSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = ListViewSomeEnabledSample()
}