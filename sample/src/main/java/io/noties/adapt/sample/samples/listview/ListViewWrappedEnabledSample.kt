package io.noties.adapt.sample.samples.listview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ListView
import io.noties.adapt.Item
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.PlainItem
import io.noties.adapt.sample.items.wrapper.ColorBackgroundWrapper
import io.noties.adapt.sample.items.wrapper.backgroundColor
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView

@AdaptSample(
    id = "20210326214950",
    title = "Wrapped in ListView",
    description = "Explicitly register <tt>ItemWrapper</tt> in a <tt>ListView</tt> with a <tt>Key</tt>",
    tags = ["listview", "wrapper", "key"]
)
class ListViewWrappedEnabledSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_list_view

    override fun render(view: View) {

        var counter = -1

        val listView: ListView = view.findViewById(R.id.list_view)
        val adapt = AdaptListView.init(listView) {

            val key = Item.Key.builder(PlainItem::class.java)
                .wrapped(ColorBackgroundWrapper::class.java)
                .build()

            it.include(key, true)
        }

        initSampleItems(
            adapt,
            onAddingNewItems = { items ->
                items
                    .toMutableList()
                    .also {
                        PlainItem(
                            "$counter",
                            Color.BLACK,
                            "Wrapped $counter"
                        )
                            .backgroundColor(0x200000FF)
                            .also(it::add)

                        counter -= 1
                    }
            }
        )
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__ListViewWrappedEnabledSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = ListViewWrappedEnabledSample()
}