package io.noties.adapt.sample.samples.adaptui

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent
import io.noties.adapt.sample.R
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Rectangle
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
                    .background(Rectangle {
                        fill(Color.RED)
                    })
                    .padding(16)

                Text("2")
                    .background(Rectangle {
                        fill(Color.GREEN)
                    })
                    .padding(12)
                    .layoutFlexGrow(1F)

                Text("3")
                    .background(Rectangle {
                        fill(Color.YELLOW)
                    })
                    .layoutFlexWrapBefore(true)
                    .layoutFlexGrow(1F)
                    .textGravity(Gravity.center)

                Text("4")
                    .layoutFlexWrapBefore(true)

            }.flexDirection(FlexDirection.ROW)
                .flexJustifyContent(JustifyContent.CENTER)
                .flexAlignItems(AlignItems.CENTER)
                .flexAlignContent(AlignContent.CENTER)
                .flexWrap(FlexWrap.WRAP)
        }
    }
}