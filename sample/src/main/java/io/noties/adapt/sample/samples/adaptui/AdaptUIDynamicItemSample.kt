package io.noties.adapt.sample.samples.adaptui

import android.content.Context
import android.util.AttributeSet
import io.noties.adapt.sample.SampleView
import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.ExploreOverlay.overlay
import io.noties.adapt.sample.explore.ExploreOverlay.overlay3
import io.noties.adapt.sample.util.Preview
import io.noties.adapt.sample.util.PreviewSampleView
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textBold
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.foregroundDefaultSelectable
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutGravity
import io.noties.adapt.ui.layoutWrap
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.onViewPreDrawOnce
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.CircleShape
import io.noties.adapt.ui.shape.RectangleShape
import io.noties.adapt.ui.shape.StatefulShape
import io.noties.adapt.ui.util.Gravity
import io.noties.adapt.ui.util.dip
import io.noties.adapt.ui.util.withAlphaComponent
import io.noties.debug.Debug

@AdaptSample(
    id = "20230715105856",
    title = "[Explore] Dynamic Item",
    description = "Direct usage of an <em>Item</em> inside ViewFactory builder",
    tags = ["adapt-ui"]
)
class AdaptUIDynamicItemSample : AdaptUISampleView() {
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text("Some text")
                .textSize(21)
                .textGravity(Gravity.center)
                .textBold()
                .padding(16)
                .overlay(StatefulShape.drawable {
                    setPressed(CircleShape().fill(Colors.orange))
                    setDefault(RectangleShape().fill(Colors.accent))
                })
                .foregroundDefaultSelectable()
                .onClick { Debug.i("clicked") }

//            Item(CardItem("D", Colors.orange, "This is item in layout"))
//                .padding(48)
//                .layoutMargin(48)

            Text("Some other text")
                .padding(16)

        }.layoutFill()
            .overlay3 {
                Text("NEW")
                    .layoutWrap()
                    .background(Colors.orange.withAlphaComponent(0.2F))
                    .layoutGravity(Gravity.top.trailing)
                    .padding(8)
                    .onViewPreDrawOnce {
//                        it.pivotX = -(it.width * 0.4F)
//                        it.pivotY = it.height * 0.87F
//                        it.pivotX = 0F
//                        it.pivotY = 0F
                        it.pivotX = it.width.toFloat()
                        it.pivotY = it.height.toFloat()
                        it.translationX -= 12.dip.toFloat()
                        it.translationY += 8.dip.toFloat()
                        it.rotation = 45F
                    }
            }
    }
}

@Preview
@Suppress("ClassName", "unused")
private class Preview__AdaptUIDynamicItemSample(
    context: Context,
    attrs: AttributeSet?
) : PreviewSampleView(context, attrs) {
    override val sampleView: SampleView
        get() = AdaptUIDynamicItemSample()
}