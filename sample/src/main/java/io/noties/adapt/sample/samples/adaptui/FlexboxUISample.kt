package io.noties.adapt.sample.samples.adaptui

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.flex.AlignContent
import io.noties.adapt.ui.flex.AlignItems
import io.noties.adapt.ui.flex.Flex
import io.noties.adapt.ui.flex.FlexDirection
import io.noties.adapt.ui.flex.FlexWrap
import io.noties.adapt.ui.flex.JustifyContent
import io.noties.adapt.ui.flex.flexAlignContent
import io.noties.adapt.ui.flex.flexAlignItems
import io.noties.adapt.ui.flex.flexDirection
import io.noties.adapt.ui.flex.flexJustifyContent
import io.noties.adapt.ui.flex.flexWrap
import io.noties.adapt.ui.flex.layoutFlexGrow
import io.noties.adapt.ui.flex.layoutFlexWrapBefore
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.util.Gravity

@AdaptSample(
    id = "20220612133759",
    title = "Typed Flexbox layout build with adapt-ui extensions",
    tags = ["flexbox", "adapt-ui"]
)
class FlexboxUISample : SampleView() {

    override val layoutResId: Int = R.layout.view_sample_frame

    override fun render(view: View) {
        val viewGroup = view.findViewById<ViewGroup>(R.id.frame_layout)

        ViewFactory.addChildren(viewGroup) {
            Flex {

                Text("1")
                    .background(RectangleShape {
                        fill(Color.RED)
                    })
                    .padding(16)

                Text("2")
                    .background(RectangleShape {
                        fill(Color.GREEN)
                    })
                    .padding(12)
                    .layoutFlexGrow(1F)

                Text("3")
                    .background(RectangleShape {
                        fill(Color.YELLOW)
                    })
                    .layoutFlexWrapBefore(true)
                    .layoutFlexGrow(1F)
                    .textGravity(Gravity.center)

                Text("4")
                    .layoutFlexWrapBefore(true)

            }.flexDirection(FlexDirection.row)
                .flexJustifyContent(JustifyContent.center)
                .flexAlignItems(AlignItems.center)
                .flexAlignContent(AlignContent.center)
                .flexWrap(FlexWrap.wrap)
        }
    }
}