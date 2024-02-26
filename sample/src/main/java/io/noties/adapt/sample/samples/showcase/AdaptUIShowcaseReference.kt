package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScrollStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout

@AdaptSample(
    id = "20230520013122",
    title = "[Showcase] AdaptUI reference elements",
    description = "Access to created elements as regular objects",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseReference : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        // Utility for VScroll { VStack { /**/ } }
        //  another one: HScrollStack
        VScrollStack {

            // an element can be referenced
            // ViewElement<TextView, LinearLayout.LayoutParams>
            val element = Text("Referenced element")

            // textSize is in SP (scaled density pixels)
            element.textSize(22)

            // `ifElement` will hold a reference to created elements
            val ifElement = if (System.currentTimeMillis() % 2 == 0L) {
                Text("Even system time")
            } else {
                View()
                    .layout(FILL, 48)
            }

            // NB! element is of View type, not TextView,
            //  so `.text*` functions are not available
            ifElement.backgroundColor(Color.GRAY)

            // reference a collection of elements (already added to parent)
            val elements = (0 until 100)
                .map { Text("$it") }

            // configure already added views
            for (el in elements) {
                el
                    .textSize(21)
                    .textColor(Color.RED)
            }
        }
    }
}

private class PreviewAdaptUIShowcaseReference(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseReference()
}