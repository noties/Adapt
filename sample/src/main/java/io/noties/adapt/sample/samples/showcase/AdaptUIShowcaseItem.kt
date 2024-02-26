package io.noties.adapt.sample.samples.showcase

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Adapt
import io.noties.adapt.listview.AdaptListView
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptRecyclerView
import io.noties.adapt.ui.adaptView
import io.noties.adapt.ui.adaptViewGroup
import io.noties.adapt.ui.element.Recycler
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScrollStack
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.recyclerLinearLayoutManager
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.setItems
import io.noties.debug.Debug

@AdaptSample(
    id = "20230602001541",
    title = "[Showcase] AdaptUI, Item",
    description = "Different usage of <em>Item</em> (including its siblings) " +
            "in different contexts: directly in UI layout, in a " +
            "<em>ViewGroup</em>, <em>RecyclerView</em> or <em>ListView</em> " +
            "(via <em>AlertDialog</em>)",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseItem : AdaptUISampleView() {

    private lateinit var adaptViewGroup: Adapt
    private lateinit var adaptRecyclerView: Adapt

    override fun ViewFactory<LayoutParams>.body() {
        VScrollStack {

            // add item directly in android-ui:
            View().adaptView(MyItem("Added directly to UI layout #1"))
            View().adaptView(MyItem("Added directly to UI layout #2"))

            VStack { }
                .adaptViewGroup()
                .reference(::adaptViewGroup)
                // setItems is a utility function, at this point adapt can be referenced
                //  and new items can be added dynamically
                .setItems(
                    listOf(
                        MyItem("Inside LinearLayout #1"),
                        MyItem("Inside LinearLayout #2"),
                    )
                )

            Recycler()
                .recyclerLinearLayoutManager()
                .adaptRecyclerView()
                .reference(::adaptRecyclerView)
                .setItems(
                    listOf(
                        MyItem("Inside RecyclerView #1"),
                        MyItem("Inside RecyclerView #2"),
                    )
                )

            Text("Click ME!")
                .onClick { showAlertDialog() }

        }.layoutFill()
    }

    private fun showAlertDialog() {
        val items = listOf(
            MyItem("Inside AlertDialog #1"),
            MyItem("Inside AlertDialog #2"),
        )
        val adapt = AdaptListView.create(context) {
            it.includeItems(items)
            // in order to make clickable by alert dialog
            it.areAllItemsEnabled(true)
        }
        AlertDialog.Builder(context)
            .setAdapter(adapt.adapter()) { dialog, which ->
                Debug.e("which:$which")
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    // this Item is going to be used in all possible contexts:
    //  - regular view
    //  - view group
    //  - alert (as a listview)
    class MyItem(val text: String) : ElementItem<MyItem.Ref>(hash(text), { Ref() }) {
        class Ref {
            lateinit var textView2: TextView
        }

        override fun bind(holder: Holder<Ref>) {
            holder.ref.textView2.text = text
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(ref: Ref) {
            Text()
                .reference(ref::textView2)
                .textSize(17)
                .textColor { black }
                .padding(16, 12)
                .onView {
                    Debug.e(it)
                    Debug.e(ref.textView2)
                }
        }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIShowcaseItem(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIShowcaseItem()
}