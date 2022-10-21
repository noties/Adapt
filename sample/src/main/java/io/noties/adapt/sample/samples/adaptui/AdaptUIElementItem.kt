package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.preview.AdaptPreviewLayout
import io.noties.adapt.sample.App
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.Gravity

class AdaptUIElementItem(val text: String) :
    ElementItem<AdaptUIElementItem.Ref>(hash(text), ::Ref) {
    class Ref {
        lateinit var textView: TextView
    }

    override fun ViewFactory<ViewGroup.LayoutParams>.body(references: Ref) {
        VStack {
            Text()
                // already SP
                .textSize(16)
                .textColor(Colors.black)
                .textGravity(Gravity.center)
                // values are already DP
                .padding(vertical = 24, horizontal = 16)
                .reference(references::textView)
        }.background(RoundedRectangle(8) {
            stroke(Colors.black, 2, 8, 2)
            padding(8)
        })
    }

    override fun bind(holder: Holder<Ref>) {
        with(holder.ref) {
            textView.text = text
        }
    }
}

// unfortunately `private class` is not really private and each
//  package must have unique class names
@Suppress("ClassName")
class __AdaptUISimpleItem(context: Context, attrs: AttributeSet) :
    AdaptPreviewLayout(context, attrs) {

    override fun initialize(layout: AdaptPreviewLayout) {
        App.mock(context)
    }

    override fun items(): List<Item<*>> = listOf(
        "This is one",
        "Two, it is"
    ).map(::AdaptUIElementItem)
}