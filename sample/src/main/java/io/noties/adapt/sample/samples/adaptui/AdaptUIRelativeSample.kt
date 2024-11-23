package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import io.noties.adapt.preview.Preview
import io.noties.adapt.sample.PreviewSampleView
import io.noties.adapt.sample.SampleViewUI
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.ExploreRelative.Relative
import io.noties.adapt.sample.explore.ExploreRelative.layoutAlignParent
import io.noties.adapt.sample.explore.ExploreRelative.layoutCenter
import io.noties.adapt.sample.explore.ExploreRelative.layoutPosition
import io.noties.adapt.sample.samples.Tags
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.util.onAttachedOnce
import io.noties.debug.Debug

@AdaptSample(
    id = "20230402020404",
    title = "[Explore] UI, RelativeLayout concept",
    tags = [Tags.explore]
)
class AdaptUIRelativeSample : SampleViewUI() {

    override fun ViewFactory<LayoutParams>.body() {
        Relative {

            val el = Text("alignParent{leading,top}")
                .padding(16)
                .backgroundColor { orange }
                .layoutWrap()
                .layoutAlignParent(leading = true, top = true)

            Text("below")
                .padding(16)
                .backgroundColor { accent }
                .layoutPosition(below = el)
                .layoutCenter(horizontal = true)

            Text("alignTrailing")
                .padding(16)
                .backgroundColor { primary }
                .layoutPosition(toTrailing = el)

        }.layoutFill()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        Debug.e()

        val container = FrameLayout(view.context).also {
            it.layoutParams = LayoutParams(100, 100)
            it.setBackgroundColor(Colors.black)
        }
        container.onAttachedOnce { Debug.e("container:$it") }

        val child = TextView(view.context).also {
            it.text = "WHATTHE!"
            it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        child.onAttachedOnce { Debug.e("child:$it") }

        container.addView(child)
        view.postDelayed({
            (view as ViewGroup).addView(container)
        }, 100L)
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIRelativeSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView
        get() = AdaptUIRelativeSample()
}

