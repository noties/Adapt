package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.debug.Debug

@AdaptSample(
    id = "20230402020404",
    title = "UI, RelativeLayout concept"
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

                override fun onViewDetachedFromWindow(v: View?) = Unit
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
                .background(Colors.orange)
                .layoutWrap()
                .layoutAlignParent(leading = true, top = true)

            Text("below")
                .padding(16)
                .background(Colors.accent)
                .layoutPosition(below = el)
                .layoutCenter(horizontal = true)

            Text("alignTrailing")
                .padding(16)
                .background(Colors.primary)
                .layoutPosition(toTrailing = el)

        }.layoutFill()
    }
}

@Suppress("ClassName")
private class Preview_AdaptUIRelativeSample(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIRelativeSample()
}

