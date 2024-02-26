package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.ExploreRelative.Relative
import io.noties.adapt.sample.explore.ExploreRelative.layoutAlignParent
import io.noties.adapt.sample.explore.ExploreRelative.layoutCenter
import io.noties.adapt.sample.explore.ExploreRelative.layoutPosition
import io.noties.adapt.sample.ui.color.accent
import io.noties.adapt.sample.ui.color.black
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.backgroundColor
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.debug.Debug

@AdaptSample(
    id = "20230402020404",
    title = "[Explore] UI, RelativeLayout concept"
)
class AdaptUIRelativeSample : AdaptUISampleView() {

    override fun render(view: View) {
        super.render(view)

        Debug.e()

        fun View.onAttached(block: (View) -> Unit) {
            addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    block(v)
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            })
        }

        val container = FrameLayout(view.context).also {
            it.layoutParams = LayoutParams(100, 100)
            it.setBackgroundColor(Colors.black)
        }
        container.onAttached { Debug.e("container:$it") }

        val child = TextView(view.context).also {
            it.text = "WHATTHE!"
            it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        child.onAttached { Debug.e("child:$it") }

        container.addView(child)
        view.postDelayed({
            (view as ViewGroup).addView(container)
        }, 100L)
    }

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
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIRelativeSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIRelativeSample()
}

