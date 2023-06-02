package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.reference
import io.noties.debug.Debug

@AdaptSample(
    id = "20230602002749",
    title = "[Showcase] AdaptUI, Reference views and elements",
    description = "Assign views and elements with `.reference` utility extension",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseReference2 : AdaptUISampleView() {

    private lateinit var textView: TextView
    private lateinit var textElement: ViewElement<TextView, *>

    private class Ref {
        lateinit var imageView: ImageView
        lateinit var imageElement: ViewElement<ImageView, *>
    }

    private val ref = Ref()

    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text("This is some text")
                // would assign view to provided property when view would be available
                //  `::textView` references parent class (`this::textView` cannot be used here
                //  as `this` refers to ViewFactory)
                .reference(::textView)
                // the same as:
//                .onView { textView = it }
                // would assign reference to this element
                .reference(::textElement)
                // the same as
//                .also { textElement = it }

            Image()
                // somehow IDE is not suggesting private properties of a class, when start with `::`
                //  this is why it is more convenient to use a special holder class
                .reference(ref::imageView)
                .reference(ref::imageElement)

        }.layoutFill()
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIShowcaseReference2(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIShowcaseReference2()
}