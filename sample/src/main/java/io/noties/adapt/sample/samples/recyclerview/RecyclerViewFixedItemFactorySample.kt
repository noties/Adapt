package io.noties.adapt.sample.samples.recyclerview

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.adapt.recyclerview.AdaptRecyclerView
import io.noties.adapt.recyclerview.DiffUtilDataSetChangedHandler
import io.noties.adapt.recyclerview.FixedItemFactory
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.Capsule

@AdaptSample(
    id = "20220610171905",
    title = "FixedItemFactory",
    description = "Predefined set fo items to use for better performance on significant collections of items",
    tags = ["recyclerview"]
)
class RecyclerViewFixedItemFactorySample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_recycler_view

    // Suppress because it is triggered mistakenly
    @SuppressLint("InvalidSetHasFixedSize")
    override fun render(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        val adapt = AdaptRecyclerView.init(recyclerView) {
            it.dataSetChangeHandler(DiffUtilDataSetChangedHandler.create(true))
            it.itemFactory(
                // if we use some other item, then it must be specified here,
                //  otherwise an exception would be generated
                FixedItemFactory.create(
                    listOf(TextItem("This is mock item, it is used to create views for other items"))
                )
            )
        }

        val items = (0 until 1000)
            .map { TextItem("Item #$it") }

        adapt.setItems(items)
    }

    private class TextItem(
        private val text: String
    ) : ElementItem<TextItem.Ref>(hash(text), ::Ref) {
        class Ref {
            lateinit var textView: TextView
        }

        override fun ViewFactory<ViewGroup.LayoutParams>.body(references: Ref) {
            Text()
                .textSize(36)
                .textColor(Color.BLACK)
                .textFont(fontStyle = Typeface.BOLD)
                .reference(references::textView)
                .padding(16)
                .background(Capsule {
                    fill(Color.YELLOW)
                    padding(8)
                })
        }

        override fun bind(holder: Holder<Ref>) {
            holder.ref.textView.text = text
        }
    }
}