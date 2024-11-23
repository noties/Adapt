package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.clipToOutline
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Image
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStackContentMeasuredLast
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.ZStackWrapHeightOrScroll
import io.noties.adapt.ui.element.imageScaleType
import io.noties.adapt.ui.element.stackContentMeasureLast
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.indent
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.reference
import io.noties.adapt.ui.scrollBarsEnabled
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.withAlphaComponent
import kotlin.math.roundToInt

@AdaptSample(
    id = "20240327011222",
    title = "AdaptUI. Scroll if content scrolls else wrap height",
    tags = [Tags.adaptUi, Tags.recipe, Tags.interactive]
)
class AdaptUIScrollIfScrollsSample : SampleViewUI() {

    private lateinit var text: TextView

    override fun ViewFactory<LayoutParams>.body() {
        ZStack {

            VStackContentMeasuredLast {

                HStack {
                    Image(R.drawable.ic_arrow_back_ios_24_white)
                        .layout(56, 56)
                        .imageScaleType { centerInside }
                }.layout(fill, 56)
                    .backgroundColor { accent }

                ZStackWrapHeightOrScroll(
                    scrollStyle = {
                        it.scrollBarsEnabled(false)
                    }
                ) {
                    Text("Content, click me to generate new")
                        .layout(fill, wrap)
                        // this one is going to be erased after wrap/unwrap
                        .layoutGravity { center.vertical }
                        .textSize { 17 }
                        .padding(top = 24, bottom = 36)
                        .padding(horizontal = 16)
                        .backgroundColor { accent.withAlphaComponent(0.2F) }
                        .reference(::text)
                }.stackContentMeasureLast()

                Text("Click me!")
                    .layout(fill, wrap)
                    .textSize(17)
                    .textGravity { center.horizontal }
                    .padding(12)
                    .background {
                        RoundedRectangle(12) {
                            fill { accent }
                        }
                    }
                    .foregroundDefaultSelectable()
                    .clipToOutline()
                    .layoutMargin(16)
                    .onClick {
                        // from 0 to 100
                        val count = (20 * Math.random()).roundToInt()
                        val base =
                            "This is the text to replicate, let it take some place, some amount of space we need to cover."
                        val result = (0 until count)
                            .joinToString("\n\n") { "[${it + 1}/$count] $base" }
                        TransitionManager.beginDelayedTransition(text.parent.parent as ViewGroup)
                        text.text = result
                    }
            }.indent()
                .layoutGravity { center }
                .backgroundColor { accent.withAlphaComponent(0.3F) }
                .layoutMargin(24)

        }.layoutFill()
    }
}

@Preview
private class PreviewAdaptUIScrollIfScrollsSample(context: Context, attrs: AttributeSet?) :
    PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIScrollIfScrollsSample()
}