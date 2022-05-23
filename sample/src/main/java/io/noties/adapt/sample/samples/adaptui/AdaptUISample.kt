package io.noties.adapt.sample.samples.adaptui

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.AnyViewFactory
import io.noties.adapt.ui.FILL
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.item.ElementItem
import io.noties.adapt.ui.item.ElementItemNoRef
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.viewgroup.AdaptViewGroup
import java.util.Date

@AdaptSample(
    id = "20220523200713",
    "Adapt-UI sample",
    "usage of <tt>adapt-ui</tt> module to build simple layouts in Kotlin",
    tags = ["adapt-ui"]
)
class AdaptUISample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_view_group

    // as always adapt-ui items are also available in all contexts:
    //  - recycler-view
    //  - view-group
    //  - list-view
    //  - view
    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.view_group)
        val adapt = AdaptViewGroup.init(viewGroup)

        val items = listOf<Item<*>>(
            StaticTextItem(),
            TextItem("Created: ${Date()}"),
            LineItem("title1", "value1"),
            LineItem("title2", "value2"),
        )

        adapt.setItems(items)
    }

    private class StaticTextItem : ElementItemNoRef(0L) {
        // as `text` is static we use it directly here
        override fun ViewFactory<LayoutParams>.body() {
            Text("This is static text that never changes")
                // 24 is SP, not pixels
                .textSize(24)
                .textColor(Color.BLACK)
                .textGravity(Gravity.CENTER)
                // 16 is DP, not pixels
                .padding(16)
                .background(Color.YELLOW)
        }
    }

    private class TextItem(
        val text: String
    ) : ElementItem<TextItem.References>(hash(text), ::References) {

        class References {
            lateinit var textView: TextView
        }

        override fun ViewFactory<LayoutParams>.body(references: References) {
            VStack {
                View()
                    // 48 is already dp
                    .layout(FILL, 48)
                    .background(Color.MAGENTA)
                Text()
                    .textSize(17)
                    .textFont(null, Typeface.BOLD)
                    .reference(references::textView)
                    .padding(8)
            }
        }

        override fun bind(holder: Holder<References>) {
            with(holder.references) {
                textView.text = text
            }
        }
    }

    private class LineItem(val title: String, val value: String) :
        ElementItem<LineItem.References>(hash(title, value), ::References) {
        class References {
            lateinit var titleView: TextView
            lateinit var valueView: TextView
        }

        override fun ViewFactory<LayoutParams>.body(references: References) {
            VStack {
                test()
                test2()
                paragraph()
                    // TODO: layout is not FILL, but WRAP somehow...
                    .layout(FILL, 256)
                    .background(Color.RED)
                HStack {
                    Text()
                        .reference(references::titleView)
                    Spacer()
                    Text()
                        .reference(references::valueView)
                }
                paragraph()
            }
        }

        // this would reduce layoutParams to ViewGroups, so only basic layout(width, height)
        //  would be configurable
        private fun AnyViewFactory.test() =
            Text("TEST")
                .textSize(12)
                .textColor(Color.WHITE)
                .padding(16)
                .background(Color.RED)

        // this does not return anything, it is valid, but no further customization
        //  would be available
        private fun AnyViewFactory.test2() {
            Text("WHATEVER")
                .textSize(12)
                .textColor(Color.RED)
                .textGravity(Gravity.END)
                .padding(12)
                .background(Color.BLACK)
        }

        // this would allow configuring layout params of ViewGroup,
        //  for example, layoutWeight if used inside LinearLayout
        private fun <LP : LayoutParams> ViewFactory<LP>.paragraph() =
            Text("***")
                .textGravity(Gravity.CENTER)
                .textSize(48)
                .padding(24)
                .textColor(Color.GRAY)
                .background(Color.CYAN)

        override fun bind(holder: Holder<References>) {
            with(holder.references) {
                titleView.text = title
                valueView.text = value
            }
        }
    }
}