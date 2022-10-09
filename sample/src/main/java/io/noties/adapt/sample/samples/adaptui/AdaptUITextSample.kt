package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewLayout
import io.noties.adapt.sample.util.hex
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.createView
import io.noties.adapt.ui.element.BreakStrategy
import io.noties.adapt.ui.element.HyphenationFrequency
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textBreakStrategy
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textEllipsize
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textHideIfEmpty
import io.noties.adapt.ui.element.textHint
import io.noties.adapt.ui.element.textHyphenationFrequency
import io.noties.adapt.ui.element.textMaxLines
import io.noties.adapt.ui.element.textOnTextChanged
import io.noties.adapt.ui.element.textSingleLine
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug

@AdaptSample(
    id = "20221008115412",
    title = "AdaptUI - Text & TextInput",
    tags = ["adapt-ui", "ui-text", "ui-text-input"]
)
class AdaptUITextSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        val child = ViewFactory.createView(view.context) {
            VScroll {
                VStack {

                    MyTextInput()

                    MyText()
                }
            }.layoutFill()
        }
        (view as ViewGroup).addView(child)
    }

    @Suppress("FunctionName")
    private fun ViewFactory<ViewGroup.MarginLayoutParams>.MyTextInput() {
        TextInput(EditorInfo.TYPE_CLASS_PHONE)
            .textSize(16)
            .textColor(Colors.black)
            .textHint("Some phone!")
            .padding(horizontal = 16, vertical = 12)
            .layoutMargin(horizontal = 16, vertical = 8)
            .background(StatefulShape.drawable {
                val base = RoundedRectangle(9)
                setFocused(base.copy {
                    stroke(Colors.orange)
                    fill(hex("#00000000"))
                    padding(1)
                })
                setDefault(base.copy {
                    fill(hex("#20000000"))
                    stroke(hex("#40000000"))
                    padding(1)
                })
            })
    }

    @Suppress("FunctionName")
    private fun ViewFactory<ViewGroup.MarginLayoutParams>.MyText() {
        Text("Can be sent via constructor")
            .text("Or as an argument")
            .text(R.string.app_name)
            .textColor(Colors.orange)
            // value is SP, so it would be automatically converted to proper value
            .textSize(42)
            .textFont(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            // if supplied text is null or empty, this TextView is going to be GONE
            .textHideIfEmpty()
            .textAllCaps()
            .textEllipsize(TextUtils.TruncateAt.END)
            .textGravity(Gravity.center)
            .textSingleLine(true)
            .textMaxLines(1)
            .ifAvailable(Build.VERSION_CODES.M) {
                it.textHyphenationFrequency(HyphenationFrequency.full)
                it.textBreakStrategy(BreakStrategy.highQuality)
            }
            .textOnTextChanged {
                Debug.i("text:'$it'")
            }
            .layoutMargin(16)
    }
}

@Suppress("ClassName", "unused")
class __AdaptUITextSample(context: Context, attributeSet: AttributeSet) :
    PreviewLayout(context, attributeSet) {
    init {
        AdaptUITextSample().render(this)
    }
}