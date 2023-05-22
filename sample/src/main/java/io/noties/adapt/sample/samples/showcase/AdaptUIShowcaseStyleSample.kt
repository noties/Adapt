package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.ElementStyle
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.style
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.padding

@AdaptSample(
    id = "20230522113544",
    title = "[Showcase] AdaptUI, element style",
    description = "Combine common styles to be reused",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseStyleSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text("This is just a text")
                .textSize(100)
                // `backgrounded` style is generic, so any element can use it
                .style(backgrounded)
                // TextStyles expect a sibling of a TextView
                // overrides `textSize` specified before
                .style(TextStyles.body)
                // overrides value from the style
                .textSize(20)

            Text("Hello again with `textStyle` extension")
                // also, when container is known, an extension could be added to allow
                //  quicker access, for example:
                .textStyle { largeTitle }

            // Layout-specific styling:
            ZStack {
//                Text("Not available, requires to be in LinearLayout")
//                    .style(weighted)
            }

            HStack {
                Text("Available here")
                    .style(weighted)
                    .style(backgrounded)
                Text("The second one with weight:1")
                    .style(weighted)
                    .style(backgrounded)
            }

        }.layoutFill()
    }

    private val backgrounded = ElementStyle.generic {
        it.background(Color.GRAY)
            .padding(16)
    }

    // only available in LinearLayout.LayoutParams context
    private val weighted = ElementStyle.layout<LinearLayout.LayoutParams> {
        it.layout(0, WRAP, 1F)
            .padding(16)
    }

    // also, when container is known an extension could be added to allow
    //  quicker access, for example:
    @Suppress("MemberVisibilityCanBePrivate")
    inline fun <V : TextView, LP : LayoutParams> ViewElement<V, LP>.textStyle(
        style: TextStyles.() -> ElementStyle<V, LP>
    ) = this.style(style.invoke(TextStyles))
}

private class PreviewAdaptUIShowcaseStyleSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseStyleSample()
}