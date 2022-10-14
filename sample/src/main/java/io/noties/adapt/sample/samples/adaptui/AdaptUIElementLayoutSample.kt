package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.util.PreviewLayout
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.addChildren
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.HStack
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VScroll
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.View
import io.noties.adapt.ui.element.ZStack
import io.noties.adapt.ui.element.text
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textFont
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layout
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.layoutWeight
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Rectangle
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20221006152015",
    title = "AdaptUI layout attributes",
    tags = ["adapt-ui", "ui-layout"]
)
class AdaptUIElementLayoutSample : SampleView() {
    override val layoutResId: Int
        get() = R.layout.view_sample_frame

    override fun render(view: View) {
        ViewFactory.addChildren(view as ViewGroup) {
            VScroll {
                VStack {

                    // usage of specific to LinearLayout attributes
                    Header("LinearLayout attributes")
                    LinearLayoutSample()

                    ZStack {
                        Header("FrameLayout attributes")
                    }.layout(FILL, 128)
                    FrameLayoutSample()
                }
            }
        }
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.Header(text: String) {
        Text(text)
            .text(text)
            .textFont(Typeface.DEFAULT_BOLD)
            .textSize(16)
            .textColor(Colors.black)
            .padding(horizontal = 16, vertical = 8)
    }

    @Suppress("FunctionName")
    private fun ViewFactory<LayoutParams>.LinearLayoutSample() {

        fun strokeBackground() = Rectangle {
            stroke(Colors.black, 1)
            padding(1)
        }

        VStack {

            HStack {
                Text("HStack #1")
            }.background(strokeBackground())
                .layout(FILL, 0, 1F)

            ZStack {

                VStack {

                    Text("VStack")
                        .background(strokeBackground())
                        .layout(FILL, 0)
                        // standalone layoutWeight is also available
                        .layoutWeight(1F)

                    HStack {

                        Text("HStack #2")
                            .layout(0, FILL, 1F)

                        View()
                            .layout(12, 12)
                            .layoutMargin(4)
                            // by default HStack has CENTER_VERTICAL
                            .layoutGravity(Gravity.bottom)
                            .background(strokeBackground())

                    }.layout(FILL, 32)
                        .background(strokeBackground())

                }.layoutFill()
                    .layoutMargin(4)

            }.background(strokeBackground())
                .layout(FILL, 0, 3F)

        }.layout(FILL, 128)
            .padding(4)
            .background(strokeBackground())
    }

    @Suppress("FunctionName")
    fun ViewFactory<LayoutParams>.FrameLayoutSample() {
        ZStack {

            Text("START|TOP")
                .layoutWrap()
                .layoutGravity(Gravity.leading.top)
                .layoutMargin(top = 8)

            Text("END|TOP")
                .layoutWrap()
                .layoutGravity(Gravity.trailing.top)
                .layoutMargin(trailing = 8)

            Text("CENTER")
                .layoutWrap()
                .layoutGravity(Gravity.center)

            Text("CENTER_HORIZONTAL|BOTTOM")
                .layoutWrap()
                .layoutGravity(Gravity.bottom.center)
                .layoutMargin(bottom = 8)

        }.layout(FILL, 128)
            .padding(16)
            .background(Rectangle {
                stroke(Colors.black)
                padding(8)
            })
    }
}

@Suppress("ClassName", "unused")
class __AdaptUIElementLayoutSample(
    context: Context,
    attributeSet: AttributeSet
) : PreviewLayout(context, attributeSet) {
    init {
        AdaptUIElementLayoutSample().render(this)
    }
}