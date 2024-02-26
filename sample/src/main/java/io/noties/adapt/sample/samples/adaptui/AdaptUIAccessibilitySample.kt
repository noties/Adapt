package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.ui.color.orange
import io.noties.adapt.sample.ui.color.primary
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.ViewElement
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.accessibilityDescription
import io.noties.adapt.ui.accessibilityLabelFor
import io.noties.adapt.ui.accessibilityTraversalBefore
import io.noties.adapt.ui.app.color.Colors
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Spacer
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.TextInput
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.focusable
import io.noties.adapt.ui.gradient.LinearGradient
import io.noties.adapt.ui.ifAvailable
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.util.InputType

@AdaptSample(
    id = "20221009162741",
    title = "AdaptUI - Accessibility",
    tags = ["adapt-ui", "ui-accessibility"]
)
class AdaptUIAccessibilitySample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {

        fun hex(color: Int) = String.format("#%08X", color)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        listOf(
            0,
            0xFFFFFF,
            0xFFFFFFFF.toInt(),
            0x80FFFFFF.toInt(),
            0x01FFFFFF
        ).forEach {
            paint.color = it
            println(
                "${hex(it)}, paint.color:${hex(paint.color)}, paint.alpha:${paint.alpha}"
            )
            paint.alpha = 128
            println("paint.color:${hex(paint.color)}, paint.alpha:${paint.alpha}")
        }

        ViewFactory.addChildren(view as ViewGroup) {
            VScroll {
                VStack {

                    val inputGroup = HStack {

                        // input element that would be added later
                        lateinit var input: ViewElement<out View, *>

                        Text("Your name")
                            .padding(4)
                            .layoutWrap()
                            .accessibilityLabelFor { input }

                        Spacer()

                        input = TextInput(InputType.text.personName)
                            .textSize(16)
                            .layoutWrap()

                    }.focusable(focusable = true, focusableInTouchMode = false)
                        .padding(horizontal = 16, vertical = 8)

                    Text("Text")
                        .accessibilityDescription("A text view")
                        .padding(16)
                        .ifAvailable(Build.VERSION_CODES.LOLLIPOP_MR1) {
                            it.accessibilityTraversalBefore { inputGroup }
                        }

                    View()
                        .layout(FILL, 256)
                        .background(RectangleShape {
//                            alpha(0.5F)
                            fill(
                                LinearGradient.edges { top to bottom }
                                    .setColors(Colors.primary, Colors.orange)
                            )
                        })
                }
            }.layoutFill()
        }
    }
}

// take original reference, obtain parent,
// replace this one with required new view
//fun <V: TextView, LP: LayoutParams> ViewElement<V, LP>.textMarquee(
//
//): ViewElement<V, LP> = onView {
//    val frame = FrameLayout(context)
//    frame.layoutParams = layoutParams
//    val parent = this.parent as ViewGroup
//    val index = parent.indexOfChild(this)
//    parent.removeViewAt(index)
//    frame.addView(this)
//    parent.addView(frame, index)
//}
//

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIAccessibilitySample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIAccessibilitySample()
}