package io.noties.adapt.sample.samples.adaptui

import io.noties.adapt.sample.annotation.AdaptSample
import io.noties.adapt.sample.explore.DrawableState
import io.noties.adapt.sample.explore.newDrawable2
import io.noties.adapt.sample.explore.onDrawableStateChange
import io.noties.adapt.ui.LayoutParams
import io.noties.adapt.ui.ViewFactory
import io.noties.adapt.ui.background
import io.noties.adapt.ui.element.Text
import io.noties.adapt.ui.element.VStack
import io.noties.adapt.ui.element.textColor
import io.noties.adapt.ui.element.textGravity
import io.noties.adapt.ui.element.textSize
import io.noties.adapt.ui.layoutFill
import io.noties.adapt.ui.layoutMargin
import io.noties.adapt.ui.noClip
import io.noties.adapt.ui.onClick
import io.noties.adapt.ui.padding
import io.noties.adapt.ui.shape.Capsule
import io.noties.adapt.ui.util.Gravity
import io.noties.debug.Debug

@AdaptSample(
    id = "20230513001821",
    title = "DrawableState",
    description = "Receive drawable state updates"
)
class AdaptUIDrawableStateSample : AdaptUISampleView() {

    /**
     * In order to receive all drawable states reliably a stateful drawable must be used
     * as a `background` or `foreground`. For example, default selectable drawable
     */
    override fun ViewFactory<LayoutParams>.body() {
        VStack {

            Text("Click me!")
                .textSize(16)
                .textColor(Colors.white)
                .textGravity(Gravity.center)
                .padding(12)
                .layoutMargin(16)
                .background(Capsule {
                    fill(Colors.primary)
                }.newDrawable2().stateful(setOf(DrawableState.pressed)))
//                .foreground(ReportStateDrawable(DrawableState.pressed))
                .onDrawableStateChange { textView, drawableState ->
                    Debug.e("state:$drawableState")
                    textView.clearAnimation()

                    val target = if (drawableState.pressed) 0.82F else 1F
                    textView.animate()
                        .alpha(target)
                        .setDuration(250L)
                        .start()
                }
                .onClick {
                    Debug.i("clicked")

                }

        }.layoutFill()
            .noClip()
    }
}