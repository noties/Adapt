package io.noties.adapt.sample.samples.listview

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import io.noties.adapt.Item
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.CardBigItem
import io.noties.adapt.sample.items.CardItem
import io.noties.adapt.sample.items.ControlItem
import io.noties.adapt.sample.items.PlainItem
import io.noties.adapt.sample.items.wrapper.ColorBackgroundWrapper
import io.noties.adapt.sample.items.wrapper.backgroundColor
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.debug.Debug
import java.util.Date

@AdaptSample(
    id = "20210326220725",
    title = "AlertDialog with Wrapped",
    description = "Explicitly register an <tt>Item</tt> wrapped with <tt>ItemWrapper</tt> " +
            "when displayed in a <tt>AlertDialog</tt>",
    tags = ["alertdialog", "listview", "wrapper", "key"]
)
class AlertDialogHasWrappedSample : SampleView() {

    // not used in this sample, but required to function
    override val layoutResId: Int
        get() = R.layout.view_sample_list_view

    override fun render(view: View) {

        val adapt = AdaptListView.create(view.context) {
            it.include(CardBigItem::class.java)
            it.include(CardItem::class.java)
            it.include(ControlItem::class.java)
            it.include(PlainItem::class.java)

            // explicit item with a Key
            val key = Item.Key.builder(PlainItem::class.java)
                .wrapped(ColorBackgroundWrapper::class.java)
                .build()
            it.include(key)
        }

        initSampleItems(
            adapt,
            onAddingNewItems = { items ->
                items
                    .toMutableList()
                    .also {
                        PlainItem("💪", Color.MAGENTA, "WRAPPED at ${Date()}")
                            .backgroundColor(0x20FF00F)
                            .also(it::add)
                    }
            }
        )

        AlertDialog.Builder(view.context)
            .setAdapter(adapt.adapter()) { _, which ->
                Debug.i(which)
            }
            .show()
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AlertDialogHasWrappedSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AlertDialogHasWrappedSample()
}