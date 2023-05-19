package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.ExploreShapeText2
import io.noties.adapt.sample.util.HtmlUtil
import io.noties.adapt.sample.util.PreviewUILayout
import io.noties.adapt.sample.util.withAlphaComponent
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.shape.Circle
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.shape.RoundedRectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20230515172204",
    title = "[Explore] Text Shape",
    "Shape that draws text",
    tags = ["shape"]
)
class AdaptUIShapeTextSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Text("!!!Text shape is available only since O (26)")
                    .textSize(18)
                    .textColor(Colors.black)
                    .layoutWrap()
                    .layoutGravity(Gravity.center)
                return@VStack
            }

            View()
                .layout(FILL, 256)
                .background(createTextShape1())

            View()
                .layout(FILL, 256)
                .background(createTextShape2())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTextShape1() = Rectangle {
        // background to indicate bounds
        fill(Colors.primary.withAlphaComponent(0.1F))

        add(ExploreShapeText2.Text {
            text("Hello there! ".repeat(10))
            textSize(21)
            textColor(Colors.white)
            textGravity(Gravity.center)
            textMaxLines(4)
            textShadow(4, color = Colors.orange)
            textBold()
            textItalic()
            textTypeface(Typeface.MONOSPACE)
            textUnderline()
            textStrikethrough()

            fill(Colors.yellow)
//            sizeRelative(0.5F, 0.5F, gravity = Gravity.center)
            padding(24)

            // children, unlike other shapes, are drawn BEFORE text content
            //  this allows creating proper backgrounds for text content, as
            //  children will receive actual text bounds
            add(RoundedRectangle(12) {
                // negative padding will grow around text
                padding(-16)
                fill(Colors.primary)
                shadow(Colors.black, 8)
                add(RoundedRectangle(12) {
                    padding(2)
                    stroke(Colors.yellow, 4, 8, 2)
                })
            })

            add(Circle {
                fill(Colors.accent)
            })
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTextShape2() = Rectangle {
        fill(Colors.orange.withAlphaComponent(0.1F))

        // orange text bounds
        add(ExploreShapeText2.Text {
            text("OLD")
            textSize(16)
            textGravity(Gravity.top.trailing)
            textRotation(45F)
            translate(x = 4, y = 0)
            textBold()
            textStrikethrough()

            padding(4)

            // would stroke all available bounds within which text is placed
            stroke(Colors.orange, 2)

            add(Rectangle {
                padding(-4)
                padding(top = -12)
                fill(Colors.primary.withAlphaComponent(0.42F))
                sizeRelative(3F)
                gravity(Gravity.center)
            })
        })

        // semi-black border
        add(ExploreShapeText2.Text {
            text("\uD83D\uDE0E")
            textSize(24)
            textGravity(Gravity.center)

            stroke(Colors.black.withAlphaComponent(0.4F), 2)

            add(RoundedRectangle(8) {
                fill(Colors.black)
                padding(-4)
                shadow(Colors.orange, 16)
            })
        })

        // accent stroke
        add(ExploreShapeText2.Text {
            text("A text is always a text <3")
            textSize(18)
            textTypeface(Typeface.MONOSPACE)
            textBold()
            textGravity(Gravity.leading.bottom)
            textGradient(LinearGradient.edges { leading to trailing }
                .setColors(0xFFff0000.toInt(), 0xFF00ff00.toInt(), 0xFF0000ff.toInt(), 0xFFff00ff.toInt(), 0xFFffff00.toInt()))
            textShadow(8)

            padding(12)
            stroke(Colors.accent)
            sizeRelative(1F, 0.25F)
            // NB! this gravity does not affect text gravity, all shape-related
            //  properties on text do affect only available bounds for text
            gravity(Gravity.bottom)

            add(Capsule {
                fill(Colors.white)
                padding(-12, -4)
                shadow(Colors.black, 12)
            })
        })

        // stroke with primary dashed
        add(ExploreShapeText2.Text {
            // accepts spannable content
            val text = kotlin.run {
                val html = "<b>Some <i>another</i></b> <u>time</u><sup><font color=red>234</font></sup>"
                HtmlUtil.fromHtml(html)
            }
            text(text)
            textSize(16)

            padding(16)
            stroke(Colors.primary, 2, 12, 4)
            sizeRelative(0.75F, 0.25F)
        })
    }
}

private class PreviewAdaptUIShapeTextSample(context: Context, attrs: AttributeSet?) :
    PreviewUILayout(context, attrs) {
    override fun ViewFactory<LayoutParams>.body() {
        with(AdaptUIShapeTextSample()) {
            body()
        }
    }
}