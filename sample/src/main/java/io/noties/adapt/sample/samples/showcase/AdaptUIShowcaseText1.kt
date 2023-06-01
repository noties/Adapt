package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textAllCaps
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textHideIfEmpty
import io.noties.adapt.ui.element.textItalic
import io.noties.adapt.ui.element.textSingleLine
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.element.textStrikeThrough
import io.noties.adapt.ui.element.textTypeface
import io.noties.adapt.ui.element.textUnderline
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.TypefaceStyle

@AdaptSample(
    id = "20230520122012",
    title = "[Showcase] AdaptUI <em>Text</em> element",
    description = "<em>TextView</em> and siblings &mdash; <em>EditText</em>, <em>Button</em>, etc",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseText1 : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            // All these `.text*` configurations are available for all TextView siblings
            Text("This is a TextView")
                // text size is scaled density pixels (sp), automatic conversion
                .textSize(21)
                // text color (both normal color and ColorStateList)
                .textColor(Color.BLACK)
                .textColor(ColorStateList.valueOf(Color.BLACK))
                // text gravity
                .textGravity(Gravity.center)
                // single line
                .textSingleLine()
                // typeface and style
                .textTypeface(Typeface.DEFAULT_BOLD, TypefaceStyle.italic)
                // apply bold style
                .textBold()
                // apply italic style
                .textItalic()
                // apply underline
                .textUnderline()
                // apply strike-through
                .textStrikeThrough()
                // make text in ALL CAPS
                .textAllCaps()
                // this will hide (ake GONE) textView if its text isEmpty
                .textHideIfEmpty()

        }.layoutFill()
    }
}

private class PreviewAdaptUIShowcaseText1(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseText1()
}