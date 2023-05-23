package io.noties.adapt.sample.samples.adaptui

import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.ItemGenerator
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.items.CardItem
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.adaptView
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RoundedRectangleShape
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230116140759",
    "AdaptUI, Item inside ViewFactory",
    description = "Usage of an Item in ViewFactory when building UI view (AdaptView)",
    tags = ["adapt-ui", "adapt-view"]
)
class AdaptUIItemSample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            VStack {

                Text("This is just some inline text")
                    .textSize(16)
                    .textColor(Colors.black)
                    .padding(16)

                View()
                    .adaptView(CardItem("Q", Colors.orange, "It is an ITEM"))

                Text("This is just some inline text")
                    .textSize(16)
                    .textColor(Colors.black)
                    .padding(16)

                // references AdaptElement<AdaptView>
                val adapt = View()
                    .adaptView {
                        it.item(CardItem("Q", Colors.orange, "It is an ITEM #2"))
                        it.changeHandlerTransitionParent()
                    }

                Text("Click me")
                    .textSize(17)
                    .textAllCaps()
                    .textColor(Colors.black)
                    .textGravity(Gravity.center)
                    .layoutMargin(16)
                    .padding(horizontal = 16, vertical = 12)
                    .background(RoundedRectangleShape(8).fill(Colors.orange))
                    .also { element ->
                        ItemGenerator.reset()
                        element.onClick {
                            adapt.adapt.setItem(ItemGenerator.next(0)[0])
                        }
                    }
            }
        }
    }
}