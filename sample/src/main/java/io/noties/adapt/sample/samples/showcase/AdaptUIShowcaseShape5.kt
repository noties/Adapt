package io.noties.adapt.sample.samples.showcase

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.samples.adaptui.AdaptUISamplePreview
import io.noties.adapt.sample.samples.adaptui.AdaptUISampleView
import io.noties.adapt.sample.util.html
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.JustificationMode
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.shape.ShapeFactory
import io.noties.adapt.ui.shape.Text
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230601102605",
    title = "[Showcase] AdaptUI, Shape #5",
    description = "<em>TextShape</em>, only Android.O+",
    tags = ["adapt-ui", "showcase"]
)
class AdaptUIShowcaseShape5 : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Text("TextShape is only available for Android.0+")
                .textBold()
                .layoutWrap()
                .textGravity(Gravity.center)
                .textSize(22)
            return
        }

        View()
            .layoutFill()
            .background(textShape())
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun textShape() = ShapeFactory.create {
    Rectangle {
        padding(24)

        // TextShape accepts CharSequence and Spannable content
        //  TextShape is backed by StaticLayout
        Text {
            text(textContent())
            textSize(18)
            // limit maximum lines
            textMaxLines(9)
            // text gradient
            textGradient(LinearGradient.edges { leading to trailing }
                .setColors(Color.MAGENTA, Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED))
            // justification mode
            textJustificationMode(JustificationMode.interWord)
            // by default uses text color (or gradient if present)
            textShadow(4)
            // line spacing
            textLineSpacing(mult = 0.8F)

            // child is supplied actual text content bounds and it is drawn BEFORE
            RoundedRectangle(12) {
                // negative padding to grow around text
                padding(-8)
                fill(Color.BLACK)
                stroke(Color.GREEN, 8, 16, 4)
            }
        }
    }
}

private fun textContent() = SpannableStringBuilder().apply {
    val text = "<h1>Heading!</h1>\n<b>Some bold and <i>italic</i>, " +
            "continue <del>going</del></b> <u>and much much more!</u> " +
            "and event something else! So interesting to see it, " +
            "just text..."
    repeat(10) {
        if (isNotEmpty()) {
            append("\n\n")
        }
        append(text.html())
    }
}

private class PreviewAdaptUIShowcaseShape5(context: Context, attrs: AttributeSet?) :
    AdaptUISamplePreview(context, attrs) {
    override val sample: AdaptUISampleView
        get() = AdaptUIShowcaseShape5()
}